package com.umb.cs682.projectlupus.alarms;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.umb.cs682.projectlupus.R;
import com.umb.cs682.projectlupus.activities.medicineAlert.MedicinePopUp;
import com.umb.cs682.projectlupus.config.LupusMate;
import com.umb.cs682.projectlupus.exceptions.AppException;
import com.umb.cs682.projectlupus.service.MedicineService;
import com.umb.cs682.projectlupus.service.ReminderService;
import com.umb.cs682.projectlupus.util.AlarmUtil;
import com.umb.cs682.projectlupus.util.Constants;

import java.util.Calendar;
import java.util.TimeZone;

public class MedicineAlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "receiver.medicine";
    private NotificationManager nm;

    private ReminderService reminderService = LupusMate.getReminderService();
    private MedicineService medicineService = LupusMate.getMedicineService();

    int reminderID;
    int requestCode;
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean snoozed = intent.getBooleanExtra(Constants.SNOOZED, false);
        requestCode = intent.getIntExtra(Constants.REQUEST_CODE, -1);
        reminderID = intent.getIntExtra(Constants.REMINDER_ID, -1);
        showNotification(context);

        if(!snoozed) {
            medicineService.incrementTotalRemindedCount(reminderService.getMedReminder(reminderID).getMedId());
            try {
                reScheduleAlarm(context, intent);
            } catch (AppException e) {
                e.printStackTrace();
            }
        }
    }

    private void showNotification(Context context){
        nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, MedicinePopUp.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Constants.REMINDER_ID, reminderID);
        int notifRequestCode = 6000 + requestCode;
        PendingIntent pendingIntent = PendingIntent.getActivity(context, notifRequestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder notificationBuilder = new Notification.Builder(context)
                .setContentIntent(pendingIntent)
                .setContentText(context.getString(R.string.popup_title))
                .setSmallIcon(R.drawable.ic_med_notification_icon)
                .setWhen(System.currentTimeMillis())
                .setTicker(context.getString(R.string.popup_title))
                .setContentTitle(context.getString(R.string.notification_med_text))
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            notificationBuilder.setColor(context.getResources().getColor(R.color.darkPurple));
        }
        Notification notification = notificationBuilder.build();
        notification.deleteIntent = PendingIntent.getBroadcast(context, notifRequestCode, getDeleteIntent(context), 0); // change reminder status to skip if notification is deleted
        Log.i(TAG, "Notified, reminder ID = "+reminderID);
        nm.notify(notifRequestCode, notification);
    }

    private Intent getDeleteIntent(Context context){
        Intent deleteIntent = new Intent(context, DeleteNotificationReceiver.class);
        deleteIntent.putExtra(Constants.REMINDER_ID, reminderID);
        deleteIntent.setAction("delete");
        return deleteIntent;
    }

    private void reScheduleAlarm(Context context, Intent intent) throws AppException {
        long currentStartTime = intent.getLongExtra(Constants.START_TIME, -1);
        Calendar futureCal = Calendar.getInstance(TimeZone.getDefault());
        Calendar currentCal = Calendar.getInstance(TimeZone.getDefault());
        currentCal.setTimeInMillis(currentStartTime);

        String alarmInterval = intent.getStringExtra(Constants.ALARM_INTERVAL);
        switch(alarmInterval){
            case Constants.DAILY:
                return;
            case Constants.WEEKLY:
                int dayOfWeek = intent.getIntExtra(Constants.DAY_OF_WEEK, -1);
                if(dayOfWeek != -1){
                    futureCal = getNextWeekStartTime(currentCal);
                }
                break;
            case Constants.MONTHLY:
                int dayOfMonth = intent.getIntExtra(Constants.DAY_OF_MONTH, -1);
                if(dayOfMonth != -1){
                    futureCal = getNextMonthStartTime(currentCal);
                }
                break;
        }
        if(reminderID != -1 && requestCode != -1) {
            AlarmUtil.setAlarm(context, requestCode, reminderID, Constants.MED_REMINDER, alarmInterval, futureCal);
            Log.i(TAG, "Rescheduled alarm, reminder ID = "+reminderID);
        }else{
            throw new AppException("Could not find request code in intent");
        }
    }

    private Calendar getNextMonthStartTime(Calendar cal) {
        int currentMonth = cal.get(Calendar.MONTH);
        currentMonth++;
        if(currentMonth > Calendar.DECEMBER){
            currentMonth = Calendar.JANUARY;
            cal.set(Calendar.YEAR, cal.get(Calendar.YEAR)+1);
        }
        cal.set(Calendar.MONTH, currentMonth);
        return cal;
    }

    private Calendar getNextWeekStartTime(Calendar cal) {
        long timeInMillis = cal.getTimeInMillis()+ AlarmManager.INTERVAL_DAY*7;
        cal.setTimeInMillis(timeInMillis);
        return cal;
    }

}

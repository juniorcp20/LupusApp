package com.umb.cs682.projectlupus.alarms;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.umb.cs682.projectlupus.R;
import com.umb.cs682.projectlupus.activities.medicineAlert.MedicinePopUp;
import com.umb.cs682.projectlupus.exceptions.AppException;
import com.umb.cs682.projectlupus.util.AlarmUtil;
import com.umb.cs682.projectlupus.util.Constants;

import java.util.Calendar;
import java.util.TimeZone;

public class MedicineAlarmReceiver extends BroadcastReceiver {
    private NotificationManager nm;
    @Override
    public void onReceive(Context context, Intent intent) {
        showNotification(context);
        try {
            reScheduleAlarm(context, intent);
        }catch(AppException e){
            e.printStackTrace();
        }
    }

    private void showNotification(Context context){
        nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, MedicinePopUp.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        Notification notification = new Notification.Builder(context)
                .setContentIntent(pendingIntent)
                .setContentText("LupusMate Reminder")
                .setSmallIcon(R.drawable.logo_24)
                .setWhen(System.currentTimeMillis())
                .setTicker("LupusMate Reminder")
                .setContentTitle("Hey! Time to take your medicines!")
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .build();
        nm.notify(1, notification);
    }

    private void reScheduleAlarm(Context context, Intent intent) throws AppException {
        int reminderID = intent.getIntExtra(Constants.REMINDER_ID, -1);
        int requestCode = intent.getIntExtra(Constants.REQUEST_CODE, -1);
        long currentStartTime = intent.getLongExtra(Constants.START_TIME, -1);
        //long nextStartTime = 0;
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
           // AlarmUtil.setOneShotAlarm(context, Constants.MED_REMINDER, reminderID, requestCode, alarmInterval, nextStartTime);
            AlarmUtil.setAlarm(context, requestCode, reminderID, Constants.MED_REMINDER, alarmInterval, futureCal);
        }else{
            throw new AppException("Could not find request code in intent");
        }
    }

    //private long getNextMonthStartTime(Calendar cal) {
    private Calendar getNextMonthStartTime(Calendar cal) {
        //cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        int currentMonth = cal.get(Calendar.MONTH);
        currentMonth++;
        if(currentMonth > Calendar.DECEMBER){
            // alright, reset month to jan and forward year by 1 e.g fro 2013 to 2014
            currentMonth = Calendar.JANUARY;
            // Move year ahead as well
            cal.set(Calendar.YEAR, cal.get(Calendar.YEAR)+1);
        }
        cal.set(Calendar.MONTH, currentMonth);
        // get the maximum possible days in this month
        //int maximumDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        //return cal.getTimeInMillis();// + AlarmManager.INTERVAL_DAY*maximumDays;
        return cal;
    }

    //private long getNextWeekStartTime(Calendar cal) {
    private Calendar getNextWeekStartTime(Calendar cal) {
        //cal.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        //return cal.getTimeInMillis()+ AlarmManager.INTERVAL_DAY*7;
        long timeInMillis = cal.getTimeInMillis()+ AlarmManager.INTERVAL_DAY*7;
        cal.setTimeInMillis(timeInMillis);
        return cal;
    }

}

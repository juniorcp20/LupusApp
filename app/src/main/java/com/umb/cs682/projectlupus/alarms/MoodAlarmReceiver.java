package com.umb.cs682.projectlupus.alarms;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.umb.cs682.projectlupus.R;
import com.umb.cs682.projectlupus.activities.moodAlert.MoodPopUp;
import com.umb.cs682.projectlupus.config.LupusMate;
import com.umb.cs682.projectlupus.service.ReminderService;
import com.umb.cs682.projectlupus.util.Constants;

public class MoodAlarmReceiver extends BroadcastReceiver{
    private static final String TAG = "receiver.mood";
    private NotificationManager nm;
    private ReminderService reminderService;

    int reminderID;
    int requestCode;

    @Override
    public void onReceive(Context context, Intent intent) {
        final LupusMate lupusMate = (LupusMate) context.getApplicationContext();
        reminderService = lupusMate.getReminderService();
        reminderID = intent.getIntExtra(Constants.REMINDER_ID, -1);
        requestCode = intent.getIntExtra(Constants.REQUEST_CODE, -1);
        reminderService.updateMoodReminderStatus(reminderID, Constants.REM_STATUS_PENDING);
        showNotification(context);
    }

    private void showNotification(Context context){
        nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(context, MoodPopUp.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Constants.REMINDER_ID, reminderID);
        int notifRequestCode = 6000 + requestCode;
        PendingIntent pendingIntent = PendingIntent.getActivity(context, notifRequestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder notificationBuilder = new Notification.Builder(context)
                .setContentIntent(pendingIntent)
                .setContentText(context.getString(R.string.popup_title))
                .setSmallIcon(R.drawable.ic_mood_notification_icon)
                .setWhen(System.currentTimeMillis())
                .setTicker(context.getString(R.string.popup_title))
                .setContentTitle(context.getString(R.string.popup_mood_text))
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            notificationBuilder.setColor(context.getResources().getColor(R.color.darkPurple));
        }

        Notification notification = notificationBuilder.build();
        notification.deleteIntent = PendingIntent.getBroadcast(context, notifRequestCode, getDeleteIntent(context), 0);
        Log.i(TAG, "Notified. reminder ID = "+reminderID);
        nm.notify(notifRequestCode, notification);
    }

    private Intent getDeleteIntent(Context context){
        Intent deleteIntent = new Intent(context, DeleteNotificationReceiver.class);
        deleteIntent.putExtra(Constants.REMINDER_ID, reminderID);
        deleteIntent.setAction("delete");
        return deleteIntent;
    }
}

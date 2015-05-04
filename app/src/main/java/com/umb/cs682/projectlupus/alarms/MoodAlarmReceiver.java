package com.umb.cs682.projectlupus.alarms;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.umb.cs682.projectlupus.R;
import com.umb.cs682.projectlupus.activities.moodAlert.MoodPopUp;
import com.umb.cs682.projectlupus.config.AppConfig;
import com.umb.cs682.projectlupus.service.ReminderService;
import com.umb.cs682.projectlupus.util.Constants;

/**
 * Created by Nithya Kiran on 4/30/2015.
 */
public class MoodAlarmReceiver extends BroadcastReceiver{
    private NotificationManager nm;
    private ReminderService reminderService = AppConfig.getReminderService();
    @Override
    public void onReceive(Context context, Intent intent) {
        int reminderID = intent.getIntExtra(Constants.REQUEST_CODE, -1);
        reminderService.updateMoodReminderStatus(reminderID, Constants.REM_STATUS_PENDING);
        showNotification(context, intent);//reminderID);
    }

    private void showNotification(Context context, Intent alarmIntent){//int reminderID) {
        nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(context, MoodPopUp.class);
        //intent.putExtra(Constants.REMINDER_ID, reminderID);
        intent.putExtras(alarmIntent);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

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
        nm.notify(1, notification);
    }
}

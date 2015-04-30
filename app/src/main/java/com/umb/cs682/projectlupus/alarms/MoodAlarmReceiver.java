package com.umb.cs682.projectlupus.alarms;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.umb.cs682.projectlupus.R;
import com.umb.cs682.projectlupus.activities.moodAlert.MoodPopUp;

/**
 * Created by Nithya Kiran on 4/30/2015.
 */
public class MoodAlarmReceiver extends BroadcastReceiver{
    private NotificationManager nm;
    @Override
    public void onReceive(Context context, Intent intent) {
        showNotification(context);
    }

    private void showNotification(Context context) {
        nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, MoodPopUp.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        Notification notification = new Notification.Builder(context)
                .setContentIntent(pendingIntent)
                .setContentText("LupusMate Reminder")
                .setSmallIcon(R.drawable.logo_24)
                .setWhen(System.currentTimeMillis())
                .setTicker("LupusMate Reminder")
                .setContentTitle("Hey! How are you feeling?")
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .build();
        nm.notify(1, notification);
    }
}

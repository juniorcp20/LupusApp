package com.umb.cs682.projectlupus.activities.medicineAlert;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.umb.cs682.projectlupus.R;
/**
 * Created by mark on 4/8/15.
 */
public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        createNotification(context, "times up", "5 seconds Has Passed",
                "Alert");

    }

    private void createNotification(Context context, String msg, String msgText, String msgAlert) {
        PendingIntent notificIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MedicineAlert.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.clock_icon)
                .setContentTitle(msg)
                .setTicker(msgAlert)
                .setContentText(msgText);
        mBuilder.setContentIntent(notificIntent);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1,mBuilder.build());



    }
}

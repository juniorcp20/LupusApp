package com.umb.cs682.projectlupus.alarms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.umb.cs682.projectlupus.config.LupusMate;
import com.umb.cs682.projectlupus.service.ReminderService;
import com.umb.cs682.projectlupus.util.Constants;

public class DeleteNotificationReceiver extends BroadcastReceiver {
    private static final String TAG = "receiver.deleteNotif";

    private int reminderID;
    private ReminderService reminderService = LupusMate.getReminderService();

    public DeleteNotificationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        reminderID = intent.getIntExtra(Constants.REMINDER_ID, -1);
        reminderService.updateReminderStatus(reminderID, Constants.REM_STATUS_SKIP);
        Log.i(TAG, "Notification cleared, setting status SKIP to reminder ID = "+reminderID);
    }
}

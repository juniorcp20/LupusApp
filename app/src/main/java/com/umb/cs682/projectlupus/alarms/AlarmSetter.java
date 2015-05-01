package com.umb.cs682.projectlupus.alarms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmSetter extends BroadcastReceiver {
    public AlarmSetter() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, AlarmSetterService.class);
        service.setAction(AlarmSetterService.CREATE);
        context.startService(service);
    }
}

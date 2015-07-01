package com.umb.cs682.projectlupus.alarms;

/**
 * Created by Junior on 7/1/2015.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.umb.cs682.projectlupus.service.MQTTService;

public class BootReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d(getClass().getCanonicalName(), "onReceive");
        context.startService(new Intent(context, MQTTService.class));
    }
}

package com.umb.cs682.projectlupus.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;


import com.umb.cs682.projectlupus.db.dao.ActivitySenseDao;
import com.umb.cs682.projectlupus.domain.ActivitySenseBO;
import com.umb.cs682.projectlupus.util.DateUtil;

import java.util.Date;

import de.greenrobot.dao.query.CountQuery;
import de.greenrobot.dao.query.DeleteQuery;

public class ActivitySenseService {
    private static final String TAG = "service.activitySense";
    private static final String INTENT_FILTER = "com.umb.cs682.projectlupus.service.ActivitySense";
    private static int stepCount;
    private boolean isBound;

    private Context context;
    private ActivitySenseDao activitySenseDao;

    private PedometerService pedometerService;
    private BroadcastReceiver receiver;
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;

    public ActivitySenseService(Context context, ActivitySenseDao activitySenseDao){
        this.activitySenseDao = activitySenseDao;
        this.context = context;
        Intent intent = new Intent(context, PedometerService.class);
        context.bindService(intent, pedometerConnection, Context.BIND_AUTO_CREATE);
    }

    /* Pedometer Service Operations */

    private void bindPedometerService(){
        Intent intent = new Intent(context, PedometerService.class);
        context.bindService(intent, pedometerConnection, Context.BIND_AUTO_CREATE);
    }

    public void startStopPedometer(boolean startStop){
        if(!isBound){
            bindPedometerService();
        }
        pedometerService.startStopPedometer(startStop);
    }

    public void onStartPedometer(){
        if(!isBound){
            bindPedometerService();
        }
        pedometerService.onStart();
    }

    public void onPausePedometer(){
        if(!isBound){
            bindPedometerService();
        }
        pedometerService.onPause();
    }
    public void setSensitivity(int sensitivity){
        if(!isBound){
            bindPedometerService();
        }
        pedometerService.setSensitivity(sensitivity*10);
    }

    public int getSensitivity(){
        if(!isBound){
            bindPedometerService();
        }
        return (pedometerService.getSensitivity())/10;
    }

    public void setHandler(Handler handler) {
        pedometerService.setHandler(handler);
    }

    public void unsetHandler(){
        pedometerService.unsetHandler();
    }

    public int getCurrentStepCount(){
        try {
            stepCount = pedometerService.getStepCount();
        }catch(Exception e){
            stepCount = 0;
        }
        return stepCount;
    }

    private ServiceConnection pedometerConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PedometerService.ActivitySenseBinder binder = (PedometerService.ActivitySenseBinder) service;
            pedometerService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
            pedometerService = null;
        }
    };

    /*Alarm Manager Setup*/
    public void startAlarm(){
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), AlarmManager.INTERVAL_HOUR, pendingIntent);
    }

    public void stopAlarm(){
        alarmManager.cancel(pendingIntent);
        context.unregisterReceiver(receiver);
    }

    public void setupAlarm(){
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //todo remove the toast
                Toast.makeText(context, "Saving Data!", Toast.LENGTH_SHORT).show();
                addActSenseData(new Date());
            }
        };

        context.registerReceiver(receiver, new IntentFilter(INTENT_FILTER));
        pendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(INTENT_FILTER),0);
        alarmManager = (AlarmManager)(context.getSystemService(Context.ALARM_SERVICE));
    }


    /* Database Operations*/

    public int getStoredStepCount(Date date){
        return getActSenseDatabyDate(date).getStepCount();
    }

    public void addActSenseData(Date date){
        ActivitySenseBO bo;
        Log.i(TAG, "Saved to DB");
        CountQuery query = activitySenseDao.queryBuilder().where(ActivitySenseDao.Properties.Date.eq(DateUtil.toDate(date))).buildCount();
        if(!(query.count() == 0)) {
            bo = getActSenseDatabyDate(date);
            bo.setStepCount(getCurrentStepCount());
            activitySenseDao.update(bo);
        }else{
            bo = new ActivitySenseBO(null, getCurrentStepCount(), DateUtil.toDate(date));
            activitySenseDao.insert(bo);
        }
    }

    public ActivitySenseBO getActSenseDatabyDate(Date date){
        Log.i(TAG, "Retrieving from DB");
        ActivitySenseBO bo = activitySenseDao.queryBuilder().where(ActivitySenseDao.Properties.Date.eq(DateUtil.toDate(date))).uniqueOrThrow();
        return bo;
    }

    public void deleteData(Date date){
        Log.i(TAG, "Deleting Data");
        DeleteQuery query = activitySenseDao.queryBuilder().where(ActivitySenseDao.Properties.Date.eq(DateUtil.toDate(date))).buildDelete();
        query.executeDeleteWithoutDetachingEntities();
    }
}

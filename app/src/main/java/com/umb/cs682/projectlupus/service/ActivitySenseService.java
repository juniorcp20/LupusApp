package com.umb.cs682.projectlupus.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import com.umb.cs682.projectlupus.db.dao.ActivitySenseDao;

public class ActivitySenseService {
    private static final String TAG = "projectlupus.service";
    private Context context;
    private PedometerService pedometerService;

    private ActivitySenseDao activitySenseDao;
    private boolean isBound;
    private static int stepCount;

    public ActivitySenseService(ActivitySenseDao activitySenseDao, Context context){
        this.activitySenseDao = activitySenseDao;
        this.context = context;
        Intent intent = new Intent(context, PedometerService.class);
        context.bindService(intent, pedometerConnection, Context.BIND_AUTO_CREATE);
    }

    private void bindPedometerService(){
        Intent intent = new Intent(context, PedometerService.class);
        context.bindService(intent, pedometerConnection, Context.BIND_AUTO_CREATE);
    }

    public void startStopPedometer(boolean startStop){
        if(!isBound){
            bindPedometerService();
        }
        pedometerService.startStopPedometer(startStop);
        /*if(!startStop){
            context.unbindService(pedometerConnection);
           // isBound = false;
        }*/
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
}

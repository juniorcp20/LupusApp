package com.umb.cs682.projectlupus.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.jwetherell.pedometer.service.IStepService;
import com.jwetherell.pedometer.service.IStepServiceCallback;
import com.jwetherell.pedometer.service.StepDetector;
import com.jwetherell.pedometer.service.StepService;
import com.umb.cs682.projectlupus.R;
import com.umb.cs682.projectlupus.config.LupusMate;

public class PedometerService extends Service {
    private static final String TAG = "projectlupus.service";
    private final IBinder binder = new ActivitySenseBinder();
    private static PowerManager powerManager = null;
    private static PowerManager.WakeLock wakeLock = null;

    public static IStepService mService = null;
    public static Intent stepServiceIntent = null;
    private static Handler handler = null;

    private static int sensitivity = 10;
    private static boolean isBound = false;
    private static int stepCount = 0;
    private static TextView text;
    public PedometerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        LayoutInflater inflater = (LayoutInflater) LupusMate.getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.a_activity_sense, null);
        text = (TextView) layout.findViewById(R.id.tv_steps);

        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        return binder;
    }

    public void onStart(){
        if (!wakeLock.isHeld()){
            wakeLock.acquire();
        }
        if (stepServiceIntent == null) {
            Bundle extras = new Bundle();
            extras.putInt("int", 1);
            stepServiceIntent = new Intent(this, StepService.class);//doubtful about "this"
            stepServiceIntent.putExtras(extras);
        }
        /* Bind without starting the service
        try {
            bindStepService();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    public void onPause(){
        if (wakeLock.isHeld()){
            wakeLock.release();
        }
        unsetHandler();
        if(isBound) {
            unbindStepService();
        }
    }

    public void startStopPedometer(boolean startStop){
        boolean serviceIsRunning = false;
        try {
            if (mService != null) serviceIsRunning = mService.isRunning();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (startStop && !serviceIsRunning) {
            start();
        } else if (!startStop && serviceIsRunning) {
            stop();
        }
    }

    private void start() {
        Log.i(TAG, "start");
        startStepService();
        bindStepService();
    }

    private void stop() {
        Log.i(TAG, "stop");
        unbindStepService();
        stopStepService();
        try {
            isBound = mService.isRunning();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mService = null;
    }

    private void startStepService() {
        try {
            startService(stepServiceIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopStepService() {
        try {
            stopService(stepServiceIntent);
        } catch (Exception e) {
            // Ignore
        }
    }

    private void bindStepService() {
        try {
            bindService(stepServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unbindStepService() {
        try {
            unbindService(mConnection);
        } catch (Exception e) {
            // Ignore
        }
    }

    public int getStepCount(){
        return stepCount;
    }

    public void setSensitivity(int value){
        sensitivity = value;
        StepDetector.setSensitivity(value);
    }

    public int getSensitivity() {
        return sensitivity;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void unsetHandler(){
        this.handler = null;
    }

    private static final IStepServiceCallback.Stub mCallback = new IStepServiceCallback.Stub() {

        @Override
        public IBinder asBinder() {
            return mCallback;
        }

        @Override
        public void stepsChanged(int value) throws RemoteException {
            //Log.i(TAG, "Steps=" + value);
            stepCount = value;
            if(handler != null) {
                Message msg = handler.obtainMessage();
                msg.arg1 = value;
                handler.sendMessage(msg);
            }
        }
    };

    private static final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.i(TAG, "onServiceConnected()");
            mService = IStepService.Stub.asInterface(service);
            try {
                mService.registerCallback(mCallback);// should be called only when resume or restart.
                mService.setSensitivity(sensitivity);
                isBound = mService.isRunning();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            Log.i(TAG, "onServiceDisconnected()");
            try {
                isBound = mService.isRunning();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mService = null;
        }
    };

    public class ActivitySenseBinder extends Binder {
        public PedometerService getService(){
            return PedometerService.this;
        }
    }
}

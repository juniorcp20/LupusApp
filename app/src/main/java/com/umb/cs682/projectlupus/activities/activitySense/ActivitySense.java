package com.umb.cs682.projectlupus.activities.activitySense;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.jwetherell.pedometer.service.IStepService;
import com.jwetherell.pedometer.service.IStepServiceCallback;
import com.jwetherell.pedometer.service.StepDetector;
import com.jwetherell.pedometer.service.StepService;
import com.umb.cs682.projectlupus.R;
import com.umb.cs682.projectlupus.activities.medicineAlert.MedicineAlert;
import com.umb.cs682.projectlupus.config.LupusMate;
import com.umb.cs682.projectlupus.service.ActivitySenseService;
import com.umb.cs682.projectlupus.util.Constants;
import com.umb.cs682.projectlupus.util.SharedPreferenceManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class ActivitySense extends Activity {
    private static final String TAG = "activities.actSense";

    //private boolean isInit = SharedPreferenceManager.getBooleanPref(Constants.IS_FIRST_RUN);
    private boolean isInit = SharedPreferenceManager.isFirstRun();
    private String DEFAULT_SENSITIVITY = "1";
    private boolean isOn = false;
    private static boolean serviceIsRunning = false;

    private static int sensitivity = 1;
    private static int stepCount;
    private static ToggleButton startStopButton = null;
    private static TextView stepCountText = null;
    Spinner sensSpinner = null;

    private static ArrayList<String> sensArrayList = null;
    private static ArrayAdapter<CharSequence> modesAdapter = null;

    private static PowerManager powerManager = null;
    private static PowerManager.WakeLock wakeLock = null;

    public static IStepService mService = null;
    public static Intent stepServiceIntent = null;

   // private ActivitySenseService service = LupusMate.getActivitySenseService();

    private ActivitySenseService service;
    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_activity_sense);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        final LupusMate lupusMate = (LupusMate) getApplicationContext();
        service = lupusMate.getActivitySenseService();

        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Demo");

        if (stepServiceIntent == null) {
            Bundle extras = new Bundle();
            extras.putInt("int", 1);
            stepServiceIntent = new Intent(ActivitySense.this, StepService.class);
            stepServiceIntent.putExtras(extras);
        }

        stepCountText = (TextView) this.findViewById(R.id.tv_steps);
        sensSpinner = (Spinner) findViewById(R.id.sp_sensitivity);
        startStopButton = (ToggleButton) this.findViewById(R.id.tb_start_stop);
        startStopButton.setOnCheckedChangeListener(startStopListener);

        initSensitivitySpinner();
        service.setupAlarm();


        if(isInit){
            actionBar.setTitle(R.string.title_init_activity_sense);
        }else {
            actionBar.setTitle(R.string.title_activity_sense);
            startStopButton.setChecked(SharedPreferenceManager.getBooleanPref(Constants.ACTIVITY_SENSE_SETTING));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(isInit) {
            getMenuInflater().inflate(R.menu.m_action_next, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_next) {
            next();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!wakeLock.isHeld()) wakeLock.acquire();

        // Bind without starting the service
        try {
            bindService(stepServiceIntent, mConnection, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //service.onPausePedometer();
        if (wakeLock.isHeld()) wakeLock.release();

        unbindStepService();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void initSensitivitySpinner(){
        String savedSensitivity = String.valueOf(SharedPreferenceManager.getIntPref(Constants.SENSITIVITY_VALUE));
        sensitivity =  savedSensitivity.equals("-1") ? Integer.parseInt(DEFAULT_SENSITIVITY) : Integer.parseInt(savedSensitivity);
        int idx = 0;

        if (sensArrayList == null) {
            String[] sensArray = getResources().getStringArray(R.array.arr_sensitivity);
            sensArrayList = new ArrayList<String>(Arrays.asList(sensArray));
        }
        if (sensArrayList.contains(String.valueOf(sensitivity/10))) {
            idx = sensArrayList.indexOf(String.valueOf(sensitivity/10));
        }


        modesAdapter = ArrayAdapter.createFromResource(this, R.array.arr_sensitivity, R.layout.li_spinner);
        modesAdapter.setDropDownViewResource(R.layout.li_spinner);
        sensSpinner.setOnItemSelectedListener(sensListener);
        sensSpinner.setAdapter(modesAdapter);
        sensSpinner.setSelection(idx);
    }

    private OnItemSelectedListener sensListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            CharSequence seq = modesAdapter.getItem(arg2);
            String sensString = String.valueOf(seq);
            if (sensString != null) {
                sensitivity = Integer.parseInt(sensString)*10;
                StepDetector.setSensitivity(sensitivity);
                SharedPreferenceManager.setIntPref(TAG, Constants.SENSITIVITY_VALUE, sensitivity);
                Log.i(TAG, "Changed sensitivity to: "+sensitivity/10);
                //service.setSensitivity(Integer.parseInt(sensString));
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // Ignore
        }
    };

    private OnCheckedChangeListener startStopListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            SharedPreferenceManager.setBooleanPref(TAG,Constants.ACTIVITY_SENSE_SETTING,isChecked);
            try{
                if (mService != null) serviceIsRunning = mService.isRunning();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            if(isChecked && !serviceIsRunning) {
                SharedPreferenceManager.setIntPref(TAG, Constants.SENSITIVITY_VALUE, sensitivity);
                start();
                service.startAlarm();
            }else if(!isChecked && serviceIsRunning){
                stop();
                service.stopAlarm();
            }
            if(!isChecked) {
                service.addActSenseData(new Date());
            }
        }
    };

    //To identify the parent activity at run-time and provide up navigation accordingly
    @Override
    public Intent getParentActivityIntent(){
        Intent newIntent = null;
        newIntent = new Intent(this, getIntent().getClass());
        return newIntent;
    }

    public void next(){
        Intent intent = new Intent();
        //intent.putExtra(Constants.PARENT_ACTIVITY_NAME, Constants.ACTIVITY_SENSE);
        startActivity(intent.setClass(this, MedicineAlert.class));
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

    public static int getStepCount(){
        return stepCount;
    }

    public static void resetStepCount(){
        if(serviceIsRunning){
            try {
                mService.resetStepCount();
                stepCount = 0;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private static final Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            int current = msg.arg1;
            stepCountText.setText("Steps = " + current);
        }
    };

    private static final IStepServiceCallback.Stub mCallback = new IStepServiceCallback.Stub() {

        @Override
        public IBinder asBinder() {
            return mCallback;
        }

        @Override
        public void stepsChanged(int value) throws RemoteException {
            Log.i(TAG, "Steps=" + value);
            stepCount = value;
            Message msg = handler.obtainMessage();
            msg.arg1 = value;
            handler.sendMessage(msg);
        }
    };

    private static final ServiceConnection mConnection = new ServiceConnection() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.i(TAG, "onServiceConnected()");
            mService = IStepService.Stub.asInterface(service);
            try {
                mService.registerCallback(mCallback);
                mService.setSensitivity(sensitivity);
                startStopButton.setChecked(mService.isRunning());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onServiceDisconnected(ComponentName className) {
            Log.i(TAG, "onServiceDisconnected()");
            try {
                startStopButton.setChecked(mService.isRunning());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mService = null;
        }
    };
}

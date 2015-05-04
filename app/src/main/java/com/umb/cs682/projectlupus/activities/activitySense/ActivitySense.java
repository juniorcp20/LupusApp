package com.umb.cs682.projectlupus.activities.activitySense;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

    private static ToggleButton startStopButton = null;
    private static TextView stepCountText = null;

    private static ArrayList<String> sensArrayList = null;
    private static ArrayAdapter<CharSequence> modesAdapter = null;

    private ActivitySenseService service = LupusMate.getActivitySenseService();
    private static Handler handler = null;

    private TextView stepCountTextDup = null;
    private Button save = null;
    private Button show = null;
    private Button delete = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_activity_sense);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        if(isInit){
            actionBar.setTitle(R.string.title_init_activity_sense);
        }else {
            actionBar.setTitle(R.string.title_activity_sense);
        }
        startStopButton = (ToggleButton) this.findViewById(R.id.tb_start_stop);
        startStopButton.setOnCheckedChangeListener(startStopListener);

        String sensStr = String.valueOf(DEFAULT_SENSITIVITY);
        int idx = 0;

        if (sensArrayList == null) {
            String[] sensArray = getResources().getStringArray(R.array.arr_sensitivity);
            sensArrayList = new ArrayList<String>(Arrays.asList(sensArray));
        }
        if (sensArrayList.contains(sensStr)) {
            idx = sensArrayList.indexOf(sensStr);
        }

        Spinner sensSpinner = (Spinner) findViewById(R.id.sp_sensitivity);
        modesAdapter = ArrayAdapter.createFromResource(this, R.array.arr_sensitivity, android.R.layout.simple_spinner_item);
        modesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sensSpinner.setOnItemSelectedListener(sensListener);
        sensSpinner.setAdapter(modesAdapter);
        sensSpinner.setSelection(idx);

        stepCountText = (TextView) this.findViewById(R.id.tv_steps);
        setHandler();

        stepCountTextDup = (TextView) findViewById(R.id.stepcount);
        save = (Button) findViewById(R.id.save);
        show = (Button) findViewById(R.id.show);
        delete = (Button) findViewById(R.id.delete);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showData();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRows();
            }
        });

        service.setupAlarm();
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
        service.onStartPedometer();
    }

    @Override
    public void onPause() {
        super.onPause();
        service.onPausePedometer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        service.unsetHandler();
        Log.d(TAG, "unsetHandler");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setHandler();
        Log.d(TAG,"setHandler");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private OnItemSelectedListener sensListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            CharSequence seq = modesAdapter.getItem(arg2);
            String sensString = String.valueOf(seq);
            if (sensString != null) {
                service.setSensitivity(Integer.parseInt(sensString));
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
            if(isChecked) {
                SharedPreferenceManager.setIntPref(TAG, Constants.SENSITIVITY_VALUE, service.getSensitivity());
                //showData();
                service.startAlarm();
            }else{
                service.stopAlarm();
            }
            service.startStopPedometer(isChecked);
            if(!isChecked) {
                saveData();
            }
        }
    };

    private void setHandler() {
        Log.d(TAG,"setHandler");
        service.setHandler(new Handler() {

            public void handleMessage(Message msg) {
                int current = msg.arg1;
                stepCountText.setText("Steps = " + current);
            }
        });
    }
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

    public void saveData(){
        service.addActSenseData(new Date());
    }

    public void showData(){
        stepCountTextDup.setText(String.valueOf(service.getStoredStepCount(new Date())));
    }

    public void deleteRows(){
        service.deleteData(new Date());
    }
}

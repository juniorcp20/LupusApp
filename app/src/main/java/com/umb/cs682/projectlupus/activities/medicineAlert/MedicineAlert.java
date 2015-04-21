package com.umb.cs682.projectlupus.activities.medicineAlert;

import com.umb.cs682.projectlupus.R;
import com.umb.cs682.projectlupus.activities.main.Home;
import com.umb.cs682.projectlupus.config.AppConfig;
import com.umb.cs682.projectlupus.service.MedicineService;
import com.umb.cs682.projectlupus.util.Constants;
import com.umb.cs682.projectlupus.util.SharedPreferenceManager;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MedicineAlert extends Activity {
    //private String parent = null;
    private boolean isInit = SharedPreferenceManager.getBooleanPref(Constants.IS_FIRST_RUN);

    Button addMed;

    private MedicineService medService = AppConfig.getMedicineService();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_medicine_alert);
        addMed = (Button) findViewById(R.id.med_alert_button);
        addMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMedicine();
            }
        });
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //parent = getIntent().getStringExtra(Constants.PARENT_ACTIVITY_NAME);
        //isInit = getIntent().getBooleanExtra(Constants.IS_INIT, false);
       // if(parent != null) {
            //Log.i(Constants.ACTIVITY_SENSE, parent);
            if (isInit) {
                actionBar.setTitle(R.string.title_init_medicine_alert);
                medService.initMedicineDB();

                //isInit = true;**
            } else {
                actionBar.setTitle(R.string.title_medicine_alert);
            }
        //}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(isInit) {
            getMenuInflater().inflate(R.menu.m_action_finish, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_finish) {
            next();
        }
        return super.onOptionsItemSelected(item);
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
        startActivity(intent.setClass(this, Home.class));
    }

    public void addMedicine(){
        Intent intent = new Intent();
        //intent.putExtra(Constants.IS_INIT, isInit);
        startActivity(intent.setClass(this, AddMedicine.class));
    }
}

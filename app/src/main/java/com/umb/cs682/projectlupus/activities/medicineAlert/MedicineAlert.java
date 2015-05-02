package com.umb.cs682.projectlupus.activities.medicineAlert;

import com.umb.cs682.projectlupus.R;
import com.umb.cs682.projectlupus.activities.main.Home;
import com.umb.cs682.projectlupus.config.AppConfig;
import com.umb.cs682.projectlupus.domain.ReminderBO;
import com.umb.cs682.projectlupus.service.MedicineService;
import com.umb.cs682.projectlupus.service.ReminderService;
import com.umb.cs682.projectlupus.util.AlarmUtil;
import com.umb.cs682.projectlupus.util.Constants;
import com.umb.cs682.projectlupus.util.DateTimeUtil;
import com.umb.cs682.projectlupus.util.SharedPreferenceManager;
import com.umb.cs682.projectlupus.util.Utils;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MedicineAlert extends Activity {
    private static final String TAG = "activities.medAlert";
    private boolean isInit = SharedPreferenceManager.getBooleanPref(Constants.IS_FIRST_RUN);

    Button addMed;
    private ListView medNamesListView;

    private ArrayList<String> medNames;
    private boolean isNew = false;
    private int currMed;
    private String medName = null;

    private AddMedicineNameAdapter adapter;

    private MedicineService medService = AppConfig.getMedicineService();
    private ReminderService reminderService = AppConfig.getReminderService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_medicine_alert);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        addMed = (Button) findViewById(R.id.med_alert_button);
        medNamesListView = (ListView)findViewById(R.id.add_medname_listview);

        addMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMedicine();
            }
        });

        initMedNamesListView();
        checkIntent();

        if (isInit) {
            actionBar.setTitle(R.string.title_init_medicine_alert);
            medService.initMedicineDB();

            //isInit = true;**
        } else {
            actionBar.setTitle(R.string.title_medicine_alert);
        }
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
        // as you specify a medName activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_finish) {
            next();
        }
        return super.onOptionsItemSelected(item);
    }

    //To identify the medName activity at run-time and provide up navigation accordingly
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
        isNew = true;
        Intent intent = new Intent();
        intent.putExtra(Constants.IS_NEW_MED, isNew);
        startActivity(intent.setClass(this, AddMedicine.class));
    }

    private void initMedNamesListView(){
        medNames = reminderService.getMedicinesWithReminders();
        if(medNames.size() == 0){
            medNamesListView.setVisibility(View.INVISIBLE);
        }

        adapter = new AddMedicineNameAdapter(getApplicationContext(), medNames);
        medNamesListView.setAdapter(adapter);
        medNamesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isNew = false;
                currMed = position;
                Intent intent = new Intent();
                intent.putExtra(Constants.IS_NEW_MED, isNew);
                intent.putExtra(Constants.MED_NAME, medNames.get(position));
                startActivity(intent.setClass(getApplicationContext(), AddMedicine.class));
            }
        });
    }

    private void checkIntent(){
        medName = getIntent().getStringExtra(Constants.MED_NAME);
        if (medName != null){
            isNew = getIntent().getBooleanExtra(Constants.IS_NEW_MED, true);
            if(isNew) {
               /* medNames.add(medName);
                adapter.notifyDataSetChanged();
                medNamesListView.setVisibility(View.VISIBLE);*/
                Utils.displayToast(this, "Added "+medName);
            }else{
               /* medNames.set(currMed, medName);
                adapter.notifyDataSetChanged();*/
                if(medNames.size()>0)
                Utils.displayToast(this, "Updated "+medName);
            }
        }
    }

    public class AddMedicineNameAdapter extends ArrayAdapter<String> {
        public AddMedicineNameAdapter(Context context, ArrayList<String> medNames) {
            super(context, R.layout.li_reminder_item, medNames);
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            View customView = inflater.inflate(R.layout.li_reminder_item, parent, false);
            String name = medNames.get(position);
            TextView displayName = (TextView) customView.findViewById(R.id.display_time);
            ImageView actionIcon = (ImageView) customView.findViewById(R.id.delete_icon);
            actionIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Long currMedID = medService.getMedicine(medName).getId();
                    ArrayList<Long> remIDs = reminderService.getAllMedReminderIDs(medService.getMedicine(medName).getId());
                    cancelAlarm(currMedID, remIDs);
                    reminderService.deleteMedReminderByMedId(currMedID);
                    medNames.remove(position);
                    if (medNames.isEmpty()) {
                        medNamesListView.setVisibility(View.INVISIBLE);
                    }
                    notifyDataSetChanged();
                }
            });
            displayName.setText(name);
            actionIcon.setImageResource(R.drawable.ic_action_discard);
            return customView;
        }
    }
    private void cancelAlarm(Long currMedID, ArrayList<Long> remIDs){
        for(Long id: remIDs){
            ReminderBO currRemBO = reminderService.getMedReminder(id);
            String status = currRemBO.getStatus();
            if(status != Constants.REM_STATUS_CREATED){
                Date reminderTime = reminderService.getMedReminder(id).getReminderTime();
                int hourOfDay = DateTimeUtil.getHour(reminderTime);
                int min = DateTimeUtil.getMin(reminderTime);
                String dayOfWeek = null;
                String dayOfMonth = null;
                String selInterval = medService.getMedicineInterval(currMedID);
                switch (selInterval){
                    case Constants.WEEKLY:
                        dayOfWeek = currRemBO.getReminderDayDate();
                    case Constants.MONTHLY:
                        dayOfMonth = currRemBO.getReminderDayDate();
                }
                AlarmUtil.cancelAlarm(this, id.intValue(), selInterval, hourOfDay, min, dayOfWeek, dayOfMonth, Constants.MED_REMINDER);
                Log.i(TAG, "Cancelling Alarm");
            }
            return;
        }


    }
}

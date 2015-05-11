package com.umb.cs682.projectlupus.activities.moodAlert;

import com.umb.cs682.projectlupus.R;
import com.umb.cs682.projectlupus.activities.activitySense.ActivitySense;
import com.umb.cs682.projectlupus.config.LupusMate;
import com.umb.cs682.projectlupus.service.ReminderService;
import com.umb.cs682.projectlupus.util.AlarmUtil;
import com.umb.cs682.projectlupus.util.Constants;
import com.umb.cs682.projectlupus.util.DateTimeUtil;
import com.umb.cs682.projectlupus.util.SharedPreferenceManager;
import com.umb.cs682.projectlupus.util.Utils;

import android.app.ActionBar;
import android.app.Activity;
import android.app.TimePickerDialog;
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
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import de.greenrobot.dao.DaoException;

public class MoodAlert extends Activity implements AdapterView.OnItemClickListener{
    private static final String TAG = "activities.moodAlert";
    private boolean isInit = SharedPreferenceManager.isFirstRun();
    private boolean isNew = false;

    private int selHour;
    private int selMin;
    private Long selID;
    
    private StringBuilder selectedTime;
    private ArrayList<Long> remIDs;

    private Button addAlertbtn;
    private ListView alertsList;

    private TimePickerDialog timePicker;

    private MoodAlertAdapter adapter;

    private ReminderService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_mood_alert);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        final LupusMate lupusMate = (LupusMate) getApplicationContext();
        service = lupusMate.getReminderService();

        if(isInit){
            actionBar.setTitle(R.string.title_init_mood_alert);
        }else {
            actionBar.setTitle(R.string.title_mood_alert);
        }
        addAlertbtn = (Button)findViewById(R.id.mood_alert_button);
        addAlertbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isNew = true;
                timePicker.show();
            }
        });

        alertsList = (ListView)findViewById(R.id.mood_alert_listview);
        remIDs = service.getAllMoodReminderIDs();
        if(remIDs.size()==0){
            alertsList.setVisibility(View.INVISIBLE);
        }

        adapter = new MoodAlertAdapter(this, remIDs);
        alertsList.setAdapter(adapter);

        alertsList.setOnItemClickListener(this);

        setupTimePicker();
    }

    private void setupTimePicker() {
        Calendar cal = Calendar.getInstance();
        timePicker = new TimePickerDialog(this, mTimeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), DateTimeUtil.is24HourFormat(getApplicationContext()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(isInit){
            getMenuInflater().inflate(R.menu.m_action_next, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_next) {
            next();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** Callback received when the user "picks" a time in the dialog */
    private TimePickerDialog.OnTimeSetListener mTimeSetListener =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    Calendar cal = Calendar.getInstance(TimeZone.getDefault());
                    cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    cal.set(Calendar.MINUTE, minute);
                    int requestCode;
                    selMin = minute;
                    String am_pm = null;
                    if(!DateTimeUtil.is24HourFormat(getApplicationContext())) {
                        if (hourOfDay >= 12)
                        {
                            if(hourOfDay == 12){
                                selHour = hourOfDay;
                            }else {
                                selHour = hourOfDay - 12;
                            }
                            am_pm = "PM";
                        }  else {
                            selHour = hourOfDay;
                            am_pm = "AM";
                        }
                    }else{
                        selHour = hourOfDay;
                    }
                    selectedTime = new StringBuilder();
                    selectedTime.append(pad(selHour)).append(":").append(pad(selMin));
                    if(am_pm != null){
                        selectedTime.append(" ").append(am_pm);
                    }
                    if(isNew){
                        try {
                            Long newID = service.addMoodReminder(selectedTime.toString());
                            int remID = newID.intValue();
                            requestCode = remID;
                            //AlarmUtil.setDailyRepeatingAlarm(getApplicationContext(), Constants.MOOD_REMINDER, remID, requestCode, cal);
                            AlarmUtil.setAlarm(getApplicationContext(), requestCode, remID, Constants.MOOD_REMINDER, Constants.DAILY, cal);
                            service.updateMoodReminderStatus(newID, Constants.REM_STATUS_ACTIVE);
                            remIDs.add(newID);
                        }catch (DaoException e){
                            Utils.displayToast(getApplicationContext(), e.getMessage());
                        }
                    }else{
                       /* Calendar oldCal = Calendar.getInstance(TimeZone.getDefault());
                        Calendar tempCal = Calendar.getInstance(TimeZone.getDefault());
                        tempCal.setTimeInMillis(service.getMoodReminder(selID).getReminderTime().getTime());
                        oldCal.set(Calendar.HOUR_OF_DAY, tempCal.get(Calendar.HOUR_OF_DAY));
                        oldCal.set(Calendar.MINUTE, tempCal.get(Calendar.MINUTE));*/
                        requestCode = selID.intValue();
                        AlarmUtil.updateAlarm(getApplicationContext(), requestCode, selID.intValue(), Constants.MOOD_REMINDER, Constants.DAILY, null, cal);
                        service.editMoodReminder(selID,selectedTime.toString());
                    }

                    adapter.notifyDataSetChanged();
                    alertsList.setVisibility(View.VISIBLE);
                    Utils.displayToast(getApplicationContext(), new StringBuilder().append("Alert set at ").append(selectedTime).toString());
                }
            };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        isNew = false;
        selID = remIDs.get(position);
        timePicker.show();
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        return super.onMenuItemSelected(featureId, item);
    }


    @Override
    public Intent getParentActivityIntent(){
        Intent newIntent = null;
        newIntent = new Intent(this, getIntent().getClass());
        return newIntent;
    }

    public void next(){
        Intent intent = new Intent();
        intent.putExtra(Constants.PARENT_ACTIVITY_NAME, Constants.MOOD_ALERT);
        startActivity(intent.setClass(this, ActivitySense.class));
    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    public class MoodAlertAdapter extends ArrayAdapter<Long> {
        public MoodAlertAdapter(Context context, ArrayList<Long> times) {
            super(context, R.layout.li_reminder_item, times);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View customView = inflater.inflate(R.layout.li_reminder_item, parent, false);
            String time = DateTimeUtil.toTimeString(service.getReminderTimeByID(getItem(position)));
            TextView displayTime = (TextView) customView.findViewById(R.id.display_time);
            ImageView actionIcon = (ImageView) customView.findViewById(R.id.delete_icon);
            actionIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currRemID = remIDs.get(position).intValue();
                    int requestCode = currRemID;
                    AlarmUtil.cancelAlarm(getApplicationContext(),requestCode, currRemID, Constants.MOOD_REMINDER, Constants.DAILY, null);
                    service.deleteMoodReminder(remIDs.get(position));
                    remIDs.remove(position);
                    if(remIDs.isEmpty()){
                        alertsList.setVisibility(View.INVISIBLE);
                    }
                    notifyDataSetChanged();
                }
            });
            displayTime.setText(time);
            actionIcon.setImageResource(R.drawable.ic_action_discard);
            return customView;
        }
    }
}

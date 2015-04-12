package com.umb.cs682.projectlupus.activities.moodAlert;

import com.umb.cs682.projectlupus.R;
import com.umb.cs682.projectlupus.activities.activitySense.ActivitySense;
import com.umb.cs682.projectlupus.util.Constants;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
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
import android.widget.Toast;

import java.util.ArrayList;

public class MoodAlert extends Activity implements AdapterView.OnItemClickListener{//, View.OnClickListener{
    private static final int TIME_DIALOG_ID = 10;
    private String parent = null;

    private int selHour;
    private int selMin;
    private StringBuilder selectedTime;
    private ArrayList<String> strArr;
    private boolean isNew = false;
    private int selTimePos;


    private Button addAlertbtn;
    private ListView alertsList;

    private MoodAlertAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_mood_alert);
        parent = getIntent().getStringExtra(Constants.PARENT_ACTIVITY_NAME);
        Log.i(Constants.MOOD_ALERT, parent);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        if(parent.equals(Constants.PROFILE)){
            actionBar.setTitle(R.string.title_init_mood_alert);
        }else {
            actionBar.setTitle(R.string.title_mood_alert);
        }
        addAlertbtn = (Button)findViewById(R.id.mood_alert_button);
        addAlertbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isNew = true;
                showDialog(TIME_DIALOG_ID);
            }
        });

        alertsList = (ListView)findViewById(R.id.mood_alert_listview);
        alertsList.setVisibility(View.INVISIBLE);
        strArr = new ArrayList<String>();

        adapter = new MoodAlertAdapter(this,strArr);
        alertsList.setAdapter(adapter);

        alertsList.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(parent.equals(Constants.PROFILE)) {
            getMenuInflater().inflate(R.menu.m_action_next, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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
                    selHour = hourOfDay;
                    selMin = minute;
                    selectedTime = new StringBuilder();
                    selectedTime.append(pad(selHour)).append(":").append(pad(selMin));
                    if(isNew){
                        strArr.add(selectedTime.toString());
                    }else{
                        strArr.set(selTimePos,selectedTime.toString());
                    }
                    adapter.notifyDataSetChanged();
                    alertsList.setVisibility(View.VISIBLE);
                    displayToast();
                }
            };

    /** Create a new dialog for time picker */

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case TIME_DIALOG_ID:
                return new TimePickerDialog(this,
                        mTimeSetListener, selHour, selMin, false);
        }
        return null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        isNew = false;
        selTimePos = position;
        showDialog(TIME_DIALOG_ID);
        ImageView del = (ImageView) view.findViewById(R.id.delete_icon);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strArr.remove(selTimePos);
                adapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "Alert Removed",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayToast() {
        Toast.makeText(this, new StringBuilder().append("Alert set at ").append(selectedTime), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        return super.onMenuItemSelected(featureId, item);
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
        intent.putExtra(Constants.PARENT_ACTIVITY_NAME, Constants.MOOD_ALERT);
        startActivity(intent.setClass(this, ActivitySense.class));
    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    public class MoodAlertAdapter extends ArrayAdapter<String> {
        public MoodAlertAdapter(Context context, ArrayList<String> times) {
            super(context, R.layout.li_reminder_item, times);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View customView = inflater.inflate(R.layout.li_reminder_item, parent, false);
            String time = getItem(position);
            TextView displayTime = (TextView) customView.findViewById(R.id.display_time);
            ImageView actionIcon = (ImageView) customView.findViewById(R.id.delete_icon);
            actionIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    strArr.remove(position);
                    if(strArr.isEmpty()){
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

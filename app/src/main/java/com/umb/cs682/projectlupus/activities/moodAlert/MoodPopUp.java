package com.umb.cs682.projectlupus.activities.moodAlert;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;

import com.umb.cs682.projectlupus.R;
import com.umb.cs682.projectlupus.config.LupusMate;
import com.umb.cs682.projectlupus.exceptions.AppException;
import com.umb.cs682.projectlupus.service.MoodLevelService;
import com.umb.cs682.projectlupus.service.ReminderService;
import com.umb.cs682.projectlupus.util.AlarmUtil;
import com.umb.cs682.projectlupus.util.Constants;
import com.umb.cs682.projectlupus.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.TimeZone;

public class MoodPopUp extends Activity {

    private RatingBar rbMoodLevel;
    private Spinner spSnoozeInterval;
    private ImageView ivSnooze;
    private ImageView ivSkip;
    private ImageView ivAccept;

    Bundle alarmIntentExtras;
    int reminderID;
    int selMoodLevel = 1;
    int selSnoozeInterval = 5;
    int snoozeRequestCode;
    boolean snoozed = false;

    MoodLevelService moodLevelService = LupusMate.getMoodLevelService();
    ReminderService reminderService = LupusMate.getReminderService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_mood_pop_up);

        rbMoodLevel =(RatingBar)findViewById(R.id.ratingBar);
        spSnoozeInterval = (Spinner) findViewById(R.id.sp_mood_snooze_interval);
        ivSnooze = (ImageView) findViewById(R.id.iv_mood_snooze);
        ivSkip = (ImageView) findViewById(R.id.iv_mood_skip);
        ivAccept = (ImageView) findViewById(R.id.iv_mood_accept);

        alarmIntentExtras = getIntent().getExtras();
        if(alarmIntentExtras != null) {
            reminderID = alarmIntentExtras.getInt(Constants.REMINDER_ID);
        }else {
            try {
                throw new AppException("Data not found in intent");
            } catch (AppException e) {
                e.printStackTrace();
            }
        }
        //reminderID = getIntent().getIntExtra(Constants.REMINDER_ID, -1);

        initSnoozeIntervalSpinner();
        moodLevelRatingListener();

        ivAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelSnooze();
                save();
                finish();
            }
        });
        ivSnooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snooze();
                finish();
            }
        });
        ivSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelSnooze();
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initSnoozeIntervalSpinner(){
        ArrayList<String> ages = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.arr_snooze_interval)));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.li_spinner, ages);
        spSnoozeInterval.setAdapter(adapter);
    }

    private void moodLevelRatingListener(){
        rbMoodLevel.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                selMoodLevel = ((int) rating);
            }
        });
    }

    private void save(){
        if (reminderID > 0) {
            moodLevelService.addMoodLevel(reminderID, selMoodLevel);
        }
    }

    private void snooze(){
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        selSnoozeInterval = Utils.getSnoozeInterval(spSnoozeInterval);
        cal.add(Calendar.MINUTE, selSnoozeInterval);
        long snoozeTime = cal.getTimeInMillis();
        snoozeRequestCode = 5000 + reminderID; // to uniquely identify snooze alarms from normal alarms
        AlarmUtil.snooze(this, snoozeRequestCode, reminderID, Constants.MOOD_REMINDER, Constants.DAILY, null, snoozeTime);
        reminderService.updateMoodReminderStatus(reminderID, Constants.REM_STATUS_SNOOZE);
        snoozed = true;
    }

    private void cancelSnooze(){
        if(snoozed){
            AlarmUtil.cancelSnooze(this, snoozeRequestCode, reminderID, Constants.MOOD_REMINDER, Constants.DAILY, null);
        }
    }


}

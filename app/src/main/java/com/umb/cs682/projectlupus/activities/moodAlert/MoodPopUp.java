package com.umb.cs682.projectlupus.activities.moodAlert;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
    private static final String TAG = "activities.moodpopup";

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

    MoodLevelService moodLevelService;
    ReminderService reminderService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_mood_pop_up);
        setFinishOnTouchOutside(false);

        final LupusMate lupusMate = (LupusMate) getApplicationContext();
        moodLevelService = lupusMate.getMoodLevelService();
        reminderService = lupusMate.getReminderService();

        rbMoodLevel =(RatingBar)findViewById(R.id.ratingBar);
        spSnoozeInterval = (Spinner) findViewById(R.id.sp_mood_snooze_interval);
        ivSnooze = (ImageView) findViewById(R.id.iv_mood_snooze);
        ivSkip = (ImageView) findViewById(R.id.iv_mood_skip);
        ivAccept = (ImageView) findViewById(R.id.iv_mood_accept);

        onNewIntent(getIntent());
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
                skip();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        reminderID = intent.getIntExtra(Constants.REMINDER_ID,0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        skip();
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
            reminderService.updateMoodReminderStatus(reminderID, Constants.REM_STATUS_DONE);
        }
    }

    private void skip(){
        reminderService.updateMoodReminderStatus(reminderID, Constants.REM_STATUS_SKIP);
        finish();
    }

    private void snooze(){
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        selSnoozeInterval = Utils.getSnoozeInterval(spSnoozeInterval.getSelectedItem().toString());
        cal.add(Calendar.MINUTE, selSnoozeInterval);
        long snoozeTime = cal.getTimeInMillis();
        snoozeRequestCode = 5000 + reminderID;
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

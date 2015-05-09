package com.umb.cs682.projectlupus.activities.medicineAlert;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.umb.cs682.projectlupus.R;
import com.umb.cs682.projectlupus.config.LupusMate;
import com.umb.cs682.projectlupus.exceptions.AppException;
import com.umb.cs682.projectlupus.service.MedicineService;
import com.umb.cs682.projectlupus.service.ReminderService;
import com.umb.cs682.projectlupus.util.AlarmUtil;
import com.umb.cs682.projectlupus.util.Constants;
import com.umb.cs682.projectlupus.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.TimeZone;

public class MedicinePopUp extends Activity {
    private static final String TAG = "activities.medpopup";

    private TextView medNameText;
    private Spinner spSnoozeInterval;
    private ImageView ivSnooze;
    private ImageView ivSkip;
    private ImageView ivAccept;


    int reminderID;
    Long medicineID;
    int selSnoozeInterval = 5;
    int snoozeRequestCode;
    boolean snoozed = false;

    MedicineService medicineService;
    ReminderService reminderService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_medicine_pop_up);
        setFinishOnTouchOutside(false);

        final LupusMate lupusMate = (LupusMate) getApplicationContext();
        medicineService = lupusMate.getMedicineService();
        reminderService = lupusMate.getReminderService();

        medNameText = (TextView) findViewById(R.id.tv_med_popup_name);
        spSnoozeInterval = (Spinner) findViewById(R.id.sp_med_snooze_interval);
        ivAccept = (ImageView) findViewById(R.id.iv_med_accept);
        ivSnooze = (ImageView) findViewById(R.id.iv_med_snooze);
        ivSkip = (ImageView) findViewById(R.id.iv_med_skip);

        initSnoozeIntervalSpinner();;

        onNewIntent(getIntent());

        ivAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelSnooze();
                take();
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
    protected void onNewIntent(Intent intent) {
        reminderID = intent.getIntExtra(Constants.REMINDER_ID,0);
        if(reminderID > 0) {
            setMedNameText();
        }
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
    public void onBackPressed() {
        super.onBackPressed();
        skip();
    }

    private void setMedNameText(){
        medicineID = reminderService.getMedReminder(reminderID).getMedId();
        String medName = medicineService.getMedicine(medicineID).getMedName();
        medNameText.setText(medName);
    }

    private void initSnoozeIntervalSpinner(){
        ArrayList<String> ages = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.arr_snooze_interval)));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.li_spinner, ages);
        spSnoozeInterval.setAdapter(adapter);
    }

    private void take(){
        medicineService.incrementMedTakenCount(medicineID);
        reminderService.updateMedReminderStatus(reminderID, Constants.REM_STATUS_DONE);
        Log.i(TAG, "Reminder status - Done");
    }

    private void skip(){
        reminderService.updateMedReminderStatus(reminderID, Constants.REM_STATUS_SKIP);
        finish();
        Log.i(TAG, "Reminder status - Skip");
    }

    private void snooze(){
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        selSnoozeInterval = Utils.getSnoozeInterval(spSnoozeInterval.getSelectedItem().toString());
        cal.add(Calendar.MINUTE, selSnoozeInterval);
        long snoozeTime = cal.getTimeInMillis();
        snoozeRequestCode = 5000 + reminderID; // to uniquely identify snooze alarms from normal alarms
        AlarmUtil.snooze(this, snoozeRequestCode, reminderID, Constants.MED_REMINDER, Constants.DAILY, null, snoozeTime);
        reminderService.updateMedReminderStatus(reminderID, Constants.REM_STATUS_SNOOZE);
        snoozed = true;
        Log.i(TAG, "Reminder status - Snooze");
    }

    private void cancelSnooze(){
        if(snoozed){
            AlarmUtil.cancelSnooze(this, snoozeRequestCode, reminderID, Constants.MED_REMINDER, Constants.DAILY, null);
            Log.i(TAG, "Snooze cancelled");
        }
    }
}

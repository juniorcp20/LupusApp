package com.umb.cs682.projectlupus.activities.medicineAlert;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

    MedicineService medicineService = LupusMate.getMedicineService();
    ReminderService reminderService = LupusMate.getReminderService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_medicine_pop_up);

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
                finish();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        reminderID = intent.getIntExtra(Constants.REMINDER_ID,0);
        /*Bundle alarmIntentExtras = intent.getExtras();
        if(alarmIntentExtras != null) {
            reminderID = alarmIntentExtras.getInt(Constants.REMINDER_ID);
        }else {
            try {
                throw new AppException("Data not found in intent");
            } catch (AppException e) {
                e.printStackTrace();
            }
        }*/
        if(reminderID > 0) {
            setMedNameText();
        }
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
    }

    private void snooze(){
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        selSnoozeInterval = Utils.getSnoozeInterval(spSnoozeInterval);
        cal.add(Calendar.MINUTE, selSnoozeInterval);
        long snoozeTime = cal.getTimeInMillis();
        snoozeRequestCode = 5000 + reminderID; // to uniquely identify snooze alarms from normal alarms
        AlarmUtil.snooze(this, snoozeRequestCode, reminderID, Constants.MED_REMINDER, Constants.DAILY, null, snoozeTime);
        reminderService.updateMoodReminderStatus(reminderID, Constants.REM_STATUS_SNOOZE);
        snoozed = true;
    }

    private void cancelSnooze(){
        if(snoozed){
            AlarmUtil.cancelSnooze(this, snoozeRequestCode, reminderID, Constants.MED_REMINDER, Constants.DAILY, null);
        }
    }
}

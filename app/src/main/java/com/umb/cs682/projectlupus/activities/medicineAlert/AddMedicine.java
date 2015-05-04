package com.umb.cs682.projectlupus.activities.medicineAlert;

import com.umb.cs682.projectlupus.R;
import com.umb.cs682.projectlupus.config.LupusMate;
import com.umb.cs682.projectlupus.domain.MedicineBO;
import com.umb.cs682.projectlupus.service.MedicineService;
import com.umb.cs682.projectlupus.service.ReminderService;
import com.umb.cs682.projectlupus.util.AlarmUtil;
import com.umb.cs682.projectlupus.util.Constants;
import com.umb.cs682.projectlupus.util.DateTimeUtil;
import com.umb.cs682.projectlupus.util.Utils;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import de.greenrobot.dao.DaoException;

public class AddMedicine extends Activity {
    private static final String TAG = "activities.AddMed";
    private static final String DAILY = "Daily";
    private static final String WEEKLY = "Weekly";
    private static final String MONTHLY = "Monthly";

    //Data to update medicine
    private int selDosage;
    private String selInterval;
        //if new med
    private long selMedID;
    private String selMedName;

    //Data to save reminder
    private String selDayOrDate = Constants.STR_DEFAULT;
    private ArrayList<Long> remIDs;

    private ArrayList<String> medNames;

    private boolean isNewRem = false;
    private boolean is24hrFormat = false;
    private boolean isNewMed = false;
    private boolean isValid = false;

    private Spinner medNameSpinner;
    private ImageButton addMedBtn;
    private EditText dosageText;
    private RadioGroup intervalGroup;
    private Button addMedAlertBtn;
    private ListView reminderListView;

    private TimePickerDialog timePickerDialog;
    private AlertDialog newMedDialog;
    private AlertDialog pickDayDialog;
    private AlertDialog pickDateDialog;

    private AddMedReminderAdapter medRemAdapter;
    private ArrayAdapter<String> medNameAdapter;

    private ReminderService reminderService = LupusMate.getReminderService();
    private MedicineService medService = LupusMate.getMedicineService();

    //local use
    private int selHour;
    private int selMin;
    private StringBuilder selectedTime;
    private long currRemID;
    private MedicineBO currMed;
    private String selDayOfWeek = "Sunday";
    private String selDayOfMonth = "1";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_add_medicine);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Build dialogs
        buildNewMedDialog(this);
        buildDayPickerDialog(this);
        buildDatePickerDialog(this);
        setupTimePickerDialog();

        //Initialize widgets
        medNameSpinner = (Spinner) findViewById(R.id.sp_med_name);
        addMedBtn = (ImageButton) findViewById(R.id.ib_add_new_med);
        dosageText = (EditText) findViewById(R.id.et_dosage);
        intervalGroup = (RadioGroup) findViewById(R.id.rg_interval);
        addMedAlertBtn = (Button) findViewById(R.id.bt_set_rem);
        reminderListView = (ListView) findViewById(R.id.lv_med_rems);

        //Check intent to decide the default values
        checkIntent();

        //Set Adapters and Listeners
        initMedNameSpinner();
        initReminderListView();
        addRadioButtonEventListeners();
        addMedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newMedDialog.show();
            }
        });
        addMedAlertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isNewRem = true;
                timePickerDialog.show();
            }
        });
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.m_action_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            validate();
            if(isValid) {
                updateDatabase();
                setAlarms();
                Intent intent = new Intent();
                intent.putExtra(Constants.IS_NEW_MED, isNewMed);
                intent.putExtra(Constants.MED_NAME, medNames.get(medNameSpinner.getSelectedItemPosition()));
                startActivity(intent.setClass(this, MedicineAlert.class));
                return true;
            }
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

    /* Begin Dialog Initialization*/
    private void buildNewMedDialog(Context context) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.d_add_new_medicine, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(dialogView);

        final EditText newMedNameText = (EditText) dialogView.findViewById(R.id.et_new_med);

        alertDialogBuilder//.setTitle("Add New Medicine")
                            .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    String newMedName = newMedNameText.getText().toString();
                                    selMedName = medService.addMedicine(newMedName, 0, DAILY, null);
                                    selMedID = getMedID();
                                    medNameAdapter.add(newMedName);
                                    medNameAdapter.notifyDataSetChanged();
                                    medNameSpinner.setSelection(Utils.getSpinnerIndex(medNameSpinner, newMedName));
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        newMedNameText.setText("");
            }
        });

        newMedDialog = alertDialogBuilder.create();
    }

    private void buildDayPickerDialog(Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Pick a day")
                  .setSingleChoiceItems(R.array.arr_days, 0, new DialogInterface.OnClickListener() {
                      public void onClick(DialogInterface dialog, int position) {
                          ListView lv = ((AlertDialog) dialog).getListView();
                          selDayOfWeek = lv.getAdapter().getItem(position).toString();
                      }
                  }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                        selDayOrDate = selDayOfWeek;
                        Utils.displayToast(getApplicationContext(), "Reminder set at "+ selDayOfWeek +" of every week.");
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        pickDayDialog = alertDialogBuilder.create();
    }

    private void buildDatePickerDialog(final Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        ArrayList<String> datesArray = new ArrayList<>();
        for(int i = 1; i <= cal.getActualMaximum(Calendar.DAY_OF_MONTH); i++){
            datesArray.add(String.valueOf(i));
        }
        alertDialogBuilder.setTitle("Pick a date")
                .setSingleChoiceItems(datesArray.toArray(new String[datesArray.size()]), 0,new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog,int position){
                        ListView lv = ((AlertDialog)dialog).getListView();
                        selDayOfMonth = lv.getAdapter().getItem(position).toString();
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            selDayOrDate = selDayOfMonth;
                            String suffix;
                            if(selDayOfMonth.equals("1")|| selDayOfMonth.equals("21")){
                                suffix = "st";
                            }
                            else if(selDayOfMonth.equals("2")|| selDayOfMonth.equals("22")){
                                suffix = "nd";
                            }
                            else if(selDayOfMonth.equals("3")|| selDayOfMonth.equals("23")){
                                suffix = "rd";
                            }else{
                                suffix = "th";
                            }
                            Utils.displayToast(context, "Reminder set at "+ selDayOfMonth +suffix+" of every month.");
                        }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        pickDateDialog = alertDialogBuilder.create();
    }

    private void setupTimePickerDialog() {
        Calendar cal = Calendar.getInstance();
        if(DateFormat.is24HourFormat(this)){
            is24hrFormat = true;
        }
        timePickerDialog = new TimePickerDialog(this, mTimeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), is24hrFormat);
    }
    /*  End Dialog Initialization   */

    /*  Begin Widget Setup   */
    private void initMedNameSpinner() {
        medNameAdapter = new MedNameSpinnerAdapter(this, medNames);
        medNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        medNameSpinner.setAdapter(medNameAdapter);
        medNameSpinner.setSelection(Utils.getSpinnerIndex(medNameSpinner,selMedName));
        medNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selMedName = medNames.get(position);
                selMedID = getMedID();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selMedName = medNames.get(0);
                selMedID = getMedID();
            }
        });
    }

    private void initReminderListView() {
        if(remIDs.size() == 0){
            reminderListView.setVisibility(View.INVISIBLE);
        }
        medRemAdapter = new AddMedReminderAdapter(this, remIDs);
        reminderListView.setAdapter(medRemAdapter);
        reminderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isNewRem = false;
                currRemID = remIDs.get(position);
                timePickerDialog.show();
            }
        });
    }

    private void addRadioButtonEventListeners() {
        setDefaultChecked();
        intervalGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_weekly:
                        selInterval = WEEKLY;
                        pickDayDialog.show();
                        break;
                    case R.id.rb_monthly:
                        selInterval = MONTHLY;
                        pickDateDialog.show();
                        break;
                }
            }
        });
    }

    /** Callback received when the user "picks" a time in the dialog */
    private TimePickerDialog.OnTimeSetListener mTimeSetListener =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    selMin = minute;
                    String am_pm = null;
                    if(!DateTimeUtil.is24hrFormat) {
                        if (hourOfDay > 12)         //hourofDay =13
                        {
                            selHour = hourOfDay - 12;     //hour=1
                            am_pm = "PM";                   //PM
                        } else {
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
                    if(isNewRem){
                        try {
                            remIDs.add(reminderService.addMedReminder(getMedID(), selDayOrDate, selectedTime.toString()));
                        }catch (DaoException e){
                            Utils.displayToast(getApplicationContext(), e.getMessage());
                        }
                    }else{
                        reminderService.editMedReminder(currRemID, selectedTime.toString());
                    }
                    medRemAdapter.notifyDataSetChanged();
                    reminderListView.setVisibility(View.VISIBLE);
                    Utils.displayToast(getApplicationContext(), "Alert set at "+selectedTime);
         }
               };
    /*  End Widget Setup    */

    /*  Adapter Begin*/

    public class MedNameSpinnerAdapter extends ArrayAdapter<String> {
        public MedNameSpinnerAdapter(Context context, ArrayList<String> names) {
            super(context, android.R.layout.simple_spinner_item, names);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View customView = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
            String name = medService.getMedicine(getItem(position)).getMedName();
            TextView displayName = (TextView) customView.findViewById(android.R.id.text1);
            displayName.setText(name);
            return customView;
        }
    }

    public class AddMedReminderAdapter extends ArrayAdapter<Long> {
        public AddMedReminderAdapter(Context context, ArrayList<Long> times) {
            super(context, R.layout.li_reminder_item, times);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View customView = inflater.inflate(R.layout.li_reminder_item, parent, false);
            String time = DateTimeUtil.toTimeString(reminderService.getReminderTimeByID(getItem(position)));
            TextView displayTime = (TextView) customView.findViewById(R.id.display_time);
            ImageView actionIcon = (ImageView) customView.findViewById(R.id.delete_icon);
            actionIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelAlarm(remIDs.get(position));
                    reminderService.deleteMedReminder(remIDs.get(position));
                    remIDs.remove(position);
                    if(remIDs.isEmpty()){
                        reminderListView.setVisibility(View.INVISIBLE);
                    }
                    notifyDataSetChanged();
                }
            });
            displayTime.setText(time);
            actionIcon.setImageResource(R.drawable.ic_action_discard);
            return customView;
        }
    }
    /*  Adapter End*/

    /* private methods*/
    private void checkIntent(){
        medNames = medService.getAllMedicineNames();
        isNewMed = getIntent().getBooleanExtra(Constants.IS_NEW_MED, false);
        if(isNewMed){
            //New Medicine
            selMedName = medNames.get(0);
            selMedID = getMedID();
            selInterval = DAILY;
            remIDs = new ArrayList<>();
        }else{
            selMedName = getIntent().getStringExtra(Constants.MED_NAME);
            currMed = medService.getMedicine(selMedName);
            selMedID = currMed.getId();
            dosageText.setText(String.valueOf(currMed.getDosage()));
            selInterval = currMed.getInterval();
            remIDs = reminderService.getAllMedReminderIDs(selMedID);
        }
    }

    private void setDefaultChecked(){
        switch(selInterval){
            case DAILY:
                intervalGroup.check(R.id.rb_daily);
                break;
            case WEEKLY:
                intervalGroup.check(R.id.rb_weekly);
                break;
            case MONTHLY:
                intervalGroup.check(R.id.rb_monthly);
                break;
        }
    }

    private void updateDatabase() {
        //Updating medicine table
        String dosage = dosageText.getText().toString();
        if(!dosage.matches("")) {
            selDosage = Integer.parseInt(dosage);
        }else{
            selDosage = 0;
        }
        medService.editDosage(selMedID, selDosage, selInterval);

        //Updating reminders table
        for(long id : remIDs) {
            reminderService.editMedReminder(id, selMedID, selDayOrDate);
        }
    }

    private void setAlarms(){
        for(Long id : remIDs){
            Date reminderTime = reminderService.getMedReminder(id).getReminderTime();
            int hourOfDay = DateTimeUtil.getHour(reminderTime);
            int min = DateTimeUtil.getMin(reminderTime);
            String dayOfWeek = null;
            String dayOfMonth = null;
            switch (selInterval){
                case WEEKLY:
                    dayOfWeek = selDayOfWeek;
                case MONTHLY:
                    dayOfMonth = selDayOfMonth;
            }
            int currRemID = id.intValue();
            int requestCode = currRemID;
            AlarmUtil.setAlarm(this, requestCode, currRemID, Constants.MED_REMINDER, selInterval, DateTimeUtil.getCalendar(hourOfDay, min, dayOfWeek, dayOfMonth));
            Log.i(TAG, "Setting Alarm");
            reminderService.updateMedReminderStatus(id, Constants.REM_STATUS_ACTIVE);
        }
    }

    private void cancelAlarm(Long id){
        String status = reminderService.getMedReminder(id).getStatus();
        if(status != Constants.REM_STATUS_CREATED){
            Date reminderTime = reminderService.getMedReminder(id).getReminderTime();
            int hourOfDay = DateTimeUtil.getHour(reminderTime);
            int min = DateTimeUtil.getMin(reminderTime);
            String dayOfWeek = null;
            String dayOfMonth = null;
            switch (selInterval){
                case WEEKLY:
                    dayOfWeek = selDayOfWeek;
                case MONTHLY:
                    dayOfMonth = selDayOfMonth;
            }
            int remID = id.intValue();
            int requestCode = remID;
            AlarmUtil.cancelAlarm(this, requestCode, remID, Constants.MED_REMINDER, selInterval, DateTimeUtil.getCalendar(hourOfDay, min, dayOfWeek, dayOfMonth));
            Log.i(TAG, "Cancelling Alarm");
        }
        return;
    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    private long getMedID(){
        return medService.getMedicine(selMedName).getId();
    }

    private void validate(){
        AlertDialog.Builder alertDialogBuilder;
        String dosage = dosageText.getText().toString();
        if(!dosage.equals("") && !(remIDs.size() == 0)){
            isValid = true;
        }else{
            StringBuilder msg = new StringBuilder("Missing data for :");
            if(dosage.equals(""))
                msg.append("\nDosage");
            if(remIDs.size() == 0)
                msg.append("\nReminders");
            alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage(msg.toString())
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });

            AlertDialog dialog = alertDialogBuilder.create();
            dialog.show();
        }
    }
}

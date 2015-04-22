package com.umb.cs682.projectlupus.activities.medicineAlert;

import com.umb.cs682.projectlupus.R;
import com.umb.cs682.projectlupus.activities.common.Profile;
import com.umb.cs682.projectlupus.config.AppConfig;
import com.umb.cs682.projectlupus.service.MedicineService;
import com.umb.cs682.projectlupus.service.ReminderService;
import com.umb.cs682.projectlupus.util.Constants;
import com.umb.cs682.projectlupus.util.DateUtil;
import com.umb.cs682.projectlupus.util.SharedPreferenceManager;
import com.umb.cs682.projectlupus.util.Utils;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class AddMedicine extends Activity {
    private static final String TAG = "activities.AddMed";
    private static final String WEEKLY = "Weekly";
    private static final String MONTHLY = "Monthly";

    private String selDay = "Sunday";
    private String selDate = "1";

    private boolean isInit = SharedPreferenceManager.getBooleanPref(Constants.IS_FIRST_RUN);

    private int selHour;
    private int selMin;
    private StringBuilder selectedTime;
    private ArrayList<String> strArr;
    private boolean isNew = false;
    private boolean is24hrFormat = false;
    private int selTimePos;

    private Button addMedAlertBtn;
    private ListView alertsList;
    private TimePickerDialog timePicker;

    private AddMedicineAdapter adapter;

    private ArrayList<String> medNames;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;

    private Spinner medNameSpinner;
    private ImageButton addMedBtn;
    private EditText dosageText;
    private RadioGroup intervalGroup;
    private Button addRemBtn;
    private ListView remList;

    private AlertDialog newMedDialog;
    private AlertDialog pickDayDialog;
    private AlertDialog pickDateDialog;

    private ReminderService reminderService = AppConfig.getReminderService();
    private MedicineService medService = AppConfig.getMedicineService();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_add_medicine);
        //isInit = getIntent().getBooleanExtra(Constants.IS_INIT, false);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Build dialogs
        buildNewMedDialog(this);
        buildDayPickerDialog(this);
        buildDatePickerDialog(this);

        medNames = medService.getAllMedicineNames();
        initMedNameSpinner();

        addMedBtn = (ImageButton) findViewById(R.id.ib_add_new_med);

        addMedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newMedDialog.show();
            }
        });

        dosageText = (EditText) findViewById(R.id.et_dosage);
        intervalGroup = (RadioGroup) findViewById(R.id.rg_interval);
        addRadioButtonEventHandlers();
        addRemBtn = (Button) findViewById(R.id.bt_set_rem);

        remList = (ListView) findViewById(R.id.lv_med_rems);
       /* actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.Tab existingMedTab = actionBar.newTab().setText("Use Existing");
        ActionBar.Tab newMedTab = actionBar.newTab().setText("Add New");
        ActionBar.Tab instTab = actionBar.newTab().setText("Instructions");

        Fragment existingMedicineFragment = new ExistingMedicineFragment();
        Fragment newMedicineFragment = new NewMedicineFragment();
        Fragment instFragment = new InstructionsFragment();

        existingMedTab.setTabListener(new AppTabListener(existingMedicineFragment));
        newMedTab.setTabListener(new AppTabListener(newMedicineFragment));
        instTab.setTabListener(new AppTabListener(instFragment));

        actionBar.addTab(existingMedTab);
        actionBar.addTab(newMedTab);
        actionBar.addTab(instTab);*/

        addMedAlertBtn = (Button)findViewById(R.id.bt_set_rem);
        alertsList = (ListView)findViewById(R.id.lv_med_rems);
        alertsList.setVisibility(View.INVISIBLE);
        strArr = new ArrayList<String>();

        adapter = new AddMedicineAdapter(this,strArr);
        alertsList.setAdapter(adapter);
        alertsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isNew = false;
                selTimePos = position;
                timePicker.show();
                ImageView del = (ImageView) view.findViewById(R.id.delete_icon);
                del.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        strArr.remove(selTimePos);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(), "Alert Removed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        setupTimePicker();
        addMedAlertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isNew = true;
                timePicker.show();
            }
        });

	}

    private void setupTimePicker() {
        Calendar cal = Calendar.getInstance();
        if(DateFormat.is24HourFormat(this)){
            is24hrFormat = true;
        }
        timePicker = new TimePickerDialog(this, mTimeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), is24hrFormat);
    }

    /** Callback received when the user "picks" a time in the dialog */
    private TimePickerDialog.OnTimeSetListener mTimeSetListener =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    selMin = minute;
                    String am_pm = null;
                    if(!DateUtil.is24hrFormat) {
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

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    private void displayToast() {
        Toast.makeText(this.getApplicationContext(), new StringBuilder().append("Alert set at ").append(selectedTime), Toast.LENGTH_SHORT).show();
    }

    public class AddMedicineAdapter extends ArrayAdapter<String> {
        public AddMedicineAdapter(Context context, ArrayList<String> times) {
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

    private void addRadioButtonEventHandlers() {
        intervalGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_weekly:
                        pickDayDialog.show();
                        break;
                    case R.id.rb_monthly:
                        pickDateDialog.show();
                        break;
                }
            }
        });
    }

    private void buildDatePickerDialog(final Context context) {
        LayoutInflater inflater = LayoutInflater.from(this);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Pick a date")
                .setSingleChoiceItems(R.array.arr_dates, 0,new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog,int position){
                        ListView lv = ((AlertDialog)dialog).getListView();
                        selDate = lv.getAdapter().getItem(position).toString();
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //todo add to db and set selection of spinner
                String suffix;
                if(selDate.equals("1")||selDate.equals("21")){
                    suffix = "st";
                }
                else if(selDate.equals("2")||selDate.equals("22")){
                    suffix = "nd";
                }
                else if(selDate.equals("3")||selDate.equals("23")){
                    suffix = "rd";
                }else{
                    suffix = "th";
                }
                Utils.displayToast(context, "Reminder set at "+selDate+suffix+" of every month.");
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        pickDateDialog = alertDialogBuilder.create();
    }
    private void buildDayPickerDialog(Context context) {
        LayoutInflater inflater = LayoutInflater.from(this);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Pick a day")
                .setSingleChoiceItems(R.array.arr_days, 0,new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog,int position){
                        ListView lv = ((AlertDialog)dialog).getListView();
                        selDay = lv.getAdapter().getItem(position).toString();
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //todo add to db and set selection of spinner
                Utils.displayToast(getApplicationContext(), "Reminder set at "+selDay+" of every week.");
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        pickDayDialog = alertDialogBuilder.create();
    }

    private void buildNewMedDialog(Context context) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.d_add_new_medicine, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(dialogView);

        final EditText newMedName = (EditText) dialogView.findViewById(R.id.et_new_med);

        alertDialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //todo add to db and set selection of spinner
            }
        }).setNegativeButton("Reset", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                newMedName.setText("");
            }
        });

        newMedDialog = alertDialogBuilder.create();
    }

    private void initMedNameSpinner() {
        medNameSpinner = (Spinner) findViewById(R.id.sp_med_name);
        ArrayAdapter<String> medNameAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, medNames);
        medNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        medNameSpinner.setAdapter(medNameAdapter);
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.m_action_save, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_save) {
            Intent intent = new Intent();
            intent.putExtra("Medicine Name",medNames.get(medNameSpinner.getSelectedItemPosition()));
            startActivity(intent.setClass(this, MedicineAlert.class));
			return true;
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

   /* class AppTabListener implements ActionBar.TabListener{
        public Fragment fragment;

        public AppTabListener(Fragment fragment){
            this.fragment = fragment;
        }

        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            ft.replace(R.id.tabContainer, fragment);
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

        }
    }*/

    private ImageButton.OnClickListener addNewMedClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            View dialogView = inflater.inflate(R.layout.d_add_new_medicine, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());
            alertDialogBuilder.setView(dialogView);

            final EditText newMedName = (EditText) dialogView.findViewById(R.id.et_new_med);

            alertDialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int id){
                    //todo add to db and set selection of spinner
                }
            }).setNegativeButton("Reset",new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int id){
                    newMedName.setText("");
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    };



}

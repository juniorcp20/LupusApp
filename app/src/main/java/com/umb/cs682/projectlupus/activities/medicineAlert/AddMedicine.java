package com.umb.cs682.projectlupus.activities.medicineAlert;

import com.umb.cs682.projectlupus.R;
import com.umb.cs682.projectlupus.config.AppConfig;
import com.umb.cs682.projectlupus.service.MedicineService;
import com.umb.cs682.projectlupus.service.ReminderService;
import com.umb.cs682.projectlupus.util.Constants;
import com.umb.cs682.projectlupus.util.SharedPreferenceManager;
import com.umb.cs682.projectlupus.util.Utils;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

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

        alertDialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog, int id){
                                    //todo add to db and set selection of spinner
                                }
                            }).setNegativeButton("Reset",new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog, int id){
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
            //todo save to DB
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

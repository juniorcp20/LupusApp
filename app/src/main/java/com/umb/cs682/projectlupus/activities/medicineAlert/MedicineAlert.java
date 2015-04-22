package com.umb.cs682.projectlupus.activities.medicineAlert;

import com.umb.cs682.projectlupus.R;
import com.umb.cs682.projectlupus.activities.main.Home;
import com.umb.cs682.projectlupus.config.AppConfig;
import com.umb.cs682.projectlupus.service.MedicineService;
import com.umb.cs682.projectlupus.util.Constants;
import com.umb.cs682.projectlupus.util.SharedPreferenceManager;
import com.umb.cs682.projectlupus.util.Utils;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Toast;

import java.util.ArrayList;

public class MedicineAlert extends Activity {
    private String medName = null;
    private boolean isInit = SharedPreferenceManager.getBooleanPref(Constants.IS_FIRST_RUN);

    Button addMed;

    private MedicineService medService = AppConfig.getMedicineService();
    private ArrayList<String> strArr;
    private AddMedicineNameAdapter adapter;
    private ListView alertsList;
    private boolean isNew = false;
    private int selTimePos;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_medicine_alert);
        addMed = (Button) findViewById(R.id.med_alert_button);
        addMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMedicine();
            }
        });
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        alertsList = (ListView)findViewById(R.id.add_medname_listview);
        alertsList.setVisibility(View.INVISIBLE);
        strArr = new ArrayList<String>();

        adapter = new AddMedicineNameAdapter(getApplicationContext(),strArr);
        alertsList.setAdapter(adapter);
        alertsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
               // intent.putExtra("Medicine Name",medNames.get(medNameSpinner.getSelectedItemPosition()));
                startActivity(intent.setClass(getApplicationContext(), AddMedicine.class));            }
        });

        medName = getIntent().getStringExtra("Medicine Name");
        if (medName != null){

            adapter.notifyDataSetChanged();
            alertsList.setVisibility(View.VISIBLE);
            Utils.displayToast(this,medName);

        }

        //isInit = getIntent().getBooleanExtra(Constants.IS_INIT, false);
       // if(medName != null) {
            //Log.i(Constants.ACTIVITY_SENSE, medName);
            if (isInit) {
                actionBar.setTitle(R.string.title_init_medicine_alert);
                medService.initMedicineDB();

                //isInit = true;**
            } else {
                actionBar.setTitle(R.string.title_medicine_alert);
            }
        //}
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
        Intent intent = new Intent();
        //intent.putExtra(Constants.IS_INIT, isInit);
        startActivity(intent.setClass(this, AddMedicine.class));
    }

    public class AddMedicineNameAdapter extends ArrayAdapter<String> {
        public AddMedicineNameAdapter(Context context, ArrayList<String> medNames) {
            super(context, R.layout.li_reminder_item, medNames);
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            View customView = inflater.inflate(R.layout.li_reminder_item, parent, false);

            TextView displayTime = (TextView) customView.findViewById(R.id.display_time);
            ImageView actionIcon = (ImageView) customView.findViewById(R.id.delete_icon);
            actionIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    strArr.remove(position);
                    if (strArr.isEmpty()) {
                        alertsList.setVisibility(View.INVISIBLE);
                    }

                    notifyDataSetChanged();

                }
            });
            if (medName != null) {
                displayTime.setText(medName);
            }
            actionIcon.setImageResource(R.drawable.ic_action_discard);
            return customView;
        }
    }

}

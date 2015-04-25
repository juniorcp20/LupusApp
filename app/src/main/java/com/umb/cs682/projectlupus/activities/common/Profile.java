package com.umb.cs682.projectlupus.activities.common;

import com.umb.cs682.projectlupus.R;
import com.umb.cs682.projectlupus.activities.moodAlert.MoodAlert;
import com.umb.cs682.projectlupus.config.AppConfig;
import com.umb.cs682.projectlupus.domain.ProfileBO;
import com.umb.cs682.projectlupus.service.ProfileService;
import com.umb.cs682.projectlupus.util.Constants;
import com.umb.cs682.projectlupus.util.Utils;
import com.umb.cs682.projectlupus.util.SharedPreferenceManager;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;

public class Profile extends Activity {
    //private String parent = null;
    private boolean isInit = SharedPreferenceManager.getBooleanPref(Constants.IS_FIRST_RUN);

    private ProfileService service = AppConfig.getProfileService();

    private EditText etUsername;
    private Spinner spAge;
    private RadioGroup rgGender;
    private Spinner spEthnicity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_profile);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        etUsername = (EditText) findViewById(R.id.et_username);
        spAge = (Spinner) findViewById(R.id.sp_age);
        rgGender = (RadioGroup) findViewById(R.id.rg_gender);
        spEthnicity = (Spinner) findViewById(R.id.sp_ethnicity);

        initAgeSpinner();
        initEthnicitySpinner();

        if(isInit){
            actionBar.setTitle(R.string.title_init_profile);
        }else {
            actionBar.setTitle(R.string.title_profile);
            populateData();
        }
	}

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
       // if(parent.equals(Constants.WELCOME)) {
        if(isInit){
            getMenuInflater().inflate(R.menu.m_action_next, menu);
        }else{
            getMenuInflater().inflate(R.menu.m_action_save, menu);
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
            if(!etUsername.getText().toString().isEmpty()){
                save();
            }
			next();
		}
        if(id == R.id.action_save){
            save();
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

    private void initAgeSpinner(){
        ArrayList<String> ages = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.arr_age)));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.li_spinner, ages);
        spAge.setAdapter(adapter);
    }

    private void initEthnicitySpinner(){
        ArrayList<String> ethnicity = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.arr_ethnicity)));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.li_spinner, ethnicity);
        spEthnicity.setAdapter(adapter);
    }

    private void populateData() {
        ProfileBO data = service.getProfileData();
        etUsername.setText(data.getUserName());
        spAge.setSelection(Utils.getSpinnerIndex(spAge, data.getAge()));
        if(data.getGender() != "Male"){
            rgGender.check(R.id.rb_female);
        }
        spEthnicity.setSelection(Utils.getSpinnerIndex(spEthnicity, data.getEthnicity()));
    }

    public void next(){
        Intent intent = new Intent();
        intent.putExtra(Constants.PARENT_ACTIVITY_NAME, Constants.PROFILE);
        startActivity(intent.setClass(this, MoodAlert.class));
    }

    public void save(){
        boolean isSuccess;
        String username = etUsername.getText().toString();
        String age = spAge.getSelectedItem().toString();
        String ethnicity = spEthnicity.getSelectedItem().toString();

        RadioButton rbGender = (RadioButton) findViewById(rgGender.getCheckedRadioButtonId());
        String gender = rbGender.getText().toString();
        isSuccess = service.addProfileData(username, age, gender, ethnicity);
        if(isSuccess){
            Utils.displayToast(this, "Saved");
        }else{
            Utils.displayToast(this, "Failed, Try Again!");
        }
    }
}

package com.umb.cs682.projectlupus.activities.common;

import com.umb.cs682.projectlupus.R;
import com.umb.cs682.projectlupus.activities.activitySense.ActivitySense;
import com.umb.cs682.projectlupus.activities.medicineAlert.MedicinePopUp;
import com.umb.cs682.projectlupus.activities.moodAlert.MoodAlert;
import com.umb.cs682.projectlupus.activities.moodAlert.MoodPopUp;
import com.umb.cs682.projectlupus.util.Constants;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class About extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_about);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}
}

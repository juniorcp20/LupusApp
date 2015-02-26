package com.umb.cs682.projectlupus.activities.moodAlert;

import com.umb.cs682.projectlupus.R;
import com.umb.cs682.projectlupus.activities.activitySense.ActivitySense;
import com.umb.cs682.projectlupus.util.Constants;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MoodAlert extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_mood_alert);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.init_mood_alert, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_next) {
            next();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

    public void next(){
        Intent intent = new Intent();
        intent.putExtra(Constants.PARENT_ACTIVITY_NAME, "InitMoodAlert");
        startActivity(intent.setClass(this, ActivitySense.class));
    }
}

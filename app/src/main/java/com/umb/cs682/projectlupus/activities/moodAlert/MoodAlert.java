package com.umb.cs682.projectlupus.activities.moodAlert;

import com.umb.cs682.projectlupus.R;
import com.umb.cs682.projectlupus.activities.activitySense.ActivitySense;
import com.umb.cs682.projectlupus.util.Constants;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MoodAlert extends Activity {
    private String parent = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_mood_alert);
        parent = getIntent().getStringExtra(Constants.PARENT_ACTIVITY_NAME);
        Log.i(Constants.MOOD_ALERT, parent);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        if(parent.equals(Constants.PROFILE)){
            actionBar.setTitle(R.string.title_init_mood_alert);
        }else {
            actionBar.setTitle(R.string.title_mood_alert);
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(parent.equals(Constants.PROFILE)) {
            getMenuInflater().inflate(R.menu.m_action_next, menu);
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
            next();
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

    public void next(){
        Intent intent = new Intent();
        intent.putExtra(Constants.PARENT_ACTIVITY_NAME, Constants.MOOD_ALERT);
        startActivity(intent.setClass(this, ActivitySense.class));
    }
}

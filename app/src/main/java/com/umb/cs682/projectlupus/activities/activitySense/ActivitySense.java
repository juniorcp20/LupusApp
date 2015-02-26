package com.umb.cs682.projectlupus.activities.activitySense;

import com.umb.cs682.projectlupus.R;
import com.umb.cs682.projectlupus.util.Constants;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class ActivitySense extends Activity {
    private String parent = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_activity_sense);
        parent = getIntent().getStringExtra(Constants.PARENT_ACTIVITY_NAME);
        Log.i("InitActivitySense", parent);
        ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
        if(parent.equals("InitMoodAlert")){
            actionBar.setTitle(R.string.title_init_activity_sense);
        }else {
            actionBar.setTitle(R.string.title_activity_sense);
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
        if(parent.equals("InitMoodAlert")) {
            getMenuInflater().inflate(R.menu.m_activity_sense, menu);
        }
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

    @Override
    public Intent getParentActivityIntent(){
        Intent newIntent = null;
        newIntent = new Intent(this, getIntent().getClass());
        return newIntent;
    }
}

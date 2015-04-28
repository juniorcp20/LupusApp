package com.umb.cs682.projectlupus.activities.main;

import com.umb.cs682.projectlupus.R;
import com.umb.cs682.projectlupus.activities.common.About;
import com.umb.cs682.projectlupus.activities.common.Help;
import com.umb.cs682.projectlupus.activities.common.Profile;
import com.umb.cs682.projectlupus.activities.common.Settings;
import com.umb.cs682.projectlupus.activities.common.ShareInfo;
import com.umb.cs682.projectlupus.activities.medicineAlert.MedicineAlert;
import com.umb.cs682.projectlupus.activities.moodAlert.MoodAlert;
import com.umb.cs682.projectlupus.activities.activitySense.ActivitySense;
import com.umb.cs682.projectlupus.config.AppConfig;
import com.umb.cs682.projectlupus.service.ActivitySenseService;
import com.umb.cs682.projectlupus.service.MoodLevelService;
import com.umb.cs682.projectlupus.util.Constants;
import com.umb.cs682.projectlupus.util.SharedPreferenceManager;

import android.app.Activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;

public class Home extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks{
    private static final String TAG = ".activities.main";

    private boolean firstRun = true;

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;
    MoodLevelService moodService = AppConfig.getMoodLevelService();
    ActivitySenseService actSenseService = AppConfig.getActivitySenseService();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_home);
        SharedPreferenceManager.setBooleanPref(TAG, Constants.IS_FIRST_RUN, false);
        ActionBar mActionBar = getActionBar();
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        mActionBar.setSelectedNavigationItem(-1);
		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
		    (DrawerLayout) findViewById(R.id.drawer_layout));
        moodService.loadDummyData();
        actSenseService.loadDummyData();
	}


    @Override
	public void onNavigationDrawerItemSelected(int position) {

        if(firstRun == true){
            firstRun = false;
            return;
        }
        Intent intent = new Intent();
        switch (position){
            case 0:
                intent = new Intent(this, MoodAlert.class);
                break;
            case 1:
                intent = new Intent(this, ActivitySense.class);
                break;
            case 2:
                intent = new Intent(this, MedicineAlert.class);
                break;
            case 3:
                intent = new Intent(this, Profile.class);
                break;
            case 4:
                intent = new Intent(this, ShareInfo.class);
                break;
            case 5:
                intent = new Intent(this, Settings.class);
                break;
            case 6:
                intent = new Intent(this, Help.class);
                break;
            case 7:
                intent = new Intent(this, About.class);
                break;
        }
        startActivity(intent);
	}

	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.home, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
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

}

package com.umb.cs682.projectlupus.activities.main;

import com.umb.cs682.projectlupus.R;
import com.umb.cs682.projectlupus.activities.common.About;
import com.umb.cs682.projectlupus.activities.common.Help;
import com.umb.cs682.projectlupus.activities.common.Profile;
import com.umb.cs682.projectlupus.activities.common.Settings;
import com.umb.cs682.projectlupus.activities.common.ShareInfo;
import com.umb.cs682.projectlupus.activities.medicineAlert.AddMedicine;
import com.umb.cs682.projectlupus.activities.moodAlert.MoodAlert;
import com.umb.cs682.projectlupus.activities.activitySense.ActivitySense;
import com.umb.cs682.projectlupus.util.Constants;
import com.umb.cs682.projectlupus.util.SharedPreferenceManager;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_home);
        SharedPreferenceManager.setBooleanPref(TAG,Constants.IS_FIRST_RUN, false);
		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
		    (DrawerLayout) findViewById(R.id.drawer_layout));
	}


    @Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		/*FragmentManager fragmentManager = getFragmentManager();
		fragmentManager
				.beginTransaction()
				.replace(R.id.container,
						PlaceholderFragment.newInstance(position + 1)).commit();*/

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
                intent = new Intent(this, AddMedicine.class);
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

	/*public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mTitle = getString(R.string.title_section1);
			break;
		case 2:
			mTitle = getString(R.string.title_section2);
			break;
		case 3:
			mTitle = getString(R.string.title_section3);
			break;
		}
	}*/

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

	/**
	 * A placeholder fragment containing a simple view.
	 */
	//public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		//private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		/*public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.f_home, container,
					false);
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((Home) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));
		}
	}*/

}

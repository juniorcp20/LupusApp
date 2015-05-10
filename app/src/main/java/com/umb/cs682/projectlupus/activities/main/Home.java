package com.umb.cs682.projectlupus.activities.main;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.umb.cs682.projectlupus.R;
import com.umb.cs682.projectlupus.activities.common.About;
import com.umb.cs682.projectlupus.activities.common.Profile;
import com.umb.cs682.projectlupus.activities.common.Settings;
import com.umb.cs682.projectlupus.activities.medicineAlert.MedicineAlert;
import com.umb.cs682.projectlupus.activities.moodAlert.MoodAlert;
import com.umb.cs682.projectlupus.activities.activitySense.ActivitySense;
import com.umb.cs682.projectlupus.config.LupusMate;;
import com.umb.cs682.projectlupus.service.ActivitySenseService;
import com.umb.cs682.projectlupus.service.MedicineService;
import com.umb.cs682.projectlupus.service.MoodLevelService;
import com.umb.cs682.projectlupus.service.ProfileService;
import com.umb.cs682.projectlupus.util.Constants;
import com.umb.cs682.projectlupus.util.SharedPreferenceManager;

import android.app.Activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import de.greenrobot.dao.DaoException;

public class Home extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks{
    private static final String TAG = ".activities.main";
    String username;
    private boolean firstRun = true;

    private LineChart moodChart = null;
    private LineChart activityChart = null;
    private BarChart medicineChart = null;
    private TextView stepCountText;


	private NavigationDrawerFragment mNavigationDrawerFragment;

	private CharSequence mTitle;

    private MoodLevelService moodLevelService;
    private ActivitySenseService activitySenseService;
    private MedicineService medicineService;
    private ProfileService profileService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_home);
        SharedPreferenceManager.setBooleanPref(TAG, Constants.IS_FIRST_RUN, false);
        ActionBar mActionBar = getActionBar();

        final LupusMate lupusMate = (LupusMate) getApplicationContext();
        moodLevelService = lupusMate.getMoodLevelService();
        activitySenseService = lupusMate.getActivitySenseService();
        medicineService = lupusMate.getMedicineService();
        profileService = lupusMate.getProfileService();

        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        mActionBar.setSelectedNavigationItem(-1);
		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
		    (DrawerLayout) findViewById(R.id.drawer_layout));

        stepCountText = (TextView) findViewById(R.id.tv_home_step_count);
        // Load dummy data for testing
        moodLevelService.loadDummyData();
        activitySenseService.loadDummyData();
        medicineService.loadDummyData();

        Iterator iterator;
        int xIndex;

        // Set up the mood chart
        ArrayList<Entry> timeVsMoodAL = new ArrayList<>();
        TreeMap<Date,Float> timeVsMoodMap = moodLevelService.getTimeVsMoodLevel();
        iterator = timeVsMoodMap.entrySet().iterator();

        ArrayList<String> moodXVals = new ArrayList<>();
        xIndex = 0;

        while (iterator.hasNext()){
            Map.Entry pair = (Map.Entry) iterator.next();
            String[] splitStrings = ((Date) pair.getKey()).toString().split(" ");
            timeVsMoodAL.add(new Entry((Float) pair.getValue(),xIndex ++));
            moodXVals.add(splitStrings[0] + " " + splitStrings[1] + " " + splitStrings[2] + " " + splitStrings[splitStrings.length - 1]);
        }

        LineDataSet timeVsMoodDataset = new LineDataSet(timeVsMoodAL,null);
        timeVsMoodDataset.setColor(getResources().getColor(R.color.darkPurple));
        timeVsMoodDataset.setCircleColor(getResources().getColor(R.color.lightPurple));
        LineData timeVsMoodData = new LineData(moodXVals,timeVsMoodDataset);
        timeVsMoodData.setDrawValues(false);
        moodChart = (LineChart) findViewById(R.id.mood_chart);
        moodChart.setData(timeVsMoodData);
        moodChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        moodChart.setDescription(null);
        moodChart.getAxisRight().setEnabled(false);
        moodChart.getLegend().setEnabled(false);
        moodChart.setGridBackgroundColor(getResources().getColor(android.R.color.transparent));
        moodChart.getXAxis().setTextColor(getResources().getColor(R.color.darkPurple));
        moodChart.getAxisLeft().setTextColor(getResources().getColor(R.color.darkPurple));

        moodChart.getXAxis().setAvoidFirstLastClipping(true);
        moodChart.animateX(2000);
        moodChart.getAxisLeft().setAxisMaxValue(5);
        moodChart.getAxisLeft().setAxisMinValue(1);


        // Set up the activity chart
        ArrayList<Entry> timeVsStepCountAL = new ArrayList<>();
        TreeMap<Date,Integer> timeVsStepCountMap = activitySenseService.getTimeVsStepCount();
        iterator = timeVsStepCountMap.entrySet().iterator();

        ArrayList<String> stepXVals = new ArrayList<>();

        xIndex = 0;

        while (iterator.hasNext()){
            Map.Entry pair = (Map.Entry) iterator.next();
            String[] splitStrings = ((Date) pair.getKey()).toString().split(" ");
            timeVsStepCountAL.add(new Entry(((Integer)pair.getValue()).floatValue(),xIndex ++));
            stepXVals.add(splitStrings[0] + " " + splitStrings[1] + " " + splitStrings[2] + " " + splitStrings[splitStrings.length - 1]);
        }

        LineDataSet timeVsStepCountDataset = new LineDataSet(timeVsStepCountAL,null);
        timeVsStepCountDataset.setColor(getResources().getColor(R.color.darkPurple));
        timeVsStepCountDataset.setCircleColor(getResources().getColor(R.color.lightPurple));
        LineData timeVsStepCountData = new LineData(stepXVals,timeVsStepCountDataset);
        timeVsStepCountData.setDrawValues(false);
        activityChart = (LineChart) findViewById(R.id.activity_chart);
        activityChart.setData(timeVsStepCountData);
        activityChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        activityChart.setDescription(null);
        activityChart.getAxisRight().setEnabled(false);
        activityChart.getLegend().setEnabled(false);
        activityChart.setGridBackgroundColor(getResources().getColor(android.R.color.transparent));
        activityChart.getXAxis().setTextColor(getResources().getColor(R.color.darkPurple));
        activityChart.getAxisLeft().setTextColor(getResources().getColor(R.color.darkPurple));

        activityChart.getXAxis().setAvoidFirstLastClipping(true);
        activityChart.animateX(2000);

        // Set up the medicine chart
        ArrayList<BarEntry> mednameVsTakenPercentageAL = new ArrayList<>();
        TreeMap<String,Float> mednameVsTakenPercentageMap = medicineService.getMednameVsTakenPercentage();
        iterator = mednameVsTakenPercentageMap.entrySet().iterator();
        ArrayList<String> medicineXVals = new ArrayList<>();
        xIndex = 0;
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            mednameVsTakenPercentageAL.add(new BarEntry((Float) pair.getValue(),xIndex ++));
            medicineXVals.add(pair.getKey().toString());
        }
        BarDataSet mednameVsTakenPercentageDataset = new BarDataSet(mednameVsTakenPercentageAL,null);
        mednameVsTakenPercentageDataset.setColor(getResources().getColor(R.color.lightPurple));
        BarData mednameVsTakenPercentageData = new BarData(medicineXVals,mednameVsTakenPercentageDataset);
        mednameVsTakenPercentageData.setValueTextColor(getResources().getColor(R.color.darkPurple));
        medicineChart = (BarChart) findViewById(R.id.medicine_chart);
        medicineChart.setData(mednameVsTakenPercentageData);
        medicineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        medicineChart.setDescription(null);
        medicineChart.getAxisRight().setEnabled(false);
        medicineChart.getLegend().setEnabled(false);
        medicineChart.setGridBackgroundColor(getResources().getColor(android.R.color.transparent));
        medicineChart.getXAxis().setTextColor(getResources().getColor(R.color.darkPurple));
        medicineChart.getAxisLeft().setTextColor(getResources().getColor(R.color.darkPurple));
        medicineChart.animateY(2000);

	}

    @Override
    protected void onStart() {
        super.onStart();
        Date now = new Date();
        activitySenseService.addActSenseData(now);
        String stepCount = String.valueOf(activitySenseService.getStoredStepCount(now));
        stepCountText.setText(stepCount);
        restoreActionBar();
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
                intent = new Intent(this, Settings.class);
                break;
            case 5:
                intent = new Intent(this, About.class);
                break;
        }
        startActivity(intent);
	}

	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
        try {
            username = profileService.getProfileData().getUserName();
        }catch(DaoException e){
            username = "";
        }
        mTitle = "Hi "+ username;
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.m_action_empty, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}


    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);

    }
}
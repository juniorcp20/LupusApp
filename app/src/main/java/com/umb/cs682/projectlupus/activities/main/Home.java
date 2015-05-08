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
import com.github.mikephil.charting.utils.ValueFormatter;
import com.umb.cs682.projectlupus.R;
import com.umb.cs682.projectlupus.activities.common.About;
import com.umb.cs682.projectlupus.activities.common.Profile;
import com.umb.cs682.projectlupus.activities.common.Settings;
import com.umb.cs682.projectlupus.activities.medicineAlert.MedicineAlert;
import com.umb.cs682.projectlupus.activities.moodAlert.MoodAlert;
import com.umb.cs682.projectlupus.activities.activitySense.ActivitySense;
import com.umb.cs682.projectlupus.config.LupusMate;
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
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class Home extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks{
    private static final String TAG = ".activities.main";

    private boolean firstRun = true;

    private LineChart moodChart = null;
    private LineChart activityChart = null;
    private BarChart medicineChart = null;
    private TextView stepCountText;

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
    private MoodLevelService moodLevelService = LupusMate.getMoodLevelService();
    private ActivitySenseService activitySenseService = LupusMate.getActivitySenseService();
    private MedicineService medicineService = LupusMate.getMedicineService();
    private ProfileService profileService = LupusMate.getProfileService();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_home);
        SharedPreferenceManager.setBooleanPref(TAG, Constants.IS_FIRST_RUN, false);
        ActionBar mActionBar = getActionBar();
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        mActionBar.setSelectedNavigationItem(-1);
		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
		mTitle = "Hi "+ profileService.getProfileData().getUserName();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
		    (DrawerLayout) findViewById(R.id.drawer_layout));

        stepCountText = (TextView) findViewById(R.id.tv_home_step_count);
        // Load dummy data for testing
        moodLevelService.loadDummyData();
        activitySenseService.loadDummyData();
        medicineService.loadDummyData();

        Iterator iterator;
        Date date;
        //Calendar calendar = Calendar.getInstance();
        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("M yyyy");

        // Set up the mood chart
        ArrayList<Entry> timeVsMoodAL = new ArrayList<>();
        TreeMap<Date,Float> timeVsMoodMap = moodLevelService.getTimeVsMoodLevel();
        iterator = timeVsMoodMap.entrySet().iterator();

        ArrayList<String> moodXVals = new ArrayList<>();
        /*Date javaStartDate = timeVsMoodMap.firstKey();
        MutableDateTime jodaStartDate = new MutableDateTime();
        jodaStartDate.setDate(javaStartDate.getYear(), javaStartDate.getMonth(), javaStartDate.getDate());
        DateTime now = DateTime.now();
        for (int i = 0;i < Days.daysBetween(jodaStartDate,now).getDays();i ++) {
            moodXVals.add();
        }*/

        while (iterator.hasNext()){
            Map.Entry pair = (Map.Entry) iterator.next();
            //calendar.setTime((Date)pair.getKey());
            date = (Date) pair.getKey();
            String[] splitStrings = date.toString().split(" ");
            timeVsMoodAL.add(new Entry((Float) pair.getValue(),date.getDate() - 1));
            moodXVals.add(splitStrings[0] + " " + splitStrings[1] + " " + splitStrings[2] + " " + splitStrings[splitStrings.length - 1]);
        }

        LineDataSet timeVsMoodDataset = new LineDataSet(timeVsMoodAL,null);
        LineData timeVsMoodData = new LineData(moodXVals,timeVsMoodDataset);
        moodChart = (LineChart) findViewById(R.id.mood_chart);
        moodChart.setData(timeVsMoodData);
        moodChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        moodChart.setDescription(null);
        moodChart.getAxisRight().setEnabled(false);
        moodChart.getLegend().setEnabled(false);

        //moodChart.setScaleEnabled(true);
        //moodChart.setDragEnabled(true);

        //testing
        /*ArrayList<Entry> timeVsMoodAL = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<>();
        for (int i = 0;i < 5; i ++){
            timeVsMoodAL.add(new Entry(i*i,i));
            xVals.add(i + "");
        }
        LineDataSet timeVsMoodDataset = new LineDataSet(timeVsMoodAL,"what does this do?");
        LineData timeVsMoodData = new LineData(xVals,timeVsMoodDataset);
        moodChart = (LineChart) findViewById(R.id.mood_chart);
        moodChart.setData(timeVsMoodData);*/

        // Set up the activity chart
        ArrayList<Entry> timeVsStepCountAL = new ArrayList<>();
        TreeMap<Date,Integer> timeVsStepCountMap = activitySenseService.getTimeVsStepCount();
        iterator = timeVsStepCountMap.entrySet().iterator();
        ArrayList<String> stepXVals = new ArrayList<>();
        while (iterator.hasNext()){
            Map.Entry pair = (Map.Entry) iterator.next();
            date = (Date) pair.getKey();
            String[] splitStrings = date.toString().split(" ");
            timeVsStepCountAL.add(new Entry(((Integer)pair.getValue()).floatValue(), date.getDate() - 1));
            stepXVals.add(splitStrings[0] + " " + splitStrings[1] + " " + splitStrings[2] + " " + splitStrings[splitStrings.length - 1]);
        }
        LineDataSet timeVsStepCountDataset = new LineDataSet(timeVsStepCountAL,null);
        LineData timeVsStepCountData = new LineData(stepXVals,timeVsStepCountDataset);
        activityChart = (LineChart) findViewById(R.id.activity_chart);
        activityChart.setData(timeVsStepCountData);
        activityChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        activityChart.setDescription(null);
        activityChart.getAxisRight().setEnabled(false);
        activityChart.getLegend().setEnabled(false);

        // Set up the medicine chart
        ArrayList<BarEntry> mednameVsTakenPercentageAL = new ArrayList<>();
        TreeMap<String,Float> mednameVsTakenPercentageMap = medicineService.getMednameVsTakenPercentage();
        iterator = mednameVsTakenPercentageMap.entrySet().iterator();
        ArrayList<String> medicineXVals = new ArrayList<>();
        int i = 0;
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            mednameVsTakenPercentageAL.add(new BarEntry((Float) pair.getValue(),i++));
            medicineXVals.add(pair.getKey().toString());
        }
        BarDataSet mednameVsTakenPercentageDataset = new BarDataSet(mednameVsTakenPercentageAL,null);
        BarData mednameVsTakenPercentageData = new BarData(medicineXVals,mednameVsTakenPercentageDataset);
        medicineChart = (BarChart) findViewById(R.id.medicine_chart);
        medicineChart.setData(mednameVsTakenPercentageData);
        medicineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        medicineChart.setDescription(null);
        medicineChart.getAxisRight().setEnabled(false);
        medicineChart.getLegend().setEnabled(false);

	}

    @Override
    protected void onStart() {
        super.onStart();
        Date now = new Date();
        activitySenseService.addActSenseData(now);
        String stepCount = String.valueOf(activitySenseService.getActSenseDatabyDate(now).getStepCount());
        stepCountText.setText(stepCount);
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

}

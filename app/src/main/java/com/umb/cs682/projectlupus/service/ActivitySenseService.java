package com.umb.cs682.projectlupus.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;


import com.umb.cs682.projectlupus.activities.activitySense.ActivitySense;
import com.umb.cs682.projectlupus.db.dao.ActivitySenseDao;
import com.umb.cs682.projectlupus.domain.ActivitySenseBO;
import com.umb.cs682.projectlupus.util.DateTimeUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

import de.greenrobot.dao.query.CountQuery;
import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.Query;

public class ActivitySenseService {
    private static final String TAG = "projectlupus.service";
    private static final String INTENT_FILTER = "com.umb.cs682.projectlupus.service.ActivitySense";
    private static int stepCount;
    private boolean isBound = false;

    private Context context;
    private ActivitySenseDao activitySenseDao;

    private BroadcastReceiver receiver;
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;

    public ActivitySenseService(Context context, ActivitySenseDao activitySenseDao){
        this.activitySenseDao = activitySenseDao;
        this.context = context;
    }

    /*Alarm Manager Setup*/
    public void startAlarm(){
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(), AlarmManager.INTERVAL_HOUR, pendingIntent);
    }

    public void stopAlarm(){
        alarmManager.cancel(pendingIntent);
    }

    public void setupAlarm(){
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                addActSenseData(new Date());
            }
        };

        context.registerReceiver(receiver, new IntentFilter(INTENT_FILTER));
        pendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(INTENT_FILTER),0);
        alarmManager = (AlarmManager)(context.getSystemService(Context.ALARM_SERVICE));
    }


    /* Database Operations*/
    public void loadDummyData(){
        ActivitySenseBO bo;
        if(getCount() == 0) {
            Calendar cal = Calendar.getInstance();
            Random random = new Random();
            for (int i = 1; i <= 31; i++) {
                cal.set(2015, 3, i);
                bo = new ActivitySenseBO(null, random.nextInt(2000), DateTimeUtil.toDate(new Date(cal.getTimeInMillis())));
                activitySenseDao.insert(bo);
            }
            for (int i = 1; i <= 11; i++) {
                cal.set(2015, 4, i);
                bo = new ActivitySenseBO(null, random.nextInt(2000), DateTimeUtil.toDate(new Date(cal.getTimeInMillis())));
                activitySenseDao.insert(bo);
            }
        }
    }

    public int getStoredStepCount(Date date){
        //updateActSenseData(date);
        return getActSenseDatabyDate(date).getStepCount();
    }

    public void addActSenseData(Date date){
        ActivitySenseBO bo;
        Log.i(TAG, "Saved to DB");
        CountQuery query = activitySenseDao.queryBuilder().where(ActivitySenseDao.Properties.Date.eq(DateTimeUtil.toDate(date))).buildCount();
        if(!(query.count() == 0)) {
            updateActSenseData(date);
        }else{
            ActivitySense.resetStepCount();
            bo = new ActivitySenseBO(null, 0, DateTimeUtil.toDate(date));
            activitySenseDao.insert(bo);
            bo = getActSenseDatabyDate(date);
            Log.i(TAG, "Added activity sense date. Step count: "+bo.getStepCount()+" Date: "+DateTimeUtil.toDateTime(bo.getDate()));
        }
    }

    public void updateActSenseData(Date date){
        ActivitySenseBO bo;
        bo = getActSenseDatabyDate(date);
        int currentCount = ActivitySense.getStepCount();
        int savedCount = bo.getStepCount();
        if( currentCount < savedCount){
            bo.setStepCount(savedCount+currentCount);
        }else {
            bo.setStepCount(currentCount);
        }
        activitySenseDao.update(bo);
        Log.i(TAG, "Updated activity sense date. Step count: " + ActivitySense.getStepCount() + " Date: " + DateTimeUtil.toDateTime(bo.getDate()));
    }

    public ActivitySenseBO getActSenseDatabyDate(Date date){
        Log.i(TAG, "Retrieving from DB");
        ActivitySenseBO bo = activitySenseDao.queryBuilder().where(ActivitySenseDao.Properties.Date.eq(DateTimeUtil.toDate(date))).uniqueOrThrow();
        return bo;
    }

    public void deleteData(Date date){
        Log.i(TAG, "Deleting Data");
        DeleteQuery query = activitySenseDao.queryBuilder().where(ActivitySenseDao.Properties.Date.eq(DateTimeUtil.toDate(date))).buildDelete();
        query.executeDeleteWithoutDetachingEntities();
    }

    private long getCount(){
        CountQuery query = activitySenseDao.queryBuilder().buildCount();
        return query.count();
    }

    public List<ActivitySenseBO> getAllData() {
        Query query = activitySenseDao.queryBuilder().build();
        return query.list();
    }

    public TreeMap<Date, Integer> getTimeVsStepCount(){
        TreeMap<Date, Integer> timeVsStepCountMap = new TreeMap<>();
        for(ActivitySenseBO currBO : getAllData()){
            timeVsStepCountMap.put(currBO.getDate(), currBO.getStepCount());
        }
        return timeVsStepCountMap;
    }
}

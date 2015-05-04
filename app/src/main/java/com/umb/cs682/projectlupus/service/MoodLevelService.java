package com.umb.cs682.projectlupus.service;

import android.content.Context;
import android.database.Cursor;

import com.umb.cs682.projectlupus.config.LupusMate;
import com.umb.cs682.projectlupus.db.dao.MoodLevelDao;
import com.umb.cs682.projectlupus.db.helpers.DaoSession;
import com.umb.cs682.projectlupus.domain.MoodLevelBO;
import com.umb.cs682.projectlupus.util.DateTimeUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import de.greenrobot.dao.query.CountQuery;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

public class MoodLevelService {
    public static final String TAG = "service.moodLevel";
    private static final String SQL_DISTINCT_DATES= "SELECT DISTINCT "+MoodLevelDao.Properties.Date.columnName+" FROM "+MoodLevelDao.TABLENAME;

    private Context context;
    private MoodLevelDao moodLevelDao;

    private MoodLevelBO bo;
    private QueryBuilder queryBuilder;

    public MoodLevelService(Context context, MoodLevelDao moodLevelDao){
        this.context = context;
        this.moodLevelDao = moodLevelDao;
        queryBuilder = moodLevelDao.queryBuilder();
    }

    public void loadDummyData(){
        if(getCount() == 0) {
            Calendar cal = Calendar.getInstance();
            for (int i = 1; i < 5; i++) {
                cal.set(2015, 4, i);
                bo = new MoodLevelBO(null, 1, DateTimeUtil.toDate(new Date(cal.getTimeInMillis())), i);
                moodLevelDao.insert(bo);
            }
        }
    }

    public void addMoodLevel(int reminderID, int moodLevel){
        bo = new MoodLevelBO(null, reminderID, DateTimeUtil.toDate(new Date()), moodLevel);
        moodLevelDao.insert(bo);
    }

    public List<MoodLevelBO> getAllData(){
        Query query = queryBuilder.build();
        return query.list();
    }

    public TreeMap<Date, Long> getTimeVsMoodLevel(){
        // Calculates the avg moodlevel for each day, because there might be multiple reminders each day.
        TreeMap<Date, Long> timeVsMoodLevelMap = new TreeMap<>();
        Long avg;
        for(Date date : getDistinctDates()){
            avg = calculateAvgMoodLevel(date);
            timeVsMoodLevelMap.put(date, avg);
        }
        return timeVsMoodLevelMap;
    }

    private List<MoodLevelBO> getDataByDate(Date date){
        Query query = queryBuilder.where(MoodLevelDao.Properties.Date.eq(DateTimeUtil.toDate(date))).build();
        return query.list();
    }

    public static List<Date> getDistinctDates() {
        DaoSession session = LupusMate.getDaoSession();
        ArrayList<Date> result = new ArrayList<>();
        Cursor c = session.getDatabase().rawQuery(SQL_DISTINCT_DATES, null);
        if (c.moveToFirst()) {
            do {
                result.add(DateTimeUtil.toDate(c.getString(0)));
            } while (c.moveToNext());
        }
        c.close();
        return result;
    }

    private long getCount(){
        CountQuery query = moodLevelDao.queryBuilder().buildCount();
        return query.count();
    }

    private Long calculateAvgMoodLevel(Date date) {
        List<MoodLevelBO> data = new ArrayList<>();
        data = getDataByDate(date);
        int sum = 0;
        for(MoodLevelBO bo : data){
            sum = sum + bo.getMoodLevel();
        }
        long avg = sum/data.size();
        return avg;
    }
}

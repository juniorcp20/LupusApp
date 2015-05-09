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
import java.util.Iterator;
import java.util.List;
import java.util.Random;
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
            Random random = new Random();
            for (int i = 1; i <= 31; i++) {
                cal.set(2015, 3, i, 7, 30);
                bo = new MoodLevelBO(null, 1,DateTimeUtil.toDate(new Date(cal.getTimeInMillis())), random.nextInt(6)); //DateTimeUtil.toDate(new Date(cal.getTimeInMillis()))
                moodLevelDao.insert(bo);
                cal.set(2015, 3, i, 18, 30);
                bo = new MoodLevelBO(null, 1,DateTimeUtil.toDate(new Date(cal.getTimeInMillis())), random.nextInt(6)); //DateTimeUtil.toDate(new Date(cal.getTimeInMillis()))
                moodLevelDao.insert(bo);
            }
            for (int i = 1; i <= 11; i++) {
                cal.set(2015, 4, i, 7, 30);
                bo = new MoodLevelBO(null, 1,DateTimeUtil.toDate(new Date(cal.getTimeInMillis())), random.nextInt(6)); //DateTimeUtil.toDate(new Date(cal.getTimeInMillis()))
                moodLevelDao.insert(bo);
                cal.set(2015, 4, i, 18, 30);
                bo = new MoodLevelBO(null, 1,DateTimeUtil.toDate(new Date(cal.getTimeInMillis())), random.nextInt(6)); //DateTimeUtil.toDate(new Date(cal.getTimeInMillis()))
                moodLevelDao.insert(bo);
            }
        }
    }

    public void addMoodLevel(int reminderID, int moodLevel){
        bo = new MoodLevelBO(null, reminderID, DateTimeUtil.toDateTime(new Date()), moodLevel);
        moodLevelDao.insert(bo);
    }

    public List<MoodLevelBO> getAllData(){
        Query query = queryBuilder.build();
        return query.list();
    }

    public TreeMap<Date, Float> getTimeVsMoodLevel(){
        // Calculates the avg moodlevel for each day, because there might be multiple reminders each day.
        TreeMap<Date, Float> timeVsMoodLevelMap = new TreeMap<>();
        Float avg;
        // Old Averaging method, not working
        /*for(Date date : getDistinctDates()){
            avg = calculateAvgMoodLevel(date);
            timeVsMoodLevelMap.put(date, avg);
        }*/

        List<MoodLevelBO> allData = getAllData();
        for (int i = 0;i < allData.size() - 1;i ++) {
            if(DateTimeUtil.toDate(allData.get(i).getDate()).equals(DateTimeUtil.toDate(allData.get(i + 1).getDate()))) {
                Date tempForComparison = DateTimeUtil.toDate(allData.get(i).getDate());
                int j = i + 1;
                avg = (float) allData.get(i).getMoodLevel();
                do {
                    avg += allData.get(j).getMoodLevel();
                    j ++;
                } while (j < allData.size() && tempForComparison.equals(DateTimeUtil.toDate(allData.get(j).getDate())));
                avg /= (j - i);
                i = j - 1;
            }
            else {
                avg = (float) allData.get(i).getMoodLevel();
            }
            timeVsMoodLevelMap.put(allData.get(i).getDate(),avg);
        }

        //testing
        /*for (MoodLevelBO currBO:getAllData()) {
            avg = (float) currBO.getMoodLevel();
            timeVsMoodLevelMap.put(currBO.getDate(),avg);
        }*/
        return timeVsMoodLevelMap;
    }

    // Not working
    /*private List<MoodLevelBO> getDataByDate(Date date){
        Query query = queryBuilder.where(MoodLevelDao.Properties.Date.eq(DateTimeUtil.toDate(date))).build();
        return query.list();
    }*/

    /*public static List<Date> getDistinctDates() {
        DaoSession session = LupusMate.getDaoSession();
        ArrayList<Date> result = new ArrayList<>();
        Cursor c = session.getDatabase().rawQuery(SQL_DISTINCT_DATES, null);
        if (c.moveToFirst()) {
            do {
                result.add(new Date(Long.parseLong(c.getString(0))));
            } while (c.moveToNext());
        }
        c.close();
        return result;
    }*/

    private long getCount(){
        CountQuery query = moodLevelDao.queryBuilder().buildCount();
        return query.count();
    }

    /*private Float calculateAvgMoodLevel(Date date) {
        List<MoodLevelBO> data = new ArrayList<>();
        data = getDataByDate(date);
        int sum = 0;
        for(MoodLevelBO bo : data){
            sum = sum + bo.getMoodLevel();
        }
        float avg = (float) sum / data.size();
        return avg;
    }*/
}

package com.umb.cs682.projectlupus.service;

import android.content.Context;

import com.umb.cs682.projectlupus.db.dao.MoodLevelDao;
import com.umb.cs682.projectlupus.domain.MoodLevelBO;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.dao.query.Query;

public class MoodLevelService {
    public static final String TAG = "service.moodLevel";

    private Context context;
    private MoodLevelDao moodLevelDao;

    private MoodLevelBO bo;

    public MoodLevelService(Context context, MoodLevelDao moodLevelDao){
        this.context = context;
        this.moodLevelDao = moodLevelDao;
    }

    public List<MoodLevelBO> getAllData(){
        Query query = moodLevelDao.queryBuilder().build();
        return query.list();
    }

    public HashMap<Date, Integer> getTimeVsMoodLevel(){
        HashMap<Date, Integer> timeVsMoodLevelMap = new HashMap<>();
        for(MoodLevelBO currBO : getAllData()){
            timeVsMoodLevelMap.put(currBO.getDate(), currBO.getMoodLevel());
        }
        return timeVsMoodLevelMap;
    }
}

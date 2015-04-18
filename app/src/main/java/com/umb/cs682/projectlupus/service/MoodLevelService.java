package com.umb.cs682.projectlupus.service;

import android.content.Context;

import com.umb.cs682.projectlupus.db.dao.MoodLevelDao;
import com.umb.cs682.projectlupus.domain.MoodLevelBO;

public class MoodLevelService {
    public static final String TAG = "service.moodLevel";

    private Context context;
    private MoodLevelDao moodLevelDao;

    private MoodLevelBO bo;

    public MoodLevelService(Context context, MoodLevelDao moodLevelDao){
        this.context = context;
        this.moodLevelDao = moodLevelDao;
    }
}

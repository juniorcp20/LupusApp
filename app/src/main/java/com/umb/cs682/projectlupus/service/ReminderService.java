package com.umb.cs682.projectlupus.service;

import android.content.Context;

import com.umb.cs682.projectlupus.db.dao.ReminderDao;
import com.umb.cs682.projectlupus.domain.ReminderBO;
import com.umb.cs682.projectlupus.util.Constants;
import com.umb.cs682.projectlupus.util.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.greenrobot.dao.DaoException;
import de.greenrobot.dao.query.CountQuery;
import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

public class ReminderService {
    public static final String TAG = "service.reminder";
    public static final int MOOD_REMINDER = Constants.MOOD_REMINDER;
    public static final int MED_REMINDER = Constants.MED_REMINDER;
    private boolean isSuccess = false;

    private Context context;
    private ReminderDao reminderDao;

    private ReminderBO bo;

    private Query getMoodRemindersQuery;
    private Query getMedRemindersQuery;

    public ReminderService(Context context, ReminderDao reminderDao){
        this.context = context;
        this.reminderDao = reminderDao;
        getMoodRemindersQuery = reminderDao.queryBuilder().where(ReminderDao.Properties.TypeId.eq(MOOD_REMINDER)).orderAsc(ReminderDao.Properties.ReminderTime).build();
        getMedRemindersQuery = reminderDao.queryBuilder().where(ReminderDao.Properties.TypeId.eq(MED_REMINDER)).orderAsc(ReminderDao.Properties.ReminderTime).build();
    }
    /*Functions common for both Mood and Medicine Alerts */

    public Date getReminderTimeByID(long id){
        return reminderDao.queryBuilder().where(ReminderDao.Properties.Id.eq(id)).unique().getReminderTime();
    }

    /* Mood Reminders */

    public long addMoodReminder(String time){
        long id = -1;
        bo = new ReminderBO(null, MOOD_REMINDER, -1,getReminderName(MOOD_REMINDER),
        DateUtil.toTime(time),Constants.REM_STATUS_ACTIVE);
        long countBeforeAdd = getRowCount(MOOD_REMINDER);
        if(getRowCount(MOOD_REMINDER, time) == 0) {
            reminderDao.insert(bo);
        }else{
            throw new DaoException("Reminder already set for this time!");
        }
        if(getRowCount(MOOD_REMINDER) > countBeforeAdd){
            id = getMoodReminder(time).getId();
        }
        return id;
    }

    public void editMoodReminder(long id,String updatedTime){
        bo = getMoodReminder(id);
        bo.setReminderTime(DateUtil.toTime(updatedTime));
        reminderDao.update(bo);
    }

    public void updateMoodReminderStatus(long id, String status){
        bo = getMoodReminder(id);
        bo.setStatus(status);
        reminderDao.update(bo);
    }

    public void deleteMoodReminder(long id){
        DeleteQuery query = reminderDao.queryBuilder().where(ReminderDao.Properties.Id.eq(id)).buildDelete();
        query.executeDeleteWithoutDetachingEntities();
    }

    public ReminderBO getMoodReminder(String time){
        return reminderDao.queryBuilder().where(ReminderDao.Properties.ReminderTime.eq(DateUtil.toTime(time)), ReminderDao.Properties.TypeId.eq(MOOD_REMINDER)).unique();
    }

    public ReminderBO getMoodReminder(long id){
        return reminderDao.queryBuilder().where(ReminderDao.Properties.Id.eq(id)).uniqueOrThrow();
    }

    public List<ReminderBO> getMoodReminders(){
        return getMoodRemindersQuery.list();
    }

    public ArrayList<Long> getAllMoodReminderIDs(){
        ArrayList<Long> ids = new ArrayList<>();
        for(ReminderBO currBO : getMoodReminders()){
            ids.add(currBO.getId());
        }
        return ids;
    }

    /* Private methods */

    private String getReminderName(int type_id) {
        StringBuilder name = null;
        if(type_id == MOOD_REMINDER){
            name = new StringBuilder("MOOD_ALERT");
            name.append(getRowCount(type_id));
        }else{
            name = new StringBuilder("MED_ALERT");
            name.append(getRowCount(type_id));
        }
        CountQuery query = reminderDao.queryBuilder().where(ReminderDao.Properties.ReminderName.eq(name)).buildCount();
        if(query.count() != 0){
            name.replace(name.length()-1, name.length(),String.valueOf(query.count()+1));
        }
        return name.toString();
    }

    private long getRowCount(int type_id){
        CountQuery query = reminderDao.queryBuilder().where(ReminderDao.Properties.TypeId.eq(type_id)).buildCount();
        return query.count();
    }

    private long getRowCount(int type_id, String time){
        CountQuery query = reminderDao.queryBuilder().where(ReminderDao.Properties.ReminderTime.eq(DateUtil.toTime(time)), ReminderDao.Properties.TypeId.eq(type_id)).buildCount();
        return query.count();
    }
}

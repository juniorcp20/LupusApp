package com.umb.cs682.projectlupus.service;

import android.content.Context;
import android.util.Log;

import com.umb.cs682.projectlupus.config.LupusMate;
import com.umb.cs682.projectlupus.db.dao.ReminderDao;
import com.umb.cs682.projectlupus.domain.ReminderBO;
import com.umb.cs682.projectlupus.util.Constants;
import com.umb.cs682.projectlupus.util.DateTimeUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.greenrobot.dao.DaoException;
import de.greenrobot.dao.query.CountQuery;
import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.Query;

public class ReminderService {
    public static final String TAG = "projectlupus.service";
    public static final int MOOD_REMINDER = Constants.MOOD_REMINDER;
    public static final int MED_REMINDER = Constants.MED_REMINDER;

    private boolean isSuccess = false;

    private Context context;
    private ReminderDao reminderDao;

    private ReminderBO bo;

    private Query getMoodRemindersQuery;
    private Query getMedRemindersQuery;


    private MedicineService medicineService;

    public ReminderService(Context context, ReminderDao reminderDao){
        this.context = context;
        this.reminderDao = reminderDao;
        getMoodRemindersQuery = reminderDao.queryBuilder().where(ReminderDao.Properties.TypeId.eq(MOOD_REMINDER)).orderAsc(ReminderDao.Properties.ReminderTime).build();
        getMedRemindersQuery = reminderDao.queryBuilder().where(ReminderDao.Properties.TypeId.eq(MED_REMINDER)).orderAsc(ReminderDao.Properties.ReminderTime).build();

        final LupusMate lupusMate = (LupusMate) context.getApplicationContext();
        medicineService = lupusMate.getMedicineService();

    }
    /*Functions common for both Mood and Medicine Alerts */

    public Date getReminderTimeByID(long id){
        return reminderDao.queryBuilder().where(ReminderDao.Properties.Id.eq(id)).unique().getReminderTime();
    }

    public ReminderBO getReminder(long id){
        return reminderDao.queryBuilder().where(ReminderDao.Properties.Id.eq(id)).uniqueOrThrow();
    }

    public void updateReminderStatus(long id, String status){
        bo = getReminder(id);
        bo.setStatus(status);
        reminderDao.update(bo);
        Log.i(TAG, "Reminder status: " + status);
    }

    /* Mood Reminders */

    public long addMoodReminder(String time){
        long id = -1;
        bo = new ReminderBO(null, MOOD_REMINDER, -1,null,
        DateTimeUtil.toTime(time), Constants.REM_STATUS_CREATED);
        long countBeforeAdd = getRowCount(MOOD_REMINDER);
        if(getRowCount(MOOD_REMINDER, time) == 0) {
            reminderDao.insert(bo);
        }else{
            throw new DaoException("Reminder already set for this time!");
        }
        if(getRowCount(MOOD_REMINDER) > countBeforeAdd){
            bo = getMoodReminder(time);
            id = bo.getId();
        }
        Log.i(TAG, "Mood reminder saved. Reminder ID: "+id+" Time: "+bo.getReminderTime());
        return id;
    }

    public void editMoodReminder(long id,String updatedTime){
        bo = getMoodReminder(id);
        bo.setReminderTime(DateTimeUtil.toTime(updatedTime));
        reminderDao.update(bo);
        Log.i(TAG, "Updated mood reminder time to: " + updatedTime);
    }

    public void updateMoodReminderStatus(long id, String status){
        bo = getMoodReminder(id);
        bo.setStatus(status);
        reminderDao.update(bo);
        bo = getMoodReminder(id);
        Log.i(TAG, "Reminder status: "+bo.getStatus());
    }

    public void deleteMoodReminder(long id){
        DeleteQuery query = reminderDao.queryBuilder().where(ReminderDao.Properties.Id.eq(id)).buildDelete();
        query.executeDeleteWithoutDetachingEntities();
        Log.i(TAG, "Deleted from database. reminder ID: "+id);
    }

    public ReminderBO getMoodReminder(String time){
        return reminderDao.queryBuilder().where(ReminderDao.Properties.ReminderTime.eq(DateTimeUtil.toTime(time)), ReminderDao.Properties.TypeId.eq(MOOD_REMINDER)).unique();
    }

    public ReminderBO getMoodReminder(long id){
        return reminderDao.queryBuilder().where(ReminderDao.Properties.Id.eq(id)).uniqueOrThrow();
    }

    public List<ReminderBO> getMoodReminders(){
        return getMoodRemindersQuery.list();
    }

    public List<ReminderBO> getMoodRemindersForCurrentThread(){
        return getMoodRemindersQuery.forCurrentThread().list();
    }

    public ArrayList<Long> getAllMoodReminderIDs(){
        ArrayList<Long> ids = new ArrayList<>();
        for(ReminderBO currBO : getMoodReminders()){
            ids.add(currBO.getId());
        }
        return ids;
    }

    /* Medicine Reminders*/

    public long addMedReminder(long medID, String dayOrDate, String time){
        long id = -1;
        bo = new ReminderBO(null, MED_REMINDER, medID,dayOrDate,
                DateTimeUtil.toTime(time),Constants.REM_STATUS_CREATED);
        long countBeforeAdd = getRowCount(MED_REMINDER);
        if(getRowCount(MED_REMINDER, medID, time) == 0) {
            reminderDao.insert(bo);
        }else{
            throw new DaoException("Reminder already set for this time!");
        }
        if(getRowCount(MED_REMINDER) > countBeforeAdd){
            id = getMedReminder(medID, time).getId();
        }
        Log.i(TAG, "Medicine reminder saved. Reminder ID: "+id+" Time: "+bo.getReminderTime());
        return id;
    }

    public void editMedReminder(long id,String updatedTime){
        bo = getMedReminder(id);
        bo.setReminderTime(DateTimeUtil.toTime(updatedTime));
        reminderDao.update(bo);
        Log.i(TAG, "Updated medicine reminder time to: "+updatedTime);
    }

    public void editMedReminder(long id, long medID, String dayOrDate){
        bo = getMedReminder(id);
        bo.setMedId(medID);
        bo.setReminderDayOrDate(dayOrDate);
        reminderDao.update(bo);
        Log.i(TAG, "Updated medicine reminder interval instant: "+dayOrDate);
    }

    public void updateMedReminderStatus(long id, String status){
        bo = getMedReminder(id);
        bo.setStatus(status);
        reminderDao.update(bo);
        Log.i(TAG, "Reminder status: " + bo.getStatus());
    }

    public void deleteMedReminder(long id){
        DeleteQuery query = reminderDao.queryBuilder().where(ReminderDao.Properties.Id.eq(id)).buildDelete();
        query.executeDeleteWithoutDetachingEntities();
        Log.i(TAG, "Deleted from database. reminder ID: " + id);
    }

    public void deleteMedReminderByMedId(long medID){
        DeleteQuery query = reminderDao.queryBuilder().where(ReminderDao.Properties.MedId.eq(medID)).buildDelete();
        query.executeDeleteWithoutDetachingEntities();
        Log.i(TAG, "Deleted reminder from database. medicine ID: " + medID);
    }

    public ReminderBO getMedReminder(long medID, String time){
        return reminderDao.queryBuilder().where(ReminderDao.Properties.MedId.eq(medID), ReminderDao.Properties.ReminderTime.eq(DateTimeUtil.toTime(time)), ReminderDao.Properties.TypeId.eq(MED_REMINDER)).unique();
    }

    public ReminderBO getMedReminder(long id){
        return reminderDao.queryBuilder().where(ReminderDao.Properties.TypeId.eq(MED_REMINDER),ReminderDao.Properties.Id.eq(id)).uniqueOrThrow();
    }

    public List<ReminderBO> getMedReminders(){
        return getMedRemindersQuery.list();
    }

    public List<ReminderBO> getMedRemindersForCurrentThread(){
        return getMedRemindersQuery.forCurrentThread().list();
    }

    public List<ReminderBO> getMedReminders(long medID){
        Query query = reminderDao.queryBuilder().where(ReminderDao.Properties.TypeId.eq(MED_REMINDER), ReminderDao.Properties.MedId.eq(medID)).build();
        return query.list();
    }

    public ArrayList<Long> getAllMedReminderIDs(){
        ArrayList<Long> ids = new ArrayList<>();
        for(ReminderBO currBO : getMedReminders()){
            ids.add(currBO.getId());
        }
        return ids;
    }

    public ArrayList<Long> getAllMedReminderIDs(long medID){
        ArrayList<Long> ids = new ArrayList<>();
        for(ReminderBO currBO : getMedReminders(medID)){
            ids.add(currBO.getId());
        }
        return ids;
    }

    public ArrayList<String> getMedicinesWithReminders(){
        ArrayList<String> medNames = new ArrayList<>();
        for(ReminderBO currBO : getMedReminders()){
            String currMedName = medicineService.getMedicine(currBO.getMedId()).getMedName();
            if(!medNames.contains(currMedName)) {
                medNames.add(currMedName);
            }
        }
        return medNames;
    }

    /* Private methods */

    private long getRowCount(int type_id){
        CountQuery query = reminderDao.queryBuilder().where(ReminderDao.Properties.TypeId.eq(type_id)).buildCount();
        return query.count();
    }

    private long getRowCount(int type_id, String time){
        CountQuery query = reminderDao.queryBuilder().where(ReminderDao.Properties.ReminderTime.eq(DateTimeUtil.toTime(time)), ReminderDao.Properties.TypeId.eq(type_id)).buildCount();
        return query.count();
    }

    private long getRowCount(int type_id, long medID, String time) {
        CountQuery query = reminderDao.queryBuilder().where(ReminderDao.Properties.ReminderTime.eq(DateTimeUtil.toTime(time)), ReminderDao.Properties.TypeId.eq(type_id), ReminderDao.Properties.MedId.eq(medID)).buildCount();
        return query.count();
    }

}

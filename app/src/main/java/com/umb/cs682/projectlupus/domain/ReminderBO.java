package com.umb.cs682.projectlupus.domain;

import java.util.List;

import com.umb.cs682.projectlupus.db.dao.MoodLevelDao;
import com.umb.cs682.projectlupus.db.dao.ReminderDao;
import com.umb.cs682.projectlupus.db.helpers.DaoSession;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoException;


public class ReminderBO {

    private Long id;
    private Integer typeId;
    private long medId;
    private String reminderDayOrDate;
    private java.util.Date reminderTime;
    private String status;

    private transient DaoSession daoSession;

    private transient ReminderDao myDao;

    private List<MoodLevelBO> moodReminders;

    public ReminderBO() {
    }

    public ReminderBO(Long id) {
        this.id = id;
    }

    public ReminderBO(Long id, Integer typeId, long medId, String reminderDayOrDate, java.util.Date reminderTime, String status) {
        this.id = id;
        this.typeId = typeId;
        this.medId = medId;
        this.reminderDayOrDate = reminderDayOrDate;
        this.reminderTime = reminderTime;
        this.status = status;
    }

    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getReminderDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public long getMedId() {
        return medId;
    }

    public void setMedId(long medId) {
        this.medId = medId;
    }

    public String getReminderDayOrDate() {
        return reminderDayOrDate;
    }

    public void setReminderDayOrDate(String reminderDayOrDate) {
        this.reminderDayOrDate = reminderDayOrDate;
    }

    public java.util.Date getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(java.util.Date reminderTime) {
        this.reminderTime = reminderTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<MoodLevelBO> getMoodReminders() {
        if (moodReminders == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MoodLevelDao targetDao = daoSession.getMoodLevelDao();
            List<MoodLevelBO> moodRemindersNew = targetDao._queryReminder_MoodReminders(id);
            synchronized (this) {
                if(moodReminders == null) {
                    moodReminders = moodRemindersNew;
                }
            }
        }
        return moodReminders;
    }

    public synchronized void resetMoodReminders() {
        moodReminders = null;
    }

    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

}

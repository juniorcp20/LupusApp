package com.umb.cs682.projectlupus.domain;

import java.util.List;

import com.umb.cs682.projectlupus.db.dao.MedicineDao;
import com.umb.cs682.projectlupus.db.dao.ReminderDao;
import com.umb.cs682.projectlupus.db.helpers.DaoSession;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoException;

public class MedicineBO {

    private Long id;
    private String medName;
    private int dosage;
    private String interval;
    private String notes;
    private Integer medReminderCount;
    private Integer medTakenCount;

    private transient DaoSession daoSession;

    private transient MedicineDao myDao;

    private List<ReminderBO> medReminders;

    public MedicineBO() {
    }

    public MedicineBO(Long id) {
        this.id = id;
    }

    public MedicineBO(Long id, String medName, int dosage, String interval, String notes, Integer medReminderCount, Integer medTakenCount) {
        this.id = id;
        this.medName = medName;
        this.dosage = dosage;
        this.interval = interval;
        this.notes = notes;
        this.medReminderCount = medReminderCount;
        this.medTakenCount = medTakenCount;
    }

    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getMedicineDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMedName() {
        return medName;
    }

    public void setMedName(String medName) {
        this.medName = medName;
    }

    public int getDosage() {
        return dosage;
    }

    public void setDosage(int dosage) {
        this.dosage = dosage;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getMedReminderCount() {
        return medReminderCount;
    }

    public void setMedReminderCount(Integer medReminderCount) {
        this.medReminderCount = medReminderCount;
    }

    public Integer getMedTakenCount() {
        return medTakenCount;
    }

    public void setMedTakenCount(Integer medTakenCount) {
        this.medTakenCount = medTakenCount;
    }

    public List<ReminderBO> getMedReminders() {
        if (medReminders == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ReminderDao targetDao = daoSession.getReminderDao();
            List<ReminderBO> medRemindersNew = targetDao._queryMedicine_MedReminders(id);
            synchronized (this) {
                if(medReminders == null) {
                    medReminders = medRemindersNew;
                }
            }
        }
        return medReminders;
    }

    public synchronized void resetMedReminders() {
        medReminders = null;
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
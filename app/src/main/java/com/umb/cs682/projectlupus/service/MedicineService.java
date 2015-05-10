package com.umb.cs682.projectlupus.service;

import android.content.Context;
import android.util.Log;

import com.umb.cs682.projectlupus.config.LupusMate;
import com.umb.cs682.projectlupus.db.dao.MedicineDao;
import com.umb.cs682.projectlupus.domain.MedicineBO;
import com.umb.cs682.projectlupus.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

import de.greenrobot.dao.DaoException;
import de.greenrobot.dao.query.CountQuery;
import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.Query;

public class MedicineService {
    private static final String TAG = "projectlupus.service";
    public static final String DAILY = Constants.DAILY;
    public static final String WEEKLY = Constants.WEEKLY;
    public static final String MONTHLY = Constants.MONTHLY;
    private Context context;
    private MedicineDao medicineDao;

    private MedicineBO bo;
    private Query getMedicinesQuery;

    public MedicineService(Context context, MedicineDao medicineDao){
        this.context = context;
        this.medicineDao = medicineDao;
        getMedicinesQuery = this.medicineDao.queryBuilder().build();
    }

    public void initMedicineDB(){
        if(medicineDao.count() == 0) {
            medicineDao.insert(new MedicineBO(null, "Prednisone", -1, DAILY, null,0,0));
            medicineDao.insert(new MedicineBO(null, "Mycophenolate", -1, DAILY, null,0,0));
            medicineDao.insert(new MedicineBO(null, "Methotrexate", -1, WEEKLY, null,0,0));
            medicineDao.insert(new MedicineBO(null, "Cyclophosphamide", -1, DAILY, null,0,0));
            medicineDao.insert(new MedicineBO(null, "Belimumab", -1, MONTHLY, null,0,0));
            medicineDao.insert(new MedicineBO(null, "Tacrolimus", -1, DAILY, null,0,0));
            medicineDao.insert(new MedicineBO(null, "Rituximab", -1, MONTHLY, null, 0, 0));
        }
    }

    public String addMedicine(String name, int dosage, String interval, String notes){
        String newMed = null;
        bo = new MedicineBO(null, name, dosage, interval, notes,0,0);
        long countBeforeAdd = getRowCount();
        if(getRowCount(name) == 0){
            medicineDao.insert(bo);
        }else{
            throw new DaoException("Medicine already exists, select from the available list!");
        }
        if(getRowCount() > countBeforeAdd){
            newMed = getMedicine(name).getMedName();
            Log.i(TAG, "Added new medicine: "+newMed);
        }
        return newMed;
    }

    public void editDosage(long id, int dosage, String interval){
        bo = getMedicine(id);
        bo.setDosage(dosage);
        bo.setInterval(interval);
        bo.update();
        Log.i(TAG, "Updated dosage: "+dosage+" interval: "+interval);
    }

    public void incrementTotalRemindedCount(long id){
        bo = getMedicine(id);
        int currentCount = bo.getMedReminderCount();
        int newCount = ++currentCount;
        bo.setMedReminderCount(newCount);
        bo.update();
        Log.i(TAG, "Update medicine reminded count: " + newCount);
    }

    public void incrementMedTakenCount(long id){
        bo = getMedicine(id);
        int currentCount = bo.getMedTakenCount();
        int newCount = ++currentCount;
        bo.setMedTakenCount(newCount);
        bo.update();
        Log.i(TAG, "Update medicine taken count: "+newCount);
    }

    public List<MedicineBO> getMedicines(){
        return getMedicinesQuery.list();
    }

    public ArrayList<String> getAllMedicineNames(){
        ArrayList<String> names = new ArrayList<>();
        for(MedicineBO currBO : getMedicines()){
            names.add(currBO.getMedName());
        }
        return names;
    }

    public MedicineBO getMedicine(long id){
        return medicineDao.queryBuilder().where(MedicineDao.Properties.Id.eq(id)).build().uniqueOrThrow();
    }

    public MedicineBO getMedicine(String name){
        return medicineDao.queryBuilder().where(MedicineDao.Properties.MedName.eq(name)).build().uniqueOrThrow();
    }

    public int getMedicineDosage(long id){
        bo = getMedicine(id);
        return bo.getDosage();
    }

    public String getMedicineInterval(long id){
        bo = getMedicine(id);
        return bo.getInterval();
    }

    private long getRowCount(){
        CountQuery query = medicineDao.queryBuilder().buildCount();
        return query.count();
    }

    private long getRowCount(String medName){
        CountQuery query = medicineDao.queryBuilder().where(MedicineDao.Properties.MedName.eq(medName)).buildCount();
        return query.count();
    }

    public List<MedicineBO> getAllData() {
        Query query = medicineDao.queryBuilder().build();
        return query.list();
    }

    public TreeMap<String,Float> getMednameVsTakenPercentage() {
        final LupusMate lupusMate = (LupusMate) context.getApplicationContext();
        ReminderService reminderService = lupusMate.getReminderService();
        TreeMap<String,Float> mednameVsTakenPercentageMap = new TreeMap<>();
        for(String currMed : reminderService.getMedicinesWithReminders()){
            MedicineBO currBO = getMedicine(currMed);
            mednameVsTakenPercentageMap.put(currBO.getMedName(),(currBO.getMedTakenCount().floatValue() / currBO.getMedReminderCount()) * 100);
        }
        /*for (MedicineBO currBO : getAllData()) {
            mednameVsTakenPercentageMap.put(currBO.getMedName(),currBO.getMedTakenCount().floatValue() / currBO.getMedReminderCount() * 100);
        }*/
        return mednameVsTakenPercentageMap;
    }

    /* Database Operations*/
    public void loadDummyData(){
        if(getCount() == 0) {
            Random random = new Random();
            for(MedicineBO bo : getAllData()){
                bo.setMedReminderCount(100);
                bo.setMedTakenCount(random.nextInt(51) + 50);
                medicineDao.update(bo);
            }
        }
    }

    private long getCount(){
        CountQuery query = medicineDao.queryBuilder().buildCount();
        return query.count();
    }
}

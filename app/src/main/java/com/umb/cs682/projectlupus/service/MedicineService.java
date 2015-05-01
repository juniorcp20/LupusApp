package com.umb.cs682.projectlupus.service;

import android.content.Context;

import com.umb.cs682.projectlupus.db.dao.MedicineDao;
import com.umb.cs682.projectlupus.domain.MedicineBO;
import com.umb.cs682.projectlupus.util.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.dao.DaoException;
import de.greenrobot.dao.query.CountQuery;
import de.greenrobot.dao.query.Query;

public class MedicineService {
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
            medicineDao.insert(new MedicineBO(null, "Prednisone/Prednisolone", -1, DAILY, null));
            medicineDao.insert(new MedicineBO(null, "Mycophenolate", -1, DAILY, null));
            medicineDao.insert(new MedicineBO(null, "Methotrexate", -1, WEEKLY, null));
            medicineDao.insert(new MedicineBO(null, "Cyclophosphamide", -1, DAILY, null));
            medicineDao.insert(new MedicineBO(null, "Belimumab", -1, MONTHLY, null));
            medicineDao.insert(new MedicineBO(null, "Tacrolimus", -1, DAILY, null));
            medicineDao.insert(new MedicineBO(null, "Rituximab", -1, MONTHLY, null));
        }
    }

    public String addMedicine(String name, int dosage, String interval, String notes){
        String newMed = null;
        bo = new MedicineBO(null, name, dosage, interval, notes);
        long countBeforeAdd = getRowCount();
        if(getRowCount(name) == 0){
            medicineDao.insert(bo);
        }else{
            throw new DaoException("Medicine already exists, select from the available list!");
        }
        if(getRowCount() > countBeforeAdd){
            newMed = getMedicine(name).getMedName();
        }
        return newMed;
    }

    public void editDosage(long id, int dosage, String interval){
        bo = getMedicine(id);
        bo.setDosage(dosage);
        bo.setInterval(interval);
        bo.update();
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

    public HashMap<String,Integer> getMednameVsDosage() {
        HashMap<String,Integer> mednameVsDosageMap = new HashMap<>();
        for (MedicineBO currBO : getAllData()) {
            mednameVsDosageMap.put(currBO.getMedName(),currBO.getDosage());
        }
        return mednameVsDosageMap;
    }

    /* Database Operations*/
    public void loadDummyData(){
        MedicineBO bo;
        if(getCount() == 0) {
            for (int i = 1; i < 5; i++) {
                bo = new MedicineBO(null,"Drug " + i,i,Constants.DAILY,null);
                medicineDao.insert(bo);
            }
        }
    }

    private long getCount(){
        CountQuery query = medicineDao.queryBuilder().buildCount();
        return query.count();
    }
}

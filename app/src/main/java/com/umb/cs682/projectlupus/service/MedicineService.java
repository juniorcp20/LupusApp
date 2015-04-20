package com.umb.cs682.projectlupus.service;

import android.content.Context;

import com.umb.cs682.projectlupus.db.dao.MedicineDao;
import com.umb.cs682.projectlupus.domain.MedicineBO;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

public class MedicineService {
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
        if(medicineDao.count()>0){
            medicineDao.deleteAll();
        }

        medicineDao.insert(new MedicineBO(null,"Prednisone/Prednisolone",-1,"mg/day",null));
        medicineDao.insert(new MedicineBO(null,"Mycophenolate",-1,"mg/day",null));
        medicineDao.insert(new MedicineBO(null,"Methotrexate",-1,"mg/week",null));
        medicineDao.insert(new MedicineBO(null,"Cyclophosphamide",-1,"mg/day",null));
        medicineDao.insert(new MedicineBO(null,"Belimumab",-1,"mg/month",null));
        medicineDao.insert(new MedicineBO(null,"Tacrolimus",-1,"mg/day",null));
        medicineDao.insert(new MedicineBO(null,"Rituximab",-1,"mg/month",null));
    }

    public void addMedicine(String name, int dosage, String units, String notes){
        bo = new MedicineBO(null, name, dosage, units, notes);
        medicineDao.insert(bo);
    }

    public void updateDosage(long id, int dosage){
        bo = getMedicine(id);
        bo.setDosage(dosage);
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

    public String getUnitOfMedicine(long id){
        bo = getMedicine(id);
        return bo.getUnits();
    }
}

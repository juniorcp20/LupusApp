package com.umb.cs682.projectlupus.service;

import android.content.Context;
import android.content.Intent;

import com.umb.cs682.projectlupus.db.dao.MedicineIntakeDao;

/**
 * Created by Nithya Kiran on 5/1/2015.
 */
public class MedicineIntakeService {

    private Context context;
    private MedicineIntakeDao medicineIntakeDao;

    public MedicineIntakeService(Context context, MedicineIntakeDao medicineIntakeDao){
        this.medicineIntakeDao = medicineIntakeDao;
        this.context = context;
    }
}

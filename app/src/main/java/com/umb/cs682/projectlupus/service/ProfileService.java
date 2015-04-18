package com.umb.cs682.projectlupus.service;

import android.content.Context;

import com.umb.cs682.projectlupus.db.dao.ProfileDao;
import com.umb.cs682.projectlupus.domain.ProfileBO;

import de.greenrobot.dao.query.CountQuery;

public class ProfileService {
    private static final String TAG = "service.profile";
    private boolean isSuccess = false;

    private Context context;
    private ProfileDao profileDao;

    private ProfileBO bo;

    public ProfileService(Context context, ProfileDao profileDao){
        this.profileDao = profileDao;
        this.context = context;
    }

    public boolean addProfileData(String userName, String age, String gender, String ethnicity){
        CountQuery query = profileDao.queryBuilder().buildCount();
        if(query.count() == 0){
            bo = new ProfileBO(null,userName, age, gender, ethnicity);
            profileDao.insert(bo);
            if(query.count() == 1){
                isSuccess = true;
            }
        }else{
            editProfileData(userName, age, gender, ethnicity);
            isSuccess = true;
        }
        return isSuccess;
    }

    public void editProfileData(String userName, String age, String gender, String ethnicity){
        CountQuery query = profileDao.queryBuilder().buildCount();
        if(query.count() == 1){
            bo = profileDao.queryBuilder().uniqueOrThrow();
            bo.setUserName(userName);
            bo.setAge(age);
            bo.setGender(gender);
            bo.setEthnicity(ethnicity);
            profileDao.update(bo);
        }
    }

    public void editDisplayName(String userName){
        CountQuery query = profileDao.queryBuilder().buildCount();
        if(query.count() == 1) {
            bo = profileDao.queryBuilder().uniqueOrThrow();
            bo.setUserName(userName);
            profileDao.update(bo);
        }
    }

    public ProfileBO getProfileData(){
        bo = profileDao.queryBuilder().uniqueOrThrow();
        return bo;
    }
}

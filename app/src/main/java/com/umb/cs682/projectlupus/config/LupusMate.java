package com.umb.cs682.projectlupus.config;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.umb.cs682.projectlupus.db.dao.ActivitySenseDao;
import com.umb.cs682.projectlupus.db.dao.MedicineDao;
import com.umb.cs682.projectlupus.db.dao.MoodLevelDao;
import com.umb.cs682.projectlupus.db.dao.ProfileDao;
import com.umb.cs682.projectlupus.db.dao.ReminderDao;
import com.umb.cs682.projectlupus.db.helpers.DaoMaster;
import com.umb.cs682.projectlupus.db.helpers.DaoSession;
import com.umb.cs682.projectlupus.service.ActivitySenseService;
import com.umb.cs682.projectlupus.service.HomeService;
import com.umb.cs682.projectlupus.service.MedicineService;
import com.umb.cs682.projectlupus.service.MoodLevelService;
import com.umb.cs682.projectlupus.service.ProfileService;
import com.umb.cs682.projectlupus.service.ReminderService;
import com.umb.cs682.projectlupus.util.SharedPreferenceManager;


public class LupusMate extends Application{
    //DB
    private static SQLiteDatabase db;
    private static DaoMaster daoMaster;
    private static DaoSession daoSession;

    //Service Objects
    private static ProfileService profileService;
    private static ActivitySenseService activitySenseService;
    private static HomeService homeService;
    private static MedicineService medicineService;
    private static MoodLevelService moodLevelService;
    private static ReminderService reminderService;

    //DAO Objects
    private static ActivitySenseDao activitySenseDao;
    private static MedicineDao medicineDao;
    private static MoodLevelDao moodLevelDao;
    private static ProfileDao profileDao;
    private static ReminderDao reminderDao;

    //Application Context
    private static Context appContext;

   @Override
    public void onCreate() {
        super.onCreate();
        setAppContext();
        try {
            configureServices();
        } catch (Exception e) {
            e.printStackTrace();
        }
       SharedPreferenceManager.setContext(this);
    }

    /*public static void setAppContext(Context context){
            appContext = context;
        }*/
    public void setAppContext(){
        appContext = getApplicationContext();
    }

  // public static void configureServices() throws Exception{
   public void configureServices() throws Exception{
        try {
            //Initialize DAOs
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(appContext, null);
            db = helper.getWritableDatabase();
            daoMaster = new DaoMaster(db);
            daoSession = daoMaster.newSession();
            activitySenseDao = daoSession.getActivitySenseDao();
            medicineDao = daoSession.getMedicineDao();
            moodLevelDao = daoSession.getMoodLevelDao();
            profileDao = daoSession.getProfileDao();
            reminderDao = daoSession.getReminderDao();

            //Initialize services
            profileService = new ProfileService(appContext, profileDao);
            moodLevelService = new MoodLevelService(appContext, moodLevelDao);
            activitySenseService = new ActivitySenseService(appContext, activitySenseDao);
            if(activitySenseService != null) {
                Log.d("Config", "Initialized Activity Sensing Service");
            }
            medicineService = new MedicineService(appContext, medicineDao);
            reminderService = new ReminderService(appContext, reminderDao);
        }catch (Exception e){
            Log.e("Config", e.getMessage());
        }
    }

    /*public Context getAppContext(){
        return appContext;
    }*/
    public ProfileService getProfileService(){
        return profileService;
    }
    public MoodLevelService getMoodLevelService(){ return moodLevelService;}
    public ActivitySenseService getActivitySenseService(){
        return activitySenseService;
    }
    public MedicineService getMedicineService(){return medicineService;}
    public ReminderService getReminderService(){return reminderService;}
    public static void clearTables(){
        profileDao.deleteAll();
        moodLevelDao.deleteAll();
        activitySenseDao.deleteAll();
        reminderDao.deleteAll();
    }
}

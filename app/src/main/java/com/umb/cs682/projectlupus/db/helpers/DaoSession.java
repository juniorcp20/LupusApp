package com.umb.cs682.projectlupus.db.helpers;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.umb.cs682.projectlupus.domain.ActivitySenseBO;
import com.umb.cs682.projectlupus.domain.ProfileBO;
import com.umb.cs682.projectlupus.domain.MoodLevelBO;
import com.umb.cs682.projectlupus.domain.ReminderBO;
import com.umb.cs682.projectlupus.domain.MedicineBO;

import com.umb.cs682.projectlupus.db.dao.ActivitySenseDao;
import com.umb.cs682.projectlupus.db.dao.MedicineDao;
import com.umb.cs682.projectlupus.db.dao.MoodLevelDao;
import com.umb.cs682.projectlupus.db.dao.ProfileDao;
import com.umb.cs682.projectlupus.db.dao.ReminderDao;


public class DaoSession extends AbstractDaoSession {

    private final DaoConfig activitySenseDaoConfig;
    private final DaoConfig profileDaoConfig;
    private final DaoConfig moodLevelDaoConfig;
    private final DaoConfig reminderDaoConfig;
    private final DaoConfig medicineDaoConfig;

    private final ActivitySenseDao activitySenseDao;
    private final ProfileDao profileDao;
    private final MoodLevelDao moodLevelDao;
    private final ReminderDao reminderDao;
    private final MedicineDao medicineDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        activitySenseDaoConfig = daoConfigMap.get(ActivitySenseDao.class).clone();
        activitySenseDaoConfig.initIdentityScope(type);

        profileDaoConfig = daoConfigMap.get(ProfileDao.class).clone();
        profileDaoConfig.initIdentityScope(type);

        moodLevelDaoConfig = daoConfigMap.get(MoodLevelDao.class).clone();
        moodLevelDaoConfig.initIdentityScope(type);

        reminderDaoConfig = daoConfigMap.get(ReminderDao.class).clone();
        reminderDaoConfig.initIdentityScope(type);

        medicineDaoConfig = daoConfigMap.get(MedicineDao.class).clone();
        medicineDaoConfig.initIdentityScope(type);

        activitySenseDao = new ActivitySenseDao(activitySenseDaoConfig, this);
        profileDao = new ProfileDao(profileDaoConfig, this);
        moodLevelDao = new MoodLevelDao(moodLevelDaoConfig, this);
        reminderDao = new ReminderDao(reminderDaoConfig, this);
        medicineDao = new MedicineDao(medicineDaoConfig, this);

        registerDao(ActivitySenseBO.class, activitySenseDao);
        registerDao(ProfileBO.class, profileDao);
        registerDao(MoodLevelBO.class, moodLevelDao);
        registerDao(ReminderBO.class, reminderDao);
        registerDao(MedicineBO.class, medicineDao);
    }
    
    public void clear() {
        activitySenseDaoConfig.getIdentityScope().clear();
        profileDaoConfig.getIdentityScope().clear();
        moodLevelDaoConfig.getIdentityScope().clear();
        reminderDaoConfig.getIdentityScope().clear();
        medicineDaoConfig.getIdentityScope().clear();
    }

    public ActivitySenseDao getActivitySenseDao() {
        return activitySenseDao;
    }

    public ProfileDao getProfileDao() {
        return profileDao;
    }

    public MoodLevelDao getMoodLevelDao() {
        return moodLevelDao;
    }

    public ReminderDao getReminderDao() {
        return reminderDao;
    }

    public MedicineDao getMedicineDao() {
        return medicineDao;
    }

}

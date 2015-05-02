package com.umb.cs682.projectlupus.alarms;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.umb.cs682.projectlupus.config.AppConfig;
import com.umb.cs682.projectlupus.domain.MedicineBO;
import com.umb.cs682.projectlupus.domain.ReminderBO;
import com.umb.cs682.projectlupus.service.MedicineService;
import com.umb.cs682.projectlupus.service.ReminderService;
import com.umb.cs682.projectlupus.util.AlarmUtil;
import com.umb.cs682.projectlupus.util.Constants;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import static com.umb.cs682.projectlupus.config.AppConfig.configureServices;
import static com.umb.cs682.projectlupus.config.AppConfig.setAppContext;

public class AlarmSetterService extends IntentService {
    private static final String TAG = "AlarmSetterService";
    public static final String CREATE = "CREATE";

    private IntentFilter matcher;
    private Context context;
    private ReminderService reminderService;
    private MedicineService medicineService;

    public AlarmSetterService() {
        super(TAG);
        matcher = new IntentFilter();
        matcher.addAction(CREATE);
        try {
            setAppContext(getApplicationContext());
            configureServices();
        }catch (Exception e){
            e.printStackTrace();
        }
        context = AppConfig.getAppContext();
        reminderService = AppConfig.getReminderService();
        medicineService = AppConfig.getMedicineService();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (matcher.matchAction(action)) {
               setAllMoodAlarms();
               setAllMedAlarms();
            }
        }
    }

    private void setAllMoodAlarms() {
        List<ReminderBO> moodAlarms = reminderService.getMoodReminders();
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        int id = 0;
        for(ReminderBO bo : moodAlarms){
            id = bo.getId().intValue();
            cal.setTimeInMillis(bo.getReminderTime().getTime());
            AlarmUtil.setDailyRepeatingAlarm(context, Constants.MOOD_REMINDER, id, cal);
        }
        Log.i(TAG, "Mood Alarms Set");
    }

    private void setAllMedAlarms(){
        List<ReminderBO> medAlarms = reminderService.getMedReminders();
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        int remID = 0;
        int medID = 0;
        String alarmInterval;
        int hourOfDay = 0;
        int min = 0;
        String dayOfWeek = null;
        String dayOfMonth = null;
        MedicineBO medicineBO;
        for(ReminderBO reminderBO : medAlarms){
            remID = reminderBO.getId().intValue();
            medID = reminderService.getMedReminder(remID).getId().intValue();
            medicineBO = medicineService.getMedicine(medID);
            alarmInterval = medicineBO.getInterval();
            cal.setTimeInMillis(reminderBO.getReminderTime().getTime());
            hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
            min = cal.get(Calendar.MINUTE);
            switch(alarmInterval){
                case Constants.DAILY:
                    AlarmUtil.setDailyRepeatingAlarm(context, Constants.MED_REMINDER, remID, cal);
                    break;
                case Constants.WEEKLY:
                    dayOfWeek = reminderBO.getReminderDayDate();
                    break;
                case Constants.MONTHLY:
                    dayOfMonth = reminderBO.getReminderDayDate();
            }
            AlarmUtil.setAlarm(context, remID, alarmInterval, hourOfDay, min, dayOfWeek, dayOfMonth, Constants.MED_REMINDER);
        }
        Log.i(TAG, "Medicine Alarms Set");
    }
}

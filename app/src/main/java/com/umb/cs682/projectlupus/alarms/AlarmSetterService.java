package com.umb.cs682.projectlupus.alarms;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.umb.cs682.projectlupus.config.LupusMate;
import com.umb.cs682.projectlupus.domain.MedicineBO;
import com.umb.cs682.projectlupus.domain.ReminderBO;
import com.umb.cs682.projectlupus.service.MedicineService;
import com.umb.cs682.projectlupus.service.ReminderService;
import com.umb.cs682.projectlupus.util.AlarmUtil;
import com.umb.cs682.projectlupus.util.Constants;
import com.umb.cs682.projectlupus.util.DateTimeUtil;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/*import static com.umb.cs682.projectlupus.config.LupusMate.configureServices;
import static com.umb.cs682.projectlupus.config.LupusMate.setAppContext;*/

public class AlarmSetterService extends IntentService {
    private static final String TAG = "AlarmSetterService";
    public static final String CREATE = "CREATE";

    private IntentFilter matcher;
    //private Context context;

        private ReminderService reminderService;
    private MedicineService medicineService;

    public AlarmSetterService() {
        super(TAG);
        matcher = new IntentFilter();
        matcher.addAction(CREATE);
       /*try {
            setAppContext(getApplicationContext());
            configureServices();
        }catch (Exception e){
            e.printStackTrace();
        }
        context = LupusMate.getAppContext();*/
        final LupusMate lupusMate = (LupusMate) getApplicationContext();
        reminderService = lupusMate.getReminderService();
        medicineService = lupusMate.getMedicineService();
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
        List<ReminderBO> moodAlarms = reminderService.getMoodRemindersForCurrentThread();
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        int remID = 0;
        int requestCode = 0;
        for(ReminderBO bo : moodAlarms){
            remID = bo.getId().intValue();
            requestCode = remID;
            cal.setTimeInMillis(bo.getReminderTime().getTime());
            //AlarmUtil.setDailyRepeatingAlarm(context, Constants.MOOD_REMINDER, remID, requestCode, cal);
            AlarmUtil.setAlarm(getApplicationContext(), requestCode, remID, Constants.MOOD_REMINDER, Constants.DAILY, cal);
        }
        Log.i(TAG, "Mood Alarms Set");
    }

    private void setAllMedAlarms(){
        List<ReminderBO> medAlarms = reminderService.getMedRemindersForCurrentThread();
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        int requestCode;
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
            requestCode = remID;
            medID = reminderService.getMedReminder(remID).getId().intValue();
            medicineBO = medicineService.getMedicine(medID);
            alarmInterval = medicineBO.getInterval();
            cal.setTimeInMillis(reminderBO.getReminderTime().getTime());
            hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
            min = cal.get(Calendar.MINUTE);
            switch(alarmInterval){
                case Constants.DAILY:
                    break;
                case Constants.WEEKLY:
                    dayOfWeek = reminderBO.getReminderDayOrDate();
                    break;
                case Constants.MONTHLY:
                    dayOfMonth = reminderBO.getReminderDayOrDate();
            }
            AlarmUtil.setAlarm(getApplicationContext(), requestCode, remID, Constants.MED_REMINDER, alarmInterval, DateTimeUtil.getCalendar(hourOfDay, min, dayOfWeek, dayOfMonth));
        }
        Log.i(TAG, "Medicine Alarms Set");
    }
}

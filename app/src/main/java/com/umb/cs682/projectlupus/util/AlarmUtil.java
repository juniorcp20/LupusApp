package com.umb.cs682.projectlupus.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.umb.cs682.projectlupus.activities.PopWindow;
import com.umb.cs682.projectlupus.activities.medicineAlert.AddMedicine;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Nithya Kiran on 4/27/2015.
 */
public class AlarmUtil {
    private static Context context;
    private static Calendar cal = Calendar.getInstance(TimeZone.getDefault());
    private static PendingIntent pendingIntent;
    private static long alarmTimeMillis;

    public static void setAlarm(Context cxt, int id, int hourOfDay, int min, String dayOfWeek, String dayOfMonth, int reminderType){
        context = cxt;
        if(hourOfDay >= 0){
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        }
        if(min >= 0 ){
            cal.set(Calendar.MINUTE, min);
        }
        if(dayOfWeek != null){
            cal.set(Calendar.DAY_OF_WEEK, getDayOfWeek(dayOfWeek));
            alarmTimeMillis = cal.getTimeInMillis();
            if(alarmTimeMillis - System.currentTimeMillis() < 0){
                alarmTimeMillis =alarmTimeMillis + AlarmManager.INTERVAL_DAY*7;
            }
        }
        if(dayOfMonth != null){
            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dayOfMonth));
            alarmTimeMillis = cal.getTimeInMillis();
            if(alarmTimeMillis - System.currentTimeMillis() < 0){
                if(cal.get(Calendar.MONTH) == Calendar.DECEMBER){
                    cal.set(Calendar.MONTH, Calendar.JANUARY);
                }else{
                    cal.set(Calendar.MONTH, cal.get(Calendar.MONTH)+1);
                }
            }
        }
        set(cal, getPendingIntent(reminderType, id));
    }

    private static PendingIntent getPendingIntent(int reminderType, int requestCode) {
        Intent intent;
        PendingIntent pi = null;
        if(reminderType == Constants.MED_REMINDER){
            intent = new Intent(context , PopWindow.class);//todo replace with actual pop-up
            pi = PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }else if(reminderType == Constants.MOOD_REMINDER){
            intent = new Intent(context , PopWindow.class);//todo replace with actual pop-up
            pi = PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        return pi;
    }

    private static void set(Calendar cal, PendingIntent pendingIntent) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long alarmTime = cal.getTimeInMillis();
        long currentTime = System.currentTimeMillis();
        //if the set time is in the past
        if(alarmTime - currentTime < 0){
            alarmTime = alarmTime + AlarmManager.INTERVAL_DAY*7;
        }
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime, AlarmManager.INTERVAL_DAY*7,pendingIntent);
    }

    private static int getDayOfWeek(String dayOfWeek) {
        int retVal = 0;
        switch(dayOfWeek.toLowerCase()){
            case "sunday":
                retVal = Calendar.SUNDAY;
                break;
            case "monday":
                retVal = Calendar.MONDAY;
                break;
            case "tuesday":
                retVal = Calendar.TUESDAY;
                break;
            case "wednesday":
                retVal = Calendar.WEDNESDAY;
                break;
            case "thursday":
                retVal = Calendar.THURSDAY;
                break;
            case "friday":
                retVal = Calendar.FRIDAY;
                break;
            case "saturday":
                retVal = Calendar.SATURDAY;
                break;

        }
        return retVal;
    }
}

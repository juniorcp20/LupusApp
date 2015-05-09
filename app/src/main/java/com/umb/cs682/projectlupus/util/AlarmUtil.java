package com.umb.cs682.projectlupus.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.umb.cs682.projectlupus.alarms.MedicineAlarmReceiver;
import com.umb.cs682.projectlupus.alarms.MoodAlarmReceiver;

import java.util.Calendar;


public class AlarmUtil {
    private static final String TAG = "util.AlarmUtil";
    private static AlarmManager alarmManager;

    public static void setAlarm(Context context, int requestCode, int reminderID, int reminderType, String alarmInterval, Calendar cal){

        long startTime = getAlarmTimeMillis(alarmInterval, cal);

        if(alarmInterval.equals(Constants.DAILY)) {
            setDailyRepeatingAlarm(context, reminderType, reminderID, requestCode, cal);
        }else{
            setOneShotAlarm(context, reminderType, reminderID, requestCode, alarmInterval, startTime);
        }
        Log.i(TAG, "Alarm set, reminder ID = "+reminderID);
    }

    public static void cancelAlarm(Context context, int requestCode, int reminderID, int reminderType, String alarmInterval, Calendar cal){
        if(alarmInterval.equals(Constants.DAILY)) {
            cancelDailyRepeatingAlarm(context, reminderType, reminderID, requestCode);
        }else{
            long startTime = getAlarmTimeMillis(alarmInterval, cal);
            cancelOneShotAlarm(context, reminderType, reminderID, requestCode, alarmInterval, startTime);
        }
        Log.i(TAG, "Alarm cancelled, reminder ID = "+reminderID);
    }

    public static void snooze(Context context, int requestCode, int reminderID, int reminderType, String alarmInterval, Calendar cal, long snoozeTime){
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long startTime;
        if(alarmInterval.equals(Constants.DAILY)) {
            startTime = 0;
        }else{
            startTime = getAlarmTimeMillis(alarmInterval, cal);
        }
        Intent intent = buildIntent(context, reminderType, reminderID, requestCode, alarmInterval, startTime);
        intent.putExtra(Constants.SNOOZED, true);
        PendingIntent pendingIntent =  PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, snoozeTime, pendingIntent);
    }

    public static void cancelSnooze(Context context, int requestCode, int reminderID, int reminderType, String alarmInterval, Calendar cal){
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long startTime;
        if(alarmInterval.equals(Constants.DAILY)) {
            startTime = 0;
        }else{
            startTime = getAlarmTimeMillis(alarmInterval, cal);
        }
        Intent intent = buildIntent(context, reminderType, reminderID, requestCode, alarmInterval, startTime);
        intent.putExtra(Constants.SNOOZED, true);
        PendingIntent pendingIntent =  PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        pendingIntent.cancel();
        alarmManager.cancel(pendingIntent);
    }

    private static void setOneShotAlarm(Context context, int reminderType, int reminderID, int requestCode, String alarmInterval, long startTime){
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = buildIntent(context, reminderType, reminderID, requestCode, alarmInterval, startTime);
        PendingIntent pendingIntent =  PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, startTime, pendingIntent);
    }

    private static void cancelOneShotAlarm(Context context, int reminderType, int reminderID, int requestCode, String alarmInterval, long startTime) {
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = buildIntent(context, reminderType, reminderID, requestCode, alarmInterval, startTime);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        pendingIntent.cancel();
        alarmManager.cancel(pendingIntent);
    }

    private static void setDailyRepeatingAlarm(Context context, int reminderType, int reminderID, int requestCode, Calendar cal) {
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = buildIntent(context, reminderType, reminderID, requestCode, Constants.DAILY, 0);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        Log.i(TAG, "Set Repeating Alarm - Successful, reminder ID = "+reminderID);
    }

    private static void cancelDailyRepeatingAlarm(Context context, int reminderType, int reminderID, int requestCode){
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = buildIntent(context, reminderType, reminderID, requestCode, Constants.DAILY, 0);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        pendingIntent.cancel();
        alarmManager.cancel(pendingIntent);
        Log.i(TAG, "Repeating Alarm - Cancelled, reminder ID = "+reminderID);
    }

    private static Intent buildIntent(Context context, int reminderType, int reminderID, int requestCode, String alarmInterval, long startTime){//int hourOfDay, int min, int dayOfWeek, int dayOfMonth){
        Intent intent = new Intent();

        if(reminderType == Constants.MOOD_REMINDER){
            intent.setClass(context, MoodAlarmReceiver.class);
        }else{
            intent.setClass(context, MedicineAlarmReceiver.class);
        }

        intent.putExtra(Constants.REQUEST_CODE, requestCode);
        intent.putExtra(Constants.REMINDER_ID, reminderID);
        intent.putExtra(Constants.ALARM_INTERVAL, alarmInterval);
        if(!alarmInterval.equals(Constants.DAILY)) {
            intent.putExtra(Constants.START_TIME, startTime);
        }
        return intent;
    }

    private static long getAlarmTimeMillis(String alarmInterval, Calendar cal){//int hourOfDay, int min, String dayOfWeek, String dayOfMonth){
        long alarmTimeMillis = 0;
        if(alarmInterval.equals(Constants.WEEKLY)){
            alarmTimeMillis = cal.getTimeInMillis();
            if(alarmTimeMillis - System.currentTimeMillis() < 0){
                alarmTimeMillis = alarmTimeMillis + AlarmManager.INTERVAL_DAY*7;
            }
        }
        if(alarmInterval.equals(Constants.MONTHLY)){
            alarmTimeMillis = cal.getTimeInMillis();
            if(alarmTimeMillis - System.currentTimeMillis() < 0){
                if(cal.get(Calendar.MONTH) == Calendar.DECEMBER){
                    cal.set(Calendar.MONTH, Calendar.JANUARY);
                    cal.set(Calendar.YEAR, cal.get(Calendar.YEAR)+1);
                }else{
                    cal.set(Calendar.MONTH, cal.get(Calendar.MONTH)+1);
                }
                alarmTimeMillis = cal.getTimeInMillis();
            }
        }
        return alarmTimeMillis;
    }
}

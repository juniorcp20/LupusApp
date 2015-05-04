package com.umb.cs682.projectlupus.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.umb.cs682.projectlupus.alarms.MedicineAlarmReceiver;
import com.umb.cs682.projectlupus.alarms.MoodAlarmReceiver;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Nithya Kiran on 4/27/2015.
 */
public class AlarmUtil {
    private static final String TAG = "util.AlarmUtil";
    private static AlarmManager alarmManager;
    //private static Calendar cal = Calendar.getInstance(TimeZone.getDefault());
    //private static String alarmInterval = Constants.DAILY;

    //public static void setAlarm(Context context, int requestCode, String alarmInterval, int hourOfDay, int min, String dayOfWeek, String dayOfMonth, int reminderType, int reminderID){ //reminderType -> Mood or medicine
    public static void setAlarm(Context context, int requestCode, int reminderID, int reminderType, String alarmInterval, Calendar cal){ //reminderType -> Mood or medicine

        long startTime = getAlarmTimeMillis(alarmInterval, cal);//hourOfDay, min, dayOfWeek, dayOfMonth);

        if(alarmInterval.equals(Constants.DAILY)) {
            setDailyRepeatingAlarm(context, reminderType, reminderID, requestCode, cal);
        }else{
            setOneShotAlarm(context, reminderType, reminderID, requestCode, alarmInterval, startTime);
        }
        Log.i(TAG, "Alarm set");
    }

    //public static void cancelAlarm(Context context, int requestCode, String alarmInterval, int hourOfDay, int min, String dayOfWeek, String dayOfMonth, int reminderType, int reminderID){
    public static void cancelAlarm(Context context, int requestCode, int reminderID, int reminderType, String alarmInterval, Calendar cal){

        //long startTime = getAlarmTimeMillis(alarmInterval, cal);//hourOfDay, min, dayOfWeek, dayOfMonth);
        if(alarmInterval.equals(Constants.DAILY)) {
            cancelDailyRepeatingAlarm(context, reminderType, reminderID, requestCode);
        }else{
            long startTime = getAlarmTimeMillis(alarmInterval, cal);
            cancelOneShotAlarm(context, reminderType, reminderID, requestCode, alarmInterval, startTime);
        }
        Log.i(TAG, "Alarm cancelled");
        /*alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = buildIntent(context, reminderType, reminderID, requestCode, alarmInterval, startTime);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        pendingIntent.cancel();
        alarmManager.cancel(pendingIntent);*/
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
        /*Intent intent = new Intent();
        intent.putExtra(Constants.REMINDER_ID, reminderID);
        if(reminderType == Constants.MOOD_REMINDER){
            intent.setClass(context, MoodAlarmReceiver.class);
        }else{
            intent.setClass(context, MedicineAlarmReceiver.class);
            intent.putExtra(Constants.ALARM_INTERVAL, Constants.DAILY);
        }
        intent.putExtra(Constants.REQUEST_CODE, requestCode);*/
        Intent intent = buildIntent(context, reminderType, reminderID, requestCode, Constants.DAILY, 0);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        Log.i(TAG, "Set Repeating Alarm - Successful");
    }

    private static void cancelDailyRepeatingAlarm(Context context, int reminderType, int reminderID, int requestCode){
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        /*Intent intent = new Intent();
        intent.putExtra(Constants.REMINDER_ID, reminderID);
        if(reminderType == Constants.MOOD_REMINDER){
            intent.setClass(context, MoodAlarmReceiver.class);
        }else{
            intent.setClass(context, MedicineAlarmReceiver.class);
        }*/
        Intent intent = buildIntent(context, reminderType, reminderID, requestCode, Constants.DAILY, 0);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        pendingIntent.cancel();
        alarmManager.cancel(pendingIntent);
        Log.i(TAG, "Repeating Alarm - Cancelled");
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
        if(!alarmInterval.equals(Constants.DAILY)) {
            intent.putExtra(Constants.ALARM_INTERVAL, alarmInterval);
            intent.putExtra(Constants.START_TIME, startTime);
        }
        return intent;
    }

    private static long getAlarmTimeMillis(String alarmInterval, Calendar cal){//int hourOfDay, int min, String dayOfWeek, String dayOfMonth){
        long alarmTimeMillis = 0;
        /*if(hourOfDay >= 0){
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        }
        if(min >= 0 ){
            cal.set(Calendar.MINUTE, min);
            alarmTimeMillis = cal.getTimeInMillis();
        }*/
        if(alarmInterval.equals(Constants.WEEKLY)){
            //alarmType = Constants.WEEKLY;
            //cal.set(Calendar.DAY_OF_WEEK, getDayOfWeek(dayOfWeek));
            alarmTimeMillis = cal.getTimeInMillis();
            if(alarmTimeMillis - System.currentTimeMillis() < 0){
                alarmTimeMillis = alarmTimeMillis + AlarmManager.INTERVAL_DAY*7;
            }
        }
        if(alarmInterval.equals(Constants.MONTHLY)){
            //alarmType = Constants.MONTHLY;
            //cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dayOfMonth));
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

    /*private static int getDayOfWeek(String dayOfWeek) {
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
    }*/

}

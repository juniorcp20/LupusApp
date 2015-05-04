package com.umb.cs682.projectlupus.util;

import android.text.format.DateFormat;

import com.umb.cs682.projectlupus.config.LupusMate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeUtil {
    private static final String DATETIME_FORMAT = "YYYY-MM-DD HH:mm:ss";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME_FORMAT_24 = "HH:mm";
    private static final String TIME_FORMAT_12 = "hh:mm a";

    public static boolean is24hrFormat = is24HourFormat();


    private static boolean is24HourFormat(){
        if(DateFormat.is24HourFormat(LupusMate.getAppContext())){
            is24hrFormat = true;
        }else{
            is24hrFormat = false;
        }
        return is24hrFormat;
    }

    public static Date toDate(Date date){
        return getFormattedDate(DATE_FORMAT, date);
    }
    public static Date toDate(String date){
        Date formattedDate = getFormattedDate(DATE_FORMAT, date);
        return formattedDate;
    }

    public static Date toDateTime(Date date){
        return getFormattedDate(DATETIME_FORMAT, date);
    }

    public static Date toTime(String time){
        Date formattedDate = null;
        if(is24hrFormat){
            formattedDate = getFormattedDate(TIME_FORMAT_24, time);
        }else{
            formattedDate = getFormattedDate(TIME_FORMAT_12, time);
        }
        return formattedDate;
    }

    public static String toTimeString(Date date){
        if(is24hrFormat){
            return getFormattedDateString(TIME_FORMAT_24, date);
        }else{
            return getFormattedDateString(TIME_FORMAT_12, date);
        }
    }

    public static Calendar actSenseDBSchedulerTime(){
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 23); // For 11 PM
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }

    private static Date getFormattedDate(String format, Date date){
        SimpleDateFormat df = new SimpleDateFormat(format);
        Date formattedDate = null;

        try {
            formattedDate = df.parse(df.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return formattedDate;
    }

    private static Date getFormattedDate(String format, String date){
        SimpleDateFormat df = new SimpleDateFormat(format);
        Date formattedDate = null;

        try {
            formattedDate = df.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return formattedDate;
    }

    private static String getFormattedDateString(String format, Date date){
        SimpleDateFormat df = new SimpleDateFormat(format);
        String formattedString = df.format(date);
        return formattedString;
    }

    public static int getHour(Date reminderTime) {
        return Integer.parseInt(getFormattedDateString("HH", reminderTime));
    }

    public static int getMin(Date reminderTime) {
        return Integer.parseInt(getFormattedDateString("mm", reminderTime));
    }

    public static Calendar getCalendar(int hourOfDay, int min, String dayOfWeek, String dayOfMonth){
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, min);
        if(dayOfWeek != null) {
            cal.set(Calendar.DAY_OF_WEEK, getDayOfWeek(dayOfWeek));
        }
        if(dayOfMonth != null) {
            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dayOfMonth));
        }
        return cal;
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

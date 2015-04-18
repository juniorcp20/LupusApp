package com.umb.cs682.projectlupus.util;

import android.text.format.DateFormat;

import com.umb.cs682.projectlupus.config.AppConfig;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    private static final String DATETIME_FORMAT = "YYYY-MM-DD HH:mm:ss";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME_FORMAT_24 = "HH:mm";
    private static final String TIME_FORMAT_12 = "hh:mm a";

    private static boolean is24hrFormat = is24HourFormat();


    public static boolean is24HourFormat(){
        if(DateFormat.is24HourFormat(AppConfig.getAppContext())){
            is24hrFormat = true;
        }else{
            is24hrFormat = false;
        }
        return is24hrFormat;
    }

    public static Date toDate(Date date){
        return getFormattedDate(DATE_FORMAT, date);
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
}

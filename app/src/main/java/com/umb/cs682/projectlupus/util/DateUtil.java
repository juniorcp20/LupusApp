package com.umb.cs682.projectlupus.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    private static final String DATETIME_FORMAT = "YYYY-MM-DD HH:mm:ss";
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private static Date formattedDate = null;
    public static Date toDate(Date date){
        SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);

        try {
            formattedDate = df.parse(df.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return formattedDate;
    }

    public static Date toDateTime(Date date){
        SimpleDateFormat df = new SimpleDateFormat(DATETIME_FORMAT);

        try {
            formattedDate = df.parse(df.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return formattedDate;
    }

    public static Calendar actSenseDBSchedulerTime(){
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 23); // For 11 PM
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }
}

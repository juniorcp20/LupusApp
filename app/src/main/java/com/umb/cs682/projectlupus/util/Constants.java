package com.umb.cs682.projectlupus.util;

/**
 * Created by Nithya Konda on 2/25/2015.
 */
public final class Constants {
    private Constants(){}
    //Keys
    public static final String PARENT_ACTIVITY_NAME = "ParentActivityName";
    public static final String IS_INIT = "Is the process an Initialization";

    //Values
    public static final String WELCOME = "Welcome";
    public static final String PROFILE = "Profile";
    public static final String MOOD_ALERT = "Mood_Alert";
    public static final String ACTIVITY_SENSE = "Activity_Sense";
    public static final String MEDICINE_ALERT = "Medicine_Alert";

    //Shared Prefs Keys
    public static final String ACTIVITY_SENSE_SETTING = "Activity_Sense_Setting";
    public static final String IS_FIRST_RUN = "Is_First_Run";
    public static final String SENSITIVITY_VALUE = "Sensitivity_Value";

    //Reminder Types
    public static final String REMINDER_TYPE = "Reminder Type";
    public static final int MED_REMINDER = 1;
    public static final int MOOD_REMINDER = 2;

    //Reminder Status
    public static final String REM_STATUS_CREATED = "Created"; //reminder data is saved but the alarm is not yet saved
    public static final String REM_STATUS_ACTIVE = "Active";   //alarm is on and will fire in future
    public static final String REM_STATUS_PENDING = "Pending"; // alarm has fired and waiting for user input
    public static final String REM_STATUS_DONE = "Done";       // user selected done on notification
    public static final String REM_STATUS_SKIP = "Skip";       // user selected skip on notification
    public static final String REM_STATUS_SNOOZE= "Snooze";    // user selected snooze on notification

    //Medicine Reminder Intervals
    public static final String DAILY = "Daily";
    public static final String WEEKLY = "Weekly";
    public static final String MONTHLY = "Monthly";

    //Alarm Related
    public static final String ALARM_INTERVAL = "Alarm Interval";
    public static final String DAY_OF_WEEK = "Day of Week";
    public static final String DAY_OF_MONTH = "Day of Month";
    public static final String REQUEST_CODE = "Request Code";
    public static final String HOUR_OF_DAY = "Hour of Day";
    public static final String MINUTES = "minutes";
    public static final String START_TIME = "Start Time";

    //Database
    public static final String STR_DEFAULT = "None";

    //Add Medicine
    public static final String IS_NEW_MED = "Is New Medicine";
    public static final String MED_NAME = "Medicine Name";
}
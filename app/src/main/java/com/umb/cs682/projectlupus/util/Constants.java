package com.umb.cs682.projectlupus.util;

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
    public static final String REMINDER_ID = "Reminder Id";
    public static final String REMINDER_TYPE = "Reminder Type";
    public static final int MED_REMINDER = 1;
    public static final int MOOD_REMINDER = 2;

    //Reminder Status
    public static final String REM_STATUS_CREATED = "Created";
    public static final String REM_STATUS_ACTIVE = "Active";
    public static final String REM_STATUS_PENDING = "Pending";
    public static final String REM_STATUS_DONE = "Done";
    public static final String REM_STATUS_SKIP = "Skip";
    public static final String REM_STATUS_SNOOZE= "Snooze";

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
    public static final String SNOOZED = "Is Snoozed";

    //Snooze Intervals
    public static final String MIN_5 = "5 minutes";
    public static final String MIN_15 = "15 minutes";
    public static final String MIN_30 = "30 minutes";
    public static final String MIN_60 = "1 hour";

    //Database
    public static final String STR_DEFAULT = "None";

    //Add Medicine
    public static final String IS_NEW_MED = "Is New Medicine";
    public static final String MED_NAME = "Medicine Name";
}
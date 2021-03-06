package com.umb.cs682.projectlupus.activities.common;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.Handler;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.umb.cs682.projectlupus.R;
import com.umb.cs682.projectlupus.config.LupusMate;
import com.umb.cs682.projectlupus.domain.ReminderBO;
import com.umb.cs682.projectlupus.service.ActivitySenseService;
import com.umb.cs682.projectlupus.service.MQTTService;
import com.umb.cs682.projectlupus.service.MedicineService;
import com.umb.cs682.projectlupus.service.MoodLevelService;
import com.umb.cs682.projectlupus.service.ProfileService;
import com.umb.cs682.projectlupus.service.ReminderService;

import de.greenrobot.dao.DaoException;

public class Settings extends PreferenceActivity {

    private static final boolean ALWAYS_SIMPLE_PREFS = false;
    private IntentFilter intentFilter = null;
    private PushReceiver pushReceiver;
    private Messenger service = null;
    private final Messenger serviceHandler = new Messenger(new ServiceHandler());
    private ProfileService profileService;
    private ActivitySenseService activitySenseService;
    private ReminderService reminderService;
    private MoodLevelService moodLevelService;
    private MedicineService medicineService;
    String username;
    int stepCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final LupusMate lupusMate = (LupusMate) getApplicationContext();
        profileService = lupusMate.getProfileService();
        activitySenseService = lupusMate.getActivitySenseService();
        moodLevelService = lupusMate.getMoodLevelService();
        medicineService = lupusMate.getMedicineService();
        reminderService = lupusMate.getReminderService();
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.umb.cs682.projectlupus.activities.common.PushReceived");
        pushReceiver = new PushReceiver();
        registerReceiver(pushReceiver, intentFilter, null, null);


        startService(new Intent(this, MQTTService.class));

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupSimplePreferencesScreen();
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, MQTTService.class), serviceConnection, 0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(serviceConnection);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(pushReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(pushReceiver);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            service = new Messenger(iBinder);
            Bundle data = new Bundle();
            data.putCharSequence(MQTTService.INTENTNAME, "com.umb.cs682.projectlupus.activities.common.PushReceived");
            Message msg = Message.obtain(null, MQTTService.REGISTER);
            msg.setData(data);
            msg.replyTo = serviceHandler;
            try {
                service.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    private void setupSimplePreferencesScreen() {
        if (!isSimplePreferences(this)) {
            return;
        }

        addPreferencesFromResource(R.xml.pref_general);

        PreferenceCategory fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_notifications);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_notification);

        fakeHeader = new PreferenceCategory(this);
        getPreferenceScreen().addPreference(fakeHeader);

        Button syncButton = new Button(this);
        syncButton.setText("Sync with the Cloud");
        setListFooter(syncButton);
        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String topic = getUserNameToTopic();
                Bundle data = new Bundle();
                // Topic with the name of the patient
                data.putCharSequence(MQTTService.TOPIC, topic);
                // ActivitySense IDs
                data.putIntegerArrayList(MQTTService.ACTIVITYSENSEID, getActivitySenseId());
                // StepCount
                data.putIntegerArrayList(MQTTService.STEPCOUNT, getStepCount());
                // StepCount Dates
                data.putSerializable(MQTTService.STEPCOUNTDATES, getStepCountDates());
                // Medicine IDs
                data.putIntegerArrayList(MQTTService.MEDICINEID, getMedicineId());
                // Medicine Name
                data.putStringArrayList(MQTTService.MEDICINE, medicineService.getAllMedicineNames());
                // Medicine Dosage
                data.putIntegerArrayList(MQTTService.MEDICINEDOSAGE, getMedicinesDosage());
                // Medicine Interval
                data.putStringArrayList(MQTTService.MEDICINEINTERVAL, getMedicineInterval());
                // Medicine Reminder Count
                data.putIntegerArrayList(MQTTService.MEDICINEREMINDERCOUNT, getMedicineReminderCount());
                // Mood level IDs
                data.putIntegerArrayList(MQTTService.MOODLEVELID, getMoodLevelID());
                // Mood level
                data.putIntegerArrayList(MQTTService.MOODLEVEL, getMoodLevel());
                // Mood level Dates
                data.putSerializable(MQTTService.MOODLEVELDATE, getMoodLevelDate());
                // Mood level Reminder Id
                data.putIntegerArrayList(MQTTService.MOODLEVELREMINDERID, getMoodLevelReminderID());
                // Reminder ID
                data.putIntegerArrayList(MQTTService.REMINDERID, getReminderID());
                // Reminder Type ID
                data.putIntegerArrayList(MQTTService.REMINDERTYPEID, getReminderTypeID());
                // Reminder Medicine ID
                data.putIntegerArrayList(MQTTService.REMINDERMEDID, getReminderMedID());
                // Reminder Day Date
                data.putStringArrayList(MQTTService.REMINDERDAYDATE, getReminderDayDate());
                // Reminder Time
                data.putSerializable(MQTTService.REMINDERTIME, getReminderTime());
                // Reminder Status
                data.putStringArrayList(MQTTService.REMINDERSTATUS, getReminderStatus());
                Message msg = Message.obtain(null, MQTTService.PUBLISH);
                msg.setData(data);
                try {
                    service.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
    }

    private ArrayList<String> getReminderStatus() {
        ArrayList<String> reminderStatus = new ArrayList<>();
        for (ReminderBO currRem : reminderService.getMedReminders())
            reminderStatus.add(currRem.getStatus());
        return reminderStatus;
    }

    private ArrayList<Serializable> getReminderTime() {
        ArrayList<Serializable> reminderTime = new ArrayList<>();
        for (ReminderBO currRem : reminderService.getMedReminders())
            reminderTime.add(currRem.getReminderTime());
        return reminderTime;
    }

    private ArrayList<String> getReminderDayDate() {
        ArrayList<String> reminderDayDate = new ArrayList<>();
        for (ReminderBO currRem : reminderService.getMedReminders())
            reminderDayDate.add((currRem.getReminderDayOrDate()));
        return reminderDayDate;
    }

    private ArrayList<Integer> getReminderTypeID() {
        ArrayList<Integer> reminderID = new ArrayList<>();
        for (ReminderBO currRem : reminderService.getMoodReminders())
            reminderID.add((int)(long) currRem.getTypeId());
        for (ReminderBO currRem : reminderService.getMedReminders())
            reminderID.add((int)(long) currRem.getTypeId());
        return reminderID;
    }



    private ArrayList<Integer> getReminderMedID() {
        ArrayList<Integer> reminderID = new ArrayList<>();
        for (ReminderBO currRem : reminderService.getMedReminders())
            reminderID.add((int)(long) currRem.getMedId());
        return reminderID;
    }

    private ArrayList<Integer> getReminderID() {
        ArrayList<Integer> reminderID = new ArrayList<>();
        for (ReminderBO currRem : reminderService.getMedReminders())
            reminderID.add((int)(long) currRem.getId());
        for (ReminderBO currRem : reminderService.getMoodReminders())
            reminderID.add((int)(long) currRem.getId());
        return reminderID;
    }

    private ArrayList<Integer> getMoodLevelReminderID() {
        ArrayList<Integer> moodLevelReminderID = new ArrayList<>();
        for (int i = 0; i < moodLevelService.getAllData().size(); i++)
            moodLevelReminderID.add((int) (long) moodLevelService.getAllData().get(i).getReminderId());
        return moodLevelReminderID;
    }

    private ArrayList<Integer> getMoodLevelID() {
        ArrayList<Integer> moodLevelID = new ArrayList<>();
        for (int i = 0; i < medicineService.getAllData().size(); i++)
            moodLevelID.add((int) (long) medicineService.getAllData().get(i).getId());
        return moodLevelID;
    }

    private ArrayList<Integer> getMedicineReminderCount() {
        ArrayList<Integer> medicineReminderCount = new ArrayList<>();
        for (int i = 0; i < medicineService.getAllData().size(); i++)
            medicineReminderCount.add(medicineService.getAllData().get(i).getMedReminderCount());
        return medicineReminderCount;
    }

    private ArrayList<String> getMedicineInterval() {
        ArrayList<String> medicineInterval = new ArrayList<>();
        for (int i = 0; i < medicineService.getAllData().size(); i++)
            medicineInterval.add(medicineService.getAllData().get(i).getInterval());
        return medicineInterval;
    }

    private ArrayList<Integer> getMedicineId() {
        ArrayList<Integer> medicineId = new ArrayList<>();
        for (int i = 0; i < medicineService.getAllData().size(); i++)
            medicineId.add((int) (long) medicineService.getAllData().get(i).getId());
        return medicineId;
    }

    private ArrayList<Integer> getMedicinesDosage() {
        ArrayList<Integer> medicinesDosage = new ArrayList<>();
        for (int i = 0; i < medicineService.getAllData().size(); i++)
            medicinesDosage.add(medicineService.getAllData().get(i).getDosage());
        return medicinesDosage;
    }

    private ArrayList<Serializable> getStepCountDates() {
        ArrayList<Serializable> stepCountDates = new ArrayList<>();
        for (int i = 0; i < activitySenseService.getAllData().size(); i++)
            stepCountDates.add(activitySenseService.getAllData().get(i).getDate());
        return stepCountDates;
    }

    private ArrayList<Integer> getActivitySenseId() {
        ArrayList<Integer> actSenseIds = new ArrayList<>();
        for (int i = 0; i < activitySenseService.getAllData().size(); i++)
            actSenseIds.add((int) (long) activitySenseService.getAllData().get(i).getId());
        return actSenseIds;
    }

    private ArrayList<Serializable> getMoodLevelDate() {
        ArrayList<Serializable> list = new ArrayList<>();
        for (int i = 0; i < moodLevelService.getAllData().size(); i++)
            list.add(moodLevelService.getAllData().get(i).getDate());
        return list;
    }

    private ArrayList<Integer> getMoodLevel() {
        ArrayList<Integer> moodlevel = new ArrayList<>();
        for (int i = 0; i < moodLevelService.getAllData().size(); i++)
            moodlevel.add(moodLevelService.getAllData().get(i).getMoodLevel());
        return moodlevel;
    }

    private String getUserNameToTopic() {
        try {
            username = profileService.getProfileData().getUserName();
        } catch (DaoException e) {
            username = "";
        }
        return username;
    }

    private ArrayList<Integer> getStepCount() {
        ArrayList<Integer> stepCount = new ArrayList<>();
        for (int i = 0; i < activitySenseService.getAllData().size(); i++)
            stepCount.add(activitySenseService.getAllData().get(i).getStepCount());
        return stepCount;
    }

    public class PushReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent i) {
            String topic = i.getStringExtra(MQTTService.TOPIC);
            String message = i.getStringExtra(MQTTService.MESSAGE);
            Toast.makeText(context, "Push message received - " + topic + ":" + message, Toast.LENGTH_LONG).show();
        }
    }

    class ServiceHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MQTTService.SUBSCRIBE:
                    break;
                case MQTTService.PUBLISH:
                    break;
                case MQTTService.REGISTER:
                    break;
                default:
                    super.handleMessage(msg);
                    return;
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this) && !isSimplePreferences(this);
    }

    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    private static boolean isSimplePreferences(Context context) {
        return ALWAYS_SIMPLE_PREFS
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
                || !isXLargeTablet(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        if (!isSimplePreferences(this)) {
            loadHeadersFromResource(R.xml.pref_headers, target);
        }
    }

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {

                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                preference
                        .setSummary(index >= 0 ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {

                if (TextUtils.isEmpty(stringValue)) {

                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {

                        preference.setSummary(null);
                    } else {

                        String name = ringtone
                                .getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {

                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference
                .setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        sBindPreferenceSummaryToValueListener.onPreferenceChange(
                preference,
                PreferenceManager.getDefaultSharedPreferences(
                        preference.getContext()).getString(preference.getKey(),
                        ""));
    }

    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);

            bindPreferenceSummaryToValue(findPreference("example_text"));
            bindPreferenceSummaryToValue(findPreference("example_list"));
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends
            PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);

            bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DataSyncPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            bindPreferenceSummaryToValue(findPreference("sync_frequency"));
        }
    }
}

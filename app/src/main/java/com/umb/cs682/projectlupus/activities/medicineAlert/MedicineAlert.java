package com.umb.cs682.projectlupus.activities.medicineAlert;

import com.umb.cs682.projectlupus.R;
import com.umb.cs682.projectlupus.activities.main.Home;
import com.umb.cs682.projectlupus.util.Constants;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.GregorianCalendar;

public class MedicineAlert extends Activity {
    private String parent = null;
    private boolean isInit;
    Button addMed;

    Button setAlert, removeAlert;
    NotificationManager notificationManager;
    boolean isNotificActive = false;
    int notifID = 11;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_medicine_alert);

        setAlert=(Button)findViewById(R.id.setAlert);
        removeAlert=(Button)findViewById(R.id.removeAlert);

        addMed = (Button) findViewById(R.id.button3);
        addMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMedicine();
            }
        });
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        parent = getIntent().getStringExtra(Constants.PARENT_ACTIVITY_NAME);
        isInit = getIntent().getBooleanExtra(Constants.IS_INIT, false);
        if(parent != null) {
            Log.i(Constants.ACTIVITY_SENSE, parent);
            if (parent.equals(Constants.ACTIVITY_SENSE)|| isInit) {
                actionBar.setTitle(R.string.title_init_medicine_alert);
                isInit = true;
            } else {
                actionBar.setTitle(R.string.title_medicine_alert);
            }
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
        if(parent!=null && parent.equals(Constants.ACTIVITY_SENSE)) {
            getMenuInflater().inflate(R.menu.m_action_finish, menu);
        }
        return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_finish) {
			next();
		}
		return super.onOptionsItemSelected(item);
	}

    //To identify the parent activity at run-time and provide up navigation accordingly
    @Override
    public Intent getParentActivityIntent(){
        Intent newIntent = null;
        newIntent = new Intent(this, getIntent().getClass());
        return newIntent;
    }

    public void next(){
        Intent intent = new Intent();
        startActivity(intent.setClass(this, Home.class));
    }

    public void addMedicine(){
        Intent intent = new Intent();
        intent.putExtra(Constants.IS_INIT, isInit);
        startActivity(intent.setClass(this, AddMedicine.class));
    }


    public void add_alert(View view){
        Long alertTime= new GregorianCalendar().getTimeInMillis()+5*1000;
        Intent alertIntent = new Intent(this,AlertReceiver.class);
        AlarmManager alarmManager = (AlarmManager)
                getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, alertTime,
                PendingIntent.getBroadcast(this,1,alertIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT));

    }/*public void add_alert(View view) {
        NotificationCompat.Builder notificBuilder = new
                NotificationCompat.Builder(this)
                .setContentTitle("Warm Reminder")
                .setContentText("It's the right time to eat medicine.")
                .setTicker("Alert New Message")
                .setSmallIcon(R.drawable.clock_icon);

        Intent moreInforInternt = new Intent(this, MoreInfoNotification.class);

        TaskStackBuilder tStackBuilder =TaskStackBuilder.create(this);
        tStackBuilder.addParentStack(MoreInfoNotification.class);
        tStackBuilder.addNextIntent(moreInforInternt);
        PendingIntent pendingIntent=tStackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);
        notificBuilder.setContentIntent(pendingIntent);
        notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notifID,notificBuilder.build());
        isNotificActive = true;


    }*/

    public void remove_alert(View view) {
        if(isNotificActive){
            notificationManager.cancel(notifID);
        }
    }

}

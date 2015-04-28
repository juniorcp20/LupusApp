package com.umb.cs682.projectlupus.activities.common;

import com.umb.cs682.projectlupus.R;
import com.umb.cs682.projectlupus.activities.activitySense.ActivitySense;
import com.umb.cs682.projectlupus.activities.medicineAlert.MedicinePopUp;
import com.umb.cs682.projectlupus.activities.moodAlert.MoodAlert;
import com.umb.cs682.projectlupus.activities.moodAlert.MoodPopUp;
import com.umb.cs682.projectlupus.util.Constants;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class About extends Activity {
    Button moodButton;
    Button medButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_about);

        moodButton = (Button) findViewById(R.id.b_mood);
        medButton = (Button) findViewById(R.id.b_med);

        moodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                startActivity(intent.setClass(getApplicationContext(), MoodPopUp.class));
            }
        });

        medButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                startActivity(intent.setClass(getApplicationContext(), MedicinePopUp.class));
            }
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.about, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

    public void openActSense(View view){
        Intent intent = new Intent(this, ActivitySense.class);
        intent.putExtra(Constants.PARENT_ACTIVITY_NAME,"About");
        startActivity(intent);
    }

    public void openMoodAlert(View view){
        Intent intent = new Intent(this, MoodAlert.class);
        startActivity(intent);
    }
}

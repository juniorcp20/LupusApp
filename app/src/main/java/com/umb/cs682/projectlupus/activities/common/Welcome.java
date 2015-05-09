package com.umb.cs682.projectlupus.activities.common;

import com.umb.cs682.projectlupus.R;
import com.umb.cs682.projectlupus.activities.main.Home;
import com.umb.cs682.projectlupus.util.Constants;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class Welcome extends Activity {
    private final String TAG = "projectlupus.activities";
    private boolean isInit = false;
    Button go;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_welcome);
        ActionBar actionBar = getActionBar();
        go = (Button) findViewById(R.id.b_welcome_go);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });
	}

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.m_action_skip, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_skip) {
            skip();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void next(){
        Intent intent = new Intent();
        intent.putExtra(Constants.PARENT_ACTIVITY_NAME, Constants.WELCOME);
        startActivity(intent.setClass(this, Profile.class));
    }

    public void skip(){
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }
}

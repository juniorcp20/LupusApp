package com.umb.cs682.projectlupus.activities.common;

import com.umb.cs682.projectlupus.R;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class Welcome extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_welcome);
        ActionBar actionBar = getActionBar();
	}

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.m_welcome, menu);
        return true;
    }
}

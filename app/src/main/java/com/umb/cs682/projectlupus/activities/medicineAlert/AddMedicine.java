package com.umb.cs682.projectlupus.activities.medicineAlert;

import com.umb.cs682.projectlupus.R;
import com.umb.cs682.projectlupus.util.Constants;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class AddMedicine extends Activity {
    private boolean isInit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_add_medicine);
        isInit = getIntent().getBooleanExtra(Constants.IS_INIT, false);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.Tab existingMedTab = actionBar.newTab().setText("Use Existing");
        ActionBar.Tab newMedTab = actionBar.newTab().setText("Add New");
        ActionBar.Tab instTab = actionBar.newTab().setText("Instructions");

        Fragment existingMedicineFragment = new ExistingMedicineFragment();
        Fragment newMedicineFragment = new NewMedicineFragment();
        Fragment instFragment = new InstructionsFragment();

        existingMedTab.setTabListener(new AppTabListener(existingMedicineFragment));
        newMedTab.setTabListener(new AppTabListener(newMedicineFragment));
        instTab.setTabListener(new AppTabListener(instFragment));

        actionBar.addTab(existingMedTab);
        actionBar.addTab(newMedTab);
        actionBar.addTab(instTab);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.m_action_save, menu);
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

    //To identify the parent activity at run-time and provide up navigation accordingly
    @Override
    public Intent getParentActivityIntent(){
        Intent newIntent = null;
        newIntent = new Intent(this, getIntent().getClass());
        newIntent.putExtra(Constants.IS_INIT, isInit);
        return newIntent;
    }

    class AppTabListener implements ActionBar.TabListener{
        public Fragment fragment;

        public AppTabListener(Fragment fragment){
            this.fragment = fragment;
        }

        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            ft.replace(R.id.tabContainer, fragment);
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

        }
    }
}

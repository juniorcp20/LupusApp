package com.umb.cs682.projectlupus.activities.main;

/**
 * Created by mark on 4/27/15.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.umb.cs682.projectlupus.R;
import com.umb.cs682.projectlupus.activities.common.Welcome;
import com.umb.cs682.projectlupus.util.Constants;
import com.umb.cs682.projectlupus.util.SharedPreferenceManager;

/**
 * Created by mark on 4/27/15.
 */
public class Splash extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();
        setContentView(R.layout.splash);

        final ImageView iv = (ImageView)findViewById(R.id.imageView);
        final Animation an = AnimationUtils.loadAnimation(getBaseContext(),R.anim.rotate);

        iv.startAnimation(an);
        an.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                finish();
                SharedPreferenceManager.initPrefs();//todo delete before distribution
                openApp();
                /*Intent i = new Intent(getBaseContext(),Welcome.class);
                startActivity(i);*/

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }

    private void openApp() {
        boolean isInit = SharedPreferenceManager.isFirstRun();
        Intent intent = new Intent();
        if(isInit){
            intent.setClass(this, Welcome.class);
        }else{
            intent.setClass(this, Home.class);
        }
        startActivity(intent);
    }
}

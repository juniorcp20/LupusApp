package com.umb.cs682.projectlupus.util;

import android.content.Context;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by Nithya Kiran on 4/17/2015.
 */
public class Utils {
    public static void displayToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static int getSpinnerIndex(Spinner spinner, String value){
        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)){
                index = i;
                break;
            }
        }
        return index;
    }

    public static int getSnoozeInterval(String interval) {
        int retval = 0;
        switch(interval){
            case Constants.MIN_5:
                retval = 5;
                break;
            case Constants.MIN_15:
                retval = 15;
                break;
            case Constants.MIN_30:
                retval = 30;
                break;
            case Constants.MIN_60:
                retval = 60;
                break;
        }
        return retval;
    }
}

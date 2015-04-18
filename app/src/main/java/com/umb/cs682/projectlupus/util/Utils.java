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
}

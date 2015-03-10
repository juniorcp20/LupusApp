package com.umb.cs682.projectlupus.activities.medicineAlert;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umb.cs682.projectlupus.R;

/**
 * Created by Nithya Kiran on 3/6/2015.
 */
public class ExistingMedicineFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_add_existing_medicine, container, false);
        return view;
    }
}

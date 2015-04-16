package com.umb.cs682.projectlupus.activities.medicineAlert;

import android.app.Dialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.umb.cs682.projectlupus.R;

import java.util.ArrayList;
import java.util.Calendar;

public class NewMedicineFragment extends Fragment implements AdapterView.OnItemClickListener{
    private int selHour;
    private int selMin;
    private StringBuilder selectedTime;
    private ArrayList<String> strArr;
    private boolean isNew = false;
    private boolean is24hrFormat = false;
    private int selTimePos;

    private Button addMedAlertBtn;
    private ListView alertsList;
    private TimePickerDialog timePicker;

    private AddMedicineAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_add_new_medicine, container, false);
        addMedAlertBtn = (Button)view.findViewById(R.id.set_reminder_button2);
        alertsList = (ListView)view.findViewById(R.id.add_new_med_listView);
        alertsList.setVisibility(View.INVISIBLE);
        strArr = new ArrayList<String>();

        adapter = new AddMedicineAdapter(getActivity().getApplicationContext(),strArr);
        alertsList.setAdapter(adapter);
        alertsList.setOnItemClickListener(this);
        setupTimePicker();
        addMedAlertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isNew = true;
                timePicker.show();
            }
        });
        return view;
    }

    private void setupTimePicker() {
        Calendar cal = Calendar.getInstance();
        if(DateFormat.is24HourFormat(getActivity())){
            is24hrFormat = true;
        }
        timePicker = new TimePickerDialog(getActivity(), mTimeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), is24hrFormat);
    }

    /** Callback received when the user "picks" a time in the dialog */
    private TimePickerDialog.OnTimeSetListener mTimeSetListener =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    selHour = hourOfDay;
                    selMin = minute;
                    selectedTime = new StringBuilder();
                    selectedTime.append(pad(selHour)).append(":").append(pad(selMin));
                    if(isNew){
                        strArr.add(selectedTime.toString());
                    }else{
                        strArr.set(selTimePos,selectedTime.toString());
                    }
                    adapter.notifyDataSetChanged();
                    alertsList.setVisibility(View.VISIBLE);
                    displayToast();
                }
            };

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        isNew = false;
        selTimePos = position;
        timePicker.show();
        ImageView del = (ImageView) view.findViewById(R.id.delete_icon);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strArr.remove(selTimePos);
                adapter.notifyDataSetChanged();
                Toast.makeText(getActivity().getApplicationContext(), "Alert Removed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayToast() {
        Toast.makeText(getActivity().getApplicationContext(), new StringBuilder().append("Alert set at ").append(selectedTime), Toast.LENGTH_SHORT).show();
    }

    public class AddMedicineAdapter extends ArrayAdapter<String> {
        public AddMedicineAdapter(Context context, ArrayList<String> times) {
            super(context, R.layout.li_reminder_item, times);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View customView = inflater.inflate(R.layout.li_reminder_item, parent, false);
            String time = getItem(position);
            TextView displayTime = (TextView) customView.findViewById(R.id.display_time);
            ImageView actionIcon = (ImageView) customView.findViewById(R.id.delete_icon);
            actionIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    strArr.remove(position);
                    if(strArr.isEmpty()){
                        alertsList.setVisibility(View.INVISIBLE);
                    }
                    notifyDataSetChanged();
                }
            });
            displayTime.setText(time);
            actionIcon.setImageResource(R.drawable.ic_action_discard);
            return customView;
        }
    }
}

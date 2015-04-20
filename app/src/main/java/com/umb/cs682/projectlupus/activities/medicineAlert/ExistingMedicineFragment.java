package com.umb.cs682.projectlupus.activities.medicineAlert;

import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.umb.cs682.projectlupus.R;
import com.umb.cs682.projectlupus.config.AppConfig;
import com.umb.cs682.projectlupus.service.MedicineService;
import com.umb.cs682.projectlupus.service.ReminderService;
import com.umb.cs682.projectlupus.util.DateUtil;
import com.umb.cs682.projectlupus.util.Utils;

import java.util.ArrayList;
import java.util.Calendar;

import de.greenrobot.dao.DaoException;

public class ExistingMedicineFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static final String TAG = "activities.medAlert";

    private boolean isNew = false;
    //private boolean is24hrFormat = false;

    private int selHour;
    private int selMin;

    private long selRemID;
    private long selMedID = 1;

    private StringBuilder selectedTime;
    private ArrayList<Long> remIDs;
    private ArrayList<String> medNames;

    private Spinner medName;
    private Spinner units;
    private Button addRemBtn;
    private ListView remList;

    private TimePickerDialog timePicker;

    private AddMedicineAdapter listAdapter;

    private ReminderService reminderService = AppConfig.getReminderService();
    private MedicineService medService = AppConfig.getMedicineService();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_add_existing_medicine, container, false);
        medNames = medService.getAllMedicineNames();
        remIDs = reminderService.getAllMedReminderIDs();
        if(remIDs.size() == 0){
            remList.setVisibility(View.INVISIBLE);
        }

        units = (Spinner)view.findViewById(R.id.sp_units_exist);
        units.setSelection(Utils.getSpinnerIndex(units, medService.getUnitOfMedicine(selMedID)));

        medName = (Spinner)view.findViewById(R.id.sp_medname);
        ArrayAdapter<String> medNameAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, medNames);
        medNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        medName.setAdapter(medNameAdapter);
        medName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selMedID = position;
                units.setSelection(Utils.getSpinnerIndex(units, medService.getUnitOfMedicine(selMedID)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Utils.displayToast(getActivity(),"Please select a medicine");
            }
        });

        addRemBtn = (Button)view.findViewById(R.id.set_reminder_button1);
        addRemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isNew = true;
                timePicker.show();
            }
        });

        remList = (ListView)view.findViewById(R.id.add_exstng_med_listView);
        listAdapter = new AddMedicineAdapter(getActivity().getApplicationContext(), remIDs);
        remList.setAdapter(listAdapter);
        remList.setOnItemClickListener(this);

        setupTimePicker();

        return view;
    }

    private void setupTimePicker() {
        Calendar cal = Calendar.getInstance();
        /*if(DateFormat.is24HourFormat(getActivity())){
            is24hrFormat = true;
        }*/
        timePicker = new TimePickerDialog(getActivity(), mTimeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), DateUtil.is24hrFormat);
    }

    /** Callback received when the user "picks" a time in the dialog */
    private TimePickerDialog.OnTimeSetListener mTimeSetListener =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    selMin = minute;
                    String am_pm = null;
                    if(!DateUtil.is24hrFormat) {
                        if (hourOfDay > 12)         //hourofDay =13
                        {
                            selHour = hourOfDay - 12;     //hour=1
                            am_pm = "PM";                   //PM
                        } else {
                            selHour = hourOfDay;
                            am_pm = "AM";
                        }
                    }else{
                        selHour = hourOfDay;
                    }
                    selectedTime = new StringBuilder();
                    selectedTime.append(pad(selHour)).append(":").append(pad(selMin));
                    if(am_pm != null){
                        selectedTime.append(" ").append(am_pm);
                    }
                    if(isNew){
                        try {
                            remIDs.add(reminderService.addMedReminder(selMedID, selectedTime.toString()));
                        }catch (DaoException e){
                            Utils.displayToast(getActivity().getApplicationContext(), e.getMessage());
                        }
                    }else{
                        reminderService.editMedReminder(selRemID, selectedTime.toString());
                    }
                    listAdapter.notifyDataSetChanged();
                    remList.setVisibility(View.VISIBLE);
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
        selRemID = remIDs.get(position);
        timePicker.show();
    }

    private void displayToast() {
        Toast.makeText(getActivity().getApplicationContext(), new StringBuilder().append("Alert set at ").append(selectedTime), Toast.LENGTH_SHORT).show();
    }

    public class AddMedicineAdapter extends ArrayAdapter<Long> {
        public AddMedicineAdapter(Context context, ArrayList<Long> times) {
            super(context, R.layout.li_reminder_item, times);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View customView = inflater.inflate(R.layout.li_reminder_item, parent, false);
            String time = DateUtil.toTimeString(reminderService.getReminderTimeByID(getItem(position)));
            TextView displayTime = (TextView) customView.findViewById(R.id.display_time);
            ImageView actionIcon = (ImageView) customView.findViewById(R.id.delete_icon);
            actionIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    remIDs.remove(position);
                    if(remIDs.isEmpty()){
                        remList.setVisibility(View.INVISIBLE);
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

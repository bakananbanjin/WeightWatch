package com.bakananbanjinApp2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import androidx.appcompat.widget.TooltipCompat;


import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

public class InsertDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogFragmentStyle);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View insertView = inflater.inflate(R.layout.insert_window, null);

        //use Hashset to prevent multiple entrances in drop down menu code was added after Engine.getString was implemented
        HashSet<String> autofillWhatSet = new HashSet<>(Arrays.asList(Engine.getStringAdaptar()));
        List<String> autofillWhatList = new ArrayList<>(autofillWhatSet);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, autofillWhatList);

        Calendar calendar = Calendar.getInstance();
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH);
        int day = Calendar.getInstance().get(Calendar.DATE);
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int min = Calendar.getInstance().get(Calendar.MINUTE);

        //preset time in insert Window
        TimePicker tpTimepicker = insertView.findViewById(R.id.insert_timePicker);
        tpTimepicker.setIs24HourView(true);
        tpTimepicker.setHour(hour);
        tpTimepicker.setMinute(min);

        //preset date in insert Window
        DatePicker dpDatepicker = insertView.findViewById(R.id.insert_datePicker);
        dpDatepicker.init(year, month, day, null);

        AutoCompleteTextView etInsertWhat = insertView.findViewById(R.id.insert_what);
        etInsertWhat.setAdapter(adapter);
        EditText etInsertCal = insertView.findViewById(R.id.insert_cal);

        Button btnInsertCancel = insertView.findViewById(R.id.insert_cancel);
        Button btnInsertOk = insertView.findViewById(R.id.insert_ok);

        btnInsertCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        //insert button code follows later for now only dismiss Alertbox
        btnInsertOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataItem userEnteredDataItem;
                String itemName;
                Integer itemYear;
                Integer itemMonth;
                Integer itemDay;
                Integer itemHour;
                Integer itemMin;

                //Textfields needs to be casted to Integer
                try {
                    itemName = etInsertWhat.getText().toString();
                    if(itemName.isEmpty()){
                        throw new Exception();
                    }
                    itemYear = dpDatepicker.getYear();
                    itemMonth = dpDatepicker.getMonth();;
                    itemDay = dpDatepicker.getDayOfMonth();
                    itemHour = tpTimepicker.getHour();
                    itemMin = tpTimepicker.getMinute();

                    Integer itemCal = Integer.parseInt(etInsertCal.getText().toString());

                    userEnteredDataItem = new DataItem(itemName, itemCal, itemYear, itemMonth, itemDay, itemHour, itemMin);
                    Engine.insertNewDataItem(userEnteredDataItem);
                    //get reference to Overview and update cal left
                    Overview fragment = (Overview) getParentFragmentManager().findFragmentById(R.id.fragment_container);
                    if (fragment != null) {
                        fragment.updateOverview();
                    }
                    dismiss();
                } catch (Exception e) {
                    //Log.e("INSERT WRONG TYPE", "insert wrong type in INSERT FIELD");
                    String message = getResources().getString(R.string.insert_no_input);
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                }
           }
        });


        /*
        +
        +TEST ONLY DELETE LATER
        */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            etInsertCal.setTooltipText("TEST");
            btnInsertOk.setTooltipText("TEST");
        } else {
            TooltipCompat.setTooltipText(etInsertCal, "ELSE");
            TooltipCompat.setTooltipText(btnInsertOk, "ELSE");
        }

        //setview and retrun Dialog window
        builder.setView(insertView).setMessage(R.string.insert_meal);
        return builder.create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}

package com.bakananbanjinApp2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class InsertDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View insertView = inflater.inflate(R.layout.insert_window, null);

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


        EditText etInsertWhat = insertView.findViewById(R.id.insert_what);
        EditText etInsertCal = insertView.findViewById(R.id.insert_cal);

        Button btnInsertCancel = insertView.findViewById(R.id.insert_cancel);
        Button btnInsertOk = insertView.findViewById(R.id.insert_ok);

        builder.setView(insertView).setMessage(R.string.insert_meal);

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
                    dismiss();
                } catch (Exception e) {
                    Log.e("INSERT WRONG TYPE", "insert wrong type in INSERT FIELD");
                    String message = getResources().getString(R.string.insert_no_input);
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                }
           }
        });

        return builder.create();
    }
}
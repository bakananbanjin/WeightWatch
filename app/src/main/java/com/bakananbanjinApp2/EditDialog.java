package com.bakananbanjinApp2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.TooltipCompat;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class EditDialog extends DialogFragment {
    private DataItem mDataItem;

    public EditDialog(DataItem dataItem) {
        mDataItem = dataItem;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View insertView = inflater.inflate(R.layout.insert_window, null);

        Calendar calendar = Calendar.getInstance();
        int year = mDataItem.getYear();
        int month = mDataItem.getMonth();
        int day = mDataItem.getDay();
        int hour = mDataItem.getHour();
        int min = mDataItem.getMin();

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
        etInsertWhat.setText(mDataItem.getmItemName());
        etInsertCal.setText(mDataItem.getmCal() + "");

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
                //update mDataItem with new user input
                try {
                    if(etInsertWhat.getText().toString().isEmpty()){
                        throw new Exception();
                    }
                    mDataItem.setmItemName(etInsertWhat.getText().toString());
                    mDataItem.setmCalendar(dpDatepicker.getYear(), dpDatepicker.getMonth(), dpDatepicker.getDayOfMonth(),
                            tpTimepicker.getHour(), tpTimepicker.getMinute());

                    mDataItem.setmCal(Integer.parseInt(etInsertCal.getText().toString()));

                    Engine.mDB.updateItem(mDataItem);
                    //get reference to Overview and update cal left

                    EditDateFrag.myAdapter.notifyDataSetChanged();
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
}

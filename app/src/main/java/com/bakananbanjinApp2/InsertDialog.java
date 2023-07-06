package com.bakananbanjinApp2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

        EditText etInsertWhat = insertView.findViewById(R.id.insert_what);
        EditText etInsertCal = insertView.findViewById(R.id.insert_cal);
        EditText etInsertTime = insertView.findViewById(R.id.insert_time);
        EditText etInsertDate = insertView.findViewById(R.id.insert_date);

        //make a calendar to get date and time set it as pretext to insert Time and Date field
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        etInsertDate.setText(dateFormat.format(calendar.getTime()));
        etInsertTime.setText(timeFormat.format(calendar.getTime()));

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
                dismiss();
            }
        });

        return builder.create();
    }
}

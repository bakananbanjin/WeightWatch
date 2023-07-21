package com.bakananbanjinApp2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class InsertWeightDialog extends DialogFragment{
    private Weight mDataItem;
    private AlertDialog.Builder builder;
    private NumberPicker npInsertWeightKilo;
    private NumberPicker npInsertWeightGram;
    private DatePicker dpInsertWeightDate;
    private Button btInsertWeightOk;
    private  Calendar calendar;

    public InsertWeightDialog(Weight dataItem) {
        mDataItem = dataItem;
    }
    public InsertWeightDialog(){
        mDataItem = null;

    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View insertView = inflater.inflate(R.layout.insert_weight_dialog, null);
        if(mDataItem == null){
            insertNewWeight(insertView);
        }
        else {
            updateWeight(insertView);
        }

        btInsertWeightOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                float kilo = npInsertWeightKilo.getValue();
                //divide the gram int by 10 so you can the the gram value
                float gram = ((float)npInsertWeightGram.getValue())/10.f;
                //add kilo and gramm to get the weight to insert
                float weighttoinsert = kilo + gram;
                    calendar.set(dpInsertWeightDate.getYear(), dpInsertWeightDate.getMonth(), dpInsertWeightDate.getDayOfMonth());
                if(mDataItem == null){
                    Engine.insertWeight(weighttoinsert, calendar );
                    dismiss();
                } else {
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);

                    Engine.mDB.updatetWeightData(mDataItem.getId(), weighttoinsert, year, month, day, hour, minute);
                    //workaround because adapter wouldnt update view should be corrected
                    EditDateFrag.tv_edit_weight.performClick();
                    EditDateFrag.myWeightAdapter.notifyDataSetChanged();
                    dismiss();
                }

            }
        });

        builder.setView(insertView).setMessage(R.string.insert_weight_title);
        return builder.create();
    }
    private void insertNewWeight(View insertView){
        calendar = Calendar.getInstance();
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH);
        int day = Calendar.getInstance().get(Calendar.DATE);
        int kilo = (int)MainActivity.user.getUserWeight();

        npInsertWeightKilo = insertView.findViewById(R.id.np_insert_weight_number);
        npInsertWeightGram = insertView.findViewById(R.id.np_insert_weight_floatvalue);
        dpInsertWeightDate = insertView.findViewById(R.id.dp_insert_weight);
        btInsertWeightOk = insertView.findViewById(R.id.insert_weight_ok);

        npInsertWeightGram.setMaxValue(9);
        npInsertWeightGram.setMinValue(0);
        npInsertWeightGram.setValue(0);

        npInsertWeightKilo.setMinValue(30);
        npInsertWeightKilo.setMaxValue(250);
        npInsertWeightKilo.setValue(kilo);
    }
    private void updateWeight(View insertView){
        calendar = mDataItem.getCalendar();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int kilo = (int)mDataItem.getWeight();
        int gram = Math.round((mDataItem.getWeight()-(float)kilo)*10);

        npInsertWeightKilo = insertView.findViewById(R.id.np_insert_weight_number);
        npInsertWeightGram = insertView.findViewById(R.id.np_insert_weight_floatvalue);
        dpInsertWeightDate = insertView.findViewById(R.id.dp_insert_weight);
        btInsertWeightOk = insertView.findViewById(R.id.insert_weight_ok);

        dpInsertWeightDate.updateDate( year, month, day);

        btInsertWeightOk.setText(R.string.update);

        npInsertWeightGram.setMaxValue(9);
        npInsertWeightGram.setMinValue(0);
        npInsertWeightGram.setValue(gram);

        npInsertWeightKilo.setMinValue(30);
        npInsertWeightKilo.setMaxValue(250);
        npInsertWeightKilo.setValue(kilo);

    }
}

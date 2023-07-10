package com.bakananbanjinApp2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class CreateUser extends DialogFragment {
    //default values for age height weight
    private int mAge = 30;
    private int mWeight = 70;
    private int mHeight = 170;
    private String mUserName = "";
    private int mBmi = -1;
    private int mCalNeed = -1;
    private boolean mMan = true;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View insertView = inflater.inflate(R.layout.create_user, null);

        EditText etUserName = insertView.findViewById(R.id.createUser_ET_Name);
        mUserName = etUserName.getText().toString();
        Switch switchIsMan = insertView.findViewById(R.id.createUser_Switch_sex);
        mMan = switchIsMan.isActivated();

        //GET Pickers and set values
        NumberPicker npWeight = insertView.findViewById(R.id.createUser_NP_weight);
        npWeight.setMaxValue(200);
        npWeight.setMinValue(30);
        npWeight.setValue(mWeight);

        NumberPicker npAge = insertView.findViewById(R.id.createUser_NP_age);
        npAge.setMaxValue(100);
        npAge.setMinValue(15);
        npAge.setValue(mAge);

        NumberPicker npHeight = insertView.findViewById(R.id.createUser_NP_height);
        npHeight.setMaxValue(250);
        npHeight.setMinValue(100);
        npHeight.setValue(mHeight);

        //GET Button
        Button btOk = insertView.findViewById(R.id.createUser_BT_ok);
        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUserName = etUserName.getText().toString();
                Engine.createUserPref(mUserName, mMan, mHeight, mWeight, mAge, 0);
                Engine.getPref();
                dismiss();
            }
        });

        //Get Information textview
        TextView informationCalNeed = insertView.findViewById(R.id.userInformation_intCal);
        TextView informationBMI = insertView.findViewById(R.id.userinformationIntBmi);




        //Numberpicker on Value ChangeListner
        npWeight.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                mWeight = npWeight.getValue();
                informationCalNeed.setText(" " + Engine.calcCalNeed(mMan, mHeight, mWeight, mAge));
                informationBMI.setText(" " + Engine.calcBMI(mWeight, mHeight));
            }
        });

        npAge.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                mAge = npAge.getValue();
                informationCalNeed.setText(" " + Engine.calcCalNeed(mMan, mHeight, mWeight, mAge));
                informationBMI.setText(" " + Engine.calcBMI(mWeight, mHeight));
            }
        });

        npHeight.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                mHeight = npHeight.getValue();
                informationCalNeed.setText(" " + Engine.calcCalNeed(mMan, mHeight, mWeight, mAge));
                informationBMI.setText(" " + Engine.calcBMI(mWeight, mHeight));
            }
        });

        switchIsMan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Perform actions based on the switch state change
                if (isChecked) {
                    mMan = true;
                } else {
                    mMan = false;
                }
                informationCalNeed.setText(" " + Engine.calcCalNeed(mMan, mHeight, mWeight, mAge));
                informationBMI.setText(" " + Engine.calcBMI(mWeight, mHeight));
            }
        });

        builder.setView(insertView);
        return builder.create();
    }

    private void update(){

    }
}

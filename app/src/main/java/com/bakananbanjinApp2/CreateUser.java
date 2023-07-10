package com.bakananbanjinApp2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class CreateUser extends DialogFragment {
    private int mAge = -1;
    private int mWeight = -1;
    private int mHeight = -1;
    private int mBmi = -1;
    private int mCalNeed = -1;
    private boolean mMan = true;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View insertView = inflater.inflate(R.layout.create_user, null);

        //GET Pickers and set values
        NumberPicker npWeight = insertView.findViewById(R.id.createUser_NP_weight);
        npWeight.setMaxValue(200);
        npWeight.setMinValue(30);
        npWeight.setValue(70);

        NumberPicker npAge = insertView.findViewById(R.id.createUser_NP_age);
        npAge.setMaxValue(100);
        npAge.setMinValue(15);
        npAge.setValue(30);

        NumberPicker npHeight = insertView.findViewById(R.id.createUser_NP_height);
        npHeight.setMaxValue(250);
        npHeight.setMinValue(100);
        npHeight.setValue(170);

        //GET Button
        Button btOk = insertView.findViewById(R.id.createUser_BT_ok);
        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        //Get Information textview
        TextView informationCalNeed = insertView.findViewById(R.id.userInformation_intCal);
        TextView informationBMI = insertView.findViewById(R.id.userinformationIntBmi);

        Switch switchIsMan = insertView.findViewById(R.id.createUser_Switch_sex);
        mMan = switchIsMan.isActivated();

        //Numberpicker on Value ChangeListner
        npWeight.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
               informationCalNeed.setText(" " + Engine.calcCalNeed(mMan, npHeight.getValue(), npWeight.getValue(), npAge.getValue()));
               informationBMI.setText(" " + Engine.calcBMI(npWeight.getValue(), npHeight.getValue()));
            }
        });

        npAge.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                informationCalNeed.setText(" " + Engine.calcCalNeed(mMan, npHeight.getValue(), npWeight.getValue(), npAge.getValue()));
                informationBMI.setText(" " + Engine.calcBMI(npWeight.getValue(), npHeight.getValue()));
            }
        });

        npHeight.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                informationCalNeed.setText(" " + Engine.calcCalNeed(mMan, npHeight.getValue(), npWeight.getValue(), npAge.getValue()));
                informationBMI.setText(" " + Engine.calcBMI(npWeight.getValue(), npHeight.getValue()));
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
                informationCalNeed.setText(" " + Engine.calcCalNeed(mMan, npHeight.getValue(), npWeight.getValue(), npAge.getValue()));
                informationBMI.setText(" " + Engine.calcBMI(npWeight.getValue(), npHeight.getValue()));
            }
        });

        builder.setView(insertView);
        return builder.create();
    }

    private void updateInformation(int weight, int height, int age, boolean men){
        mBmi = Engine.calcBMI(weight, height);
        mCalNeed = Engine.calcCalNeed(men, height, weight, age);

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View informationView = inflater.inflate(R.layout.user_information, null);

        TextView informationBmi = informationView.findViewById(R.id.userInformation_TV_BMI);
        TextView informationCalNeed = informationView.findViewById(R.id.userInformation_TV_caluse);

        informationBmi.setText(informationBmi.getText() + " " + mBmi);
        informationCalNeed.setText(informationCalNeed.getText() + " " + mCalNeed);

    }
}

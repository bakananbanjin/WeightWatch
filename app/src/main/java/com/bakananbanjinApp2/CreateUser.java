package com.bakananbanjinApp2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class CreateUser extends DialogFragment {
    //default values for age height weight
    private int mAge = 30;
    private float mWeight = 70f;
    private int mHeight = 170;
    private int mTargetWeight = 65;
    private String mUserName = "";
    private int mBmi = -1;
    private int mCalNeed = -1;
    private boolean mMan = true;

    /*+
    Low activity (little to no exercise): BMR × 1.2
    Medium active (light exercise/sports 1-3 days per week): BMR × 1.4
    High active (moderate exercise/sports 3-5 days per week): BMR × 1.6
     */
    private float mActivityLevelmultiplier = 1.2f;
    private static float LOWACTIVITY = 1.2f;
    private static float MEDIUMACTIVITY = 1.4f;
    private static float HIGHACTIVITY = 1.6f;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View insertView = inflater.inflate(R.layout.create_user, null);

        //Get Information textview
        TextView informationCalNeed = insertView.findViewById(R.id.userInformation_intCal);
        TextView informationBMI = insertView.findViewById(R.id.userinformationIntBmi);
        TextView informationTargetBMI = insertView.findViewById((R.id.userInformation_int_target_BMI));


        EditText etUserName = insertView.findViewById(R.id.createUser_ET_Name);
        mUserName = etUserName.getText().toString();
        Switch switchIsMan = insertView.findViewById(R.id.createUser_Switch_sex);
        mMan = switchIsMan.isActivated();

        RadioGroup rgActivityLevel = insertView.findViewById(R.id.rg_createuser_activity);
        RadioButton rbLowActivity = insertView.findViewById(R.id.rb_lowactivity);
        RadioButton rbMediumActivity = insertView.findViewById(R.id.rb_activitymedium);
        RadioButton rbHighActivity = insertView.findViewById(R.id.rb_activityhigh);
        rbLowActivity.setChecked(true);

        //GET Pickers and set values
        NumberPicker npWeight = insertView.findViewById(R.id.createUser_NP_weight);
        npWeight.setMaxValue(200);
        npWeight.setMinValue(30);
        npWeight.setValue((int)mWeight);

        NumberPicker npAge = insertView.findViewById(R.id.createUser_NP_age);
        npAge.setMaxValue(100);
        npAge.setMinValue(15);
        npAge.setValue(mAge);

        NumberPicker npHeight = insertView.findViewById(R.id.createUser_NP_height);
        npHeight.setMaxValue(250);
        npHeight.setMinValue(100);
        npHeight.setValue(mHeight);

        NumberPicker npTargetWeight = insertView.findViewById(R.id.createUser_NP_target_weight);
        npTargetWeight.setMaxValue(200);
        npTargetWeight.setMinValue(40);
        npTargetWeight.setValue(mTargetWeight);

        //GET Button
        Button btOk = insertView.findViewById(R.id.createUser_BT_ok);
        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUserName = etUserName.getText().toString();
                Engine.createUserPref(mUserName, mMan, mAge, mHeight, mWeight, mTargetWeight, mActivityLevelmultiplier);
                Engine.insertWeightFromCreateUser(mWeight, Calendar.getInstance());
                Engine.getPref();

                //restart Activity to update overview with new data
                //restart app to update overlay
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();

               /* Intent intent = getActivity().getIntent();
                getActivity().finish();
                startActivity(intent);
                dismiss();*/
            }
        });

        //Numberpicker on Value ChangeListner
        npWeight.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                mWeight = (float)npWeight.getValue();
                informationCalNeed.setText(" " + Engine.calcCalNeed(mMan, mHeight, mWeight, mAge, mActivityLevelmultiplier));
                informationBMI.setText(" " + Engine.calcBMI(mWeight, mHeight));
            }
        });

        npAge.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                mAge = npAge.getValue();
                informationCalNeed.setText(" " + Engine.calcCalNeed(mMan, mHeight, mWeight, mAge, mActivityLevelmultiplier));
                informationBMI.setText(" " + Engine.calcBMI(mWeight, mHeight));
            }
        });

        npHeight.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                mHeight = npHeight.getValue();
                informationCalNeed.setText(" " + Engine.calcCalNeed(mMan, mHeight, mWeight, mAge, mActivityLevelmultiplier));
                informationBMI.setText(" " + Engine.calcBMI(mWeight, mHeight));
            }
        });

        npTargetWeight.setOnValueChangedListener(new NumberPicker.OnValueChangeListener(){
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                mTargetWeight = npTargetWeight.getValue();
                informationTargetBMI.setText(" " + Engine.calcBMI(mTargetWeight, mHeight));
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
                informationCalNeed.setText(" " + Engine.calcCalNeed(mMan, mHeight, mWeight, mAge, mActivityLevelmultiplier));
                informationBMI.setText(" " + Engine.calcBMI(mWeight, mHeight));
            }
        });

        rgActivityLevel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i){
                if(i == R.id.rb_lowactivity){
                    mActivityLevelmultiplier = LOWACTIVITY;
                } else if(i == R.id.rb_activitymedium){
                    mActivityLevelmultiplier = MEDIUMACTIVITY;
                } else if(i == R.id.rb_activityhigh){
                    mActivityLevelmultiplier = HIGHACTIVITY;
                }
                informationCalNeed.setText(" " + Engine.calcCalNeed(mMan, mHeight, mWeight, mAge, mActivityLevelmultiplier));
                informationBMI.setText(" " + Engine.calcBMI(mWeight, mHeight));
            }
        });

        builder.setView(insertView);
        return builder.create();
    }

    private void update(){

    }
}

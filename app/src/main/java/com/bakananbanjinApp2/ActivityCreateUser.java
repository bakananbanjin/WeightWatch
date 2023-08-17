package com.bakananbanjinApp2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Calendar;

public class ActivityCreateUser extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        Toolbar toolbar = findViewById(R.id.toolbar_create);
        toolbar.setTitle(R.string.createUser_toolbar_title);

        //Get Information textview
        TextView informationCalNeed = findViewById(R.id.userInformation_intCal);
        TextView informationBMI = findViewById(R.id.userinformationIntBmi);
        TextView informationTargetBMI = findViewById((R.id.userInformation_int_target_BMI));

        EditText etUserName = findViewById(R.id.createUser_ET_Name);
        mUserName = etUserName.getText().toString();
        Switch switchIsMan = findViewById(R.id.createUser_Switch_sex);
        RadioGroup rgSex = findViewById(R.id.rg_sex);
        RadioButton rbMale = findViewById(R.id.rb_male);
        RadioButton rbFemale = findViewById(R.id.rb_female);
        rgSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                // Handle the selected radio button here
                // Do something for Option 3
                if (checkedId == R.id.rb_male) {
                    mMan = false;
                } else if (checkedId == R.id.rb_female) {
                    mMan = true;
                } else {
                    mMan = true;
                }
                informationCalNeed.setText(" " + Engine.calcCalNeed(mMan, mHeight, mWeight, mAge, mActivityLevelmultiplier));
                informationBMI.setText(" " + Engine.calcBMI(mWeight, mHeight));
            }
        });

        RadioGroup rgActivityLevel = findViewById(R.id.rg_createuser_activity);
        RadioButton rbLowActivity = findViewById(R.id.rb_lowactivity);
        RadioButton rbMediumActivity = findViewById(R.id.rb_activitymedium);
        RadioButton rbHighActivity = findViewById(R.id.rb_activityhigh);
        rbLowActivity.setChecked(true);

        //GET Pickers and set values
        NumberPicker npWeight = findViewById(R.id.createUser_NP_weight);
        npWeight.setMaxValue(200);
        npWeight.setMinValue(30);
        npWeight.setValue((int)mWeight);

        NumberPicker npAge = findViewById(R.id.createUser_NP_age);
        npAge.setMaxValue(100);
        npAge.setMinValue(15);
        npAge.setValue(mAge);

        NumberPicker npHeight = findViewById(R.id.createUser_NP_height);
        npHeight.setMaxValue(250);
        npHeight.setMinValue(100);
        npHeight.setValue(mHeight);

        NumberPicker npTargetWeight = findViewById(R.id.createUser_NP_target_weight);
        npTargetWeight.setMaxValue(200);
        npTargetWeight.setMinValue(40);
        npTargetWeight.setValue(mTargetWeight);

        //GET Button
        Button btOk = findViewById(R.id.createUser_BT_ok);
        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUserName = etUserName.getText().toString();
                Engine.createUserPref(mUserName, mMan, mAge, mHeight, mWeight, mTargetWeight, mActivityLevelmultiplier);
                Engine.insertWeightFromCreateUser(mWeight, Calendar.getInstance());
                Engine.getPref();

                //restart Activity to update overview with new data
                //restart app to update overlay
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
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
    }
}
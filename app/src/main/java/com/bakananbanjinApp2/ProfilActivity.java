package com.bakananbanjinApp2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfilActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView imageViewProfile;
    private TextView textViewProfileName;
    private TextView textViewProfileAge;
    private TextView textViewProfileWeight;
    private TextView textViewProfileHeight;
    private TextView textViewProfileTargetWeight;
    private TextView textViewProfileCalDay;
    private TextView textViewProfileBMI;
    private TextView textViewProfileAdvice;
    private Button btViewProfileEdit;
    private User mUser;
    private int mCalDay;
    private int mBmi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        mUser = Engine.getPref();
        mCalDay = Engine.calcCalNeed(mUser.ismIsMan(), mUser.getUserHeight(), mUser.getUserWeight(), mUser.getUserAge(), mUser.getUserActivity());
        mBmi = Engine.calcBMI(mUser.getUserWeight(),mUser.getUserHeight());
        //set toolbar as action bar and show back arrow and title
        toolbar = findViewById(R.id.toolbar_profil);
        toolbar.setTitle(R.string.profilActivity_toolbarMessage);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        imageViewProfile = findViewById(R.id.imageview_profil);
        if(!mUser.ismIsMan()){
            imageViewProfile.setImageDrawable(getDrawable(R.drawable.usermale));
        } else {
            imageViewProfile.setImageDrawable(getDrawable(R.drawable.userfemale));
        }


        textViewProfileName = findViewById(R.id.tv_profile_name);
        textViewProfileName.setText(mUser.getUserName());

        textViewProfileAge = findViewById(R.id.tv_profile_age);
        textViewProfileAge.setText(mUser.getUserAge() + " age");

        textViewProfileHeight = findViewById(R.id.tv_profile_height);
        textViewProfileHeight.setText(mUser.getUserHeight() + " cm");

        textViewProfileWeight = findViewById(R.id.tv_profile_weight);
        textViewProfileWeight.setText(mUser.getUserWeight() + " kg");

        textViewProfileTargetWeight = findViewById(R.id.tv_profile_targetweight);
        textViewProfileTargetWeight.setText( getString(R.string.profile_target) + " " + mUser.getUserTargetWeight() + " kg");

        textViewProfileCalDay = findViewById(R.id.tv_profile_Calorie);
        textViewProfileCalDay.setText(getString(R.string.profile_calroies) + " " + mCalDay);

        textViewProfileBMI = findViewById(R.id.tv_profile_bmi);
        textViewProfileBMI.setText(getString(R.string.profile_bmi) + " " + mBmi);

        textViewProfileAdvice = findViewById(R.id.tv_profile_advise);
        textViewProfileAdvice.setText(getString(R.string.profile_advice));

        btViewProfileEdit = findViewById(R.id.bt_profile_edit);
        btViewProfileEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateUser createUserDialog = new CreateUser();
                createUserDialog.show(getSupportFragmentManager(), "");
            }
        });
    }
    public void onBackPressed() {
        super.onBackPressed();
    }
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
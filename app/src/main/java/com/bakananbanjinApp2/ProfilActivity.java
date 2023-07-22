package com.bakananbanjinApp2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

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
        //code to set advice
        //get 3 weight data first last and 7 days ago
        //get all calintake from 7 days
        //decide message and build string
        String profile_progress_advice ="";
        String messageNewUser = "";
        String messageGreeting = "";
        String messageWeight = "";
        String message7days = "";
        String messageWeightResult = "";
        String messageCal = "";
        String messageIntermittent = "";
        String messageIntermittentResult = "";
        String messageEncourage = "";

        try {
            List<Weight> weighList = Engine.profileAdviceWeightNumbers();
            if(Engine.dayComparison(weighList.get(0).getCalendar(), weighList.get(2).getCalendar()) < 6){
                //no 7 days weight data new user?
                messageNewUser = getString(R.string.profile_progress_message_new_user);
            } else{
                //at least 7 days weight data is present
                if(weighList.get(1).getWeight() - weighList.get(0).getWeight() > weighList.get(0).getWeight() / 100f){
                    //we had weight lost since data recorded at least 1%
                    messageGreeting = getString(R.string.profile_progress_greeting_good);
                    messageWeight = getString(R.string.profile_progress_message_weight,
                            weighList.get(1).getWeight(),
                            Engine.calendarToDatetoString(weighList.get(1).getCalendar()),
                            weighList.get(1).getWeight()-weighList.get(0).getWeight());
                    float tempfloat = weighList.get(2).getWeight()-weighList.get(0).getWeight();
                    message7days = " " + getString(R.string.profile_progress_message_weight_7days, tempfloat);
                    messageWeightResult = "" + getString(R.string.profile_progress_message_weight_result_good);
                    float dayUntillTarget = weighList.get(0).getWeight() - (float) MainActivity.user.getUserTargetWeight() / (tempfloat/7);
                    messageEncourage = getString(R.string.profile_progress_message_encourage_good, (float)MainActivity.user.getUserTargetWeight(), (int) dayUntillTarget + 1);
                } else if(weighList.get(1).getWeight() - weighList.get(0).getWeight() > 0) {
                    //we had weigh lose less then 1 %
                    messageGreeting = getString(R.string.profile_progress_greeting_bad);
                    messageWeight = getString(R.string.profile_progress_message_weight,
                            weighList.get(1).getWeight(),
                            Engine.calendarToDatetoString(weighList.get(1).getCalendar()),
                            weighList.get(1).getWeight()-weighList.get(0).getWeight());
                    messageWeightResult = " " + getString(R.string.profile_progress_message_weight_result_bad);
                    messageEncourage = getString(R.string.profile_progress_message_encourage_bad, (float)MainActivity.user.getUserTargetWeight());
                } else {
                    //we have no weight lose or gained some weight
                    messageGreeting = getString(R.string.profile_progress_greeting_bad);
                    messageWeightResult = getString(R.string.profile_progress_message_weight_result_bad);
                    messageEncourage = getString(R.string.profile_progress_message_encourage_bad, (float)MainActivity.user.getUserTargetWeight());
                }
                //get the sum of all cal of the last 7 days
                float calIntakeLast7days = 0f;
                int counter = 0;
                for(int i = 0; i < 7; i++) {
                    float temp = Engine.mDB.calculateSumByDate(weighList.get(2).getCalendar(), DataSetDB.DB_TABLE_NAME, DataSetDB.DB_ROW_CAL);
                    calIntakeLast7days += temp;
                    weighList.get(2).getCalendar().set(Calendar.DAY_OF_MONTH, weighList.get(2).getCalendar().get(Calendar.DAY_OF_MONTH) +1);
                    //no cal intake dont increase counter
                    //POSIBILE ERROR FASTING DAY
                    if(temp > 1 ){
                        counter++;
                    }
                }
                float avgCalIntakeLast7days = 0f;
                float avgCalDeficit = 0f;
                if(counter > 0) {
                    avgCalIntakeLast7days = calIntakeLast7days / counter;
                }
                avgCalDeficit = avgCalIntakeLast7days - Engine.calcCalNeed(MainActivity.user);
                messageCal = getString(R.string.profile_progress_message_cal, avgCalIntakeLast7days, avgCalDeficit);
                messageIntermittent = getString(R.string.profile_progress_message_intermittent, DayPlanner.getIntermittenfast);
                if(DayPlanner.getIntermittenfast < 3){
                    messageIntermittentResult = getString(R.string.profile_progress_message_intermittent_bad);
                } else {
                    messageIntermittentResult = getString(R.string.profile_progress_message_intermittent_good);
                }
            }
        } catch (Exception e){
            Log.e("PROFILEPRGRESS", "error " );
        }



        profile_progress_advice = messageNewUser + messageGreeting
                + messageWeight +  message7days +  messageWeightResult
                + messageCal + messageIntermittent + messageIntermittentResult
                + messageEncourage;

        textViewProfileAdvice.setText(profile_progress_advice);
        textViewProfileAdvice.setMovementMethod(new ScrollingMovementMethod());
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
package com.bakananbanjinApp2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Overview extends Fragment {
    private TextView overviewCalLeft;
    private int intCal;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.overview_fragment, container, false);

        TextView overviewBMI = view.findViewById(R.id.overview_bmi);
        TextView overviewCal = view.findViewById(R.id.overview_cal_day);
        TextView overviewWeight = view.findViewById(R.id.overview_weight);
        overviewCalLeft = view.findViewById(R.id.overview_cal_left);

        //calculate BMI and set overview item
        int userWeight = MainActivity.mPrefs.getInt(MainActivity.WEIGHT, -1);
        int userHeight = MainActivity.mPrefs.getInt(MainActivity.HEIGHT, -1);
        int userAge = MainActivity.mPrefs.getInt(MainActivity.AGE, -1);
        boolean userIsMan = MainActivity.mPrefs.getBoolean(MainActivity.ISMAN, true);
        float userActivity = MainActivity.mPrefs.getFloat(MainActivity.ACTIVITYLEVEL, 1.2f);

        int intBMI = Engine.calcBMI(userWeight,userHeight);
        String stringOverviewBMI = getText(R.string.overview_bmi) + "\n" + intBMI;
        if(intBMI > 25){
            overviewBMI.setBackgroundResource(R.drawable.round_corner_view_red);
        } else {
            overviewBMI.setBackgroundResource(R.drawable.round_corner_view);
        }
        overviewBMI.setText(stringOverviewBMI);

        intCal = Engine.calcCalNeed(userIsMan, userHeight, userWeight, userAge, userActivity);
        String stringOverviewCal = getText(R.string.overview_cal_day) + "\n" + intCal;
        overviewCal.setBackgroundResource(R.drawable.round_corner_view);
        overviewCal.setText(stringOverviewCal);

        int intWeight = MainActivity.mPrefs.getInt(MainActivity.WEIGHT, -1);
        String stringOverviewWeight = getText(R.string.overview_weight) + "\n" + intWeight;
        overviewWeight.setBackgroundResource(R.drawable.round_corner_view);
        overviewWeight.setText(stringOverviewWeight);

        int intCalLeft = intCal - Engine.calcUsedToday();
        String stringOverviewCalLeft = getText(R.string.overview_cal_left) + "\n" + intCalLeft;
        if(intCalLeft < 0){
            overviewCalLeft.setBackgroundResource(R.drawable.round_corner_view_red);
        } else {
            overviewCalLeft.setBackgroundResource(R.drawable.round_corner_view);
        }
        overviewCalLeft.setText(stringOverviewCalLeft);

        return view;
    }
    //update ovreview cal left called when new item is added or edited
    public void updateOverview(){
        int intCalLeft = intCal - Engine.calcUsedToday();
        if(intCalLeft < 0){
            overviewCalLeft.setBackgroundResource(R.drawable.round_corner_view_red);
        } else {
            overviewCalLeft.setBackgroundResource(R.drawable.round_corner_view);
        }
        String stringOverviewCalLeft = getText(R.string.overview_cal_left) + "\n" + intCalLeft;
        overviewCalLeft.setText(stringOverviewCalLeft);
    }

}

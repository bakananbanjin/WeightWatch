package com.bakananbanjinApp2;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Overview extends Fragment {
    private TextView overviewCalLeft;
    private TextView overviewWeight;
    private ImageView ivAddWeight;
    private ImageView ivAddCal;
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
        overviewWeight = view.findViewById(R.id.overview_weight);
        overviewCalLeft = view.findViewById(R.id.overview_cal_left);
        ivAddWeight = view.findViewById(R.id.iv_overview_add_weight);
        ivAddCal = view.findViewById(R.id.iv_overview_add_cal);
        ivAddCal.setVisibility(View.INVISIBLE);

        //calculate BMI and set overview item
        float userWeight = MainActivity.mPrefs.getFloat(MainActivity.WEIGHT, -1f);
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
        overviewBMI.setPadding(0, 0, 0, 10);
        overviewBMI.setText(stringOverviewBMI);

        intCal = Engine.calcCalNeed(userIsMan, userHeight, userWeight, userAge, userActivity);
        String stringOverviewCal = getText(R.string.overview_cal_day) + "\n" + intCal;
        overviewCal.setBackgroundResource(R.drawable.round_corner_view);
        overviewCal.setPadding(0, 0, 0, 10);
        overviewCal.setText(stringOverviewCal);
        // Log.i("MPREFS WEIGHT", MainActivity.mPrefs.getAll().toString());
        int intWeight = Math.round(MainActivity.mPrefs.getFloat(MainActivity.WEIGHT, -1.0f));
        String stringOverviewWeight = getText(R.string.overview_weight) + "\n" + intWeight;
        overviewWeight.setBackgroundResource(R.drawable.round_corner_view);
        overviewWeight.setPadding(0, 0, 0, 10);
        overviewWeight.setText(stringOverviewWeight);

        int intCalLeft = intCal - Engine.calcUsedToday();
        String stringOverviewCalLeft = getText(R.string.overview_cal_left) + "\n" + intCalLeft;
        if(intCalLeft < 0){
            overviewCalLeft.setBackgroundResource(R.drawable.round_corner_view_red);
        } else {
            overviewCalLeft.setBackgroundResource(R.drawable.round_corner_view);
        }
        overviewCalLeft.setText(stringOverviewCalLeft);
        overviewCalLeft.setPadding(0, 0, 0, 10);

        ivAddWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InsertWeightDialog insertWeightDialog = new InsertWeightDialog();
                insertWeightDialog.show(getParentFragmentManager(),"");
                //Log.i("ADDWEIGHT", "add weight pressed");
            }
        });

        ivAddCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("ADDCAL", "add cal pressed");
            }
        });

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

        //get last weight save in preference and update overview
        float newWeight = Engine.getLastWeight();
        MainActivity.user.setUserWeight(newWeight);
        MainActivity.mEditor.putFloat(MainActivity.WEIGHT, newWeight);
        MainActivity.mEditor.commit();
        String stringOverviewWeight = getText(R.string.overview_weight) + "\n" + newWeight;
        overviewWeight.setText(stringOverviewWeight);
    }

}

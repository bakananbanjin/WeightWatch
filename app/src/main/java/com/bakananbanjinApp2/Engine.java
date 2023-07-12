package com.bakananbanjinApp2;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

public class Engine {
    public static DataSetDB mDB;
    private static Engine engine;
    private Context mContext;
    public Engine(Context context) {
        mContext = context;
        mDB = new DataSetDB(context);
        engine = this;
    }
    //function gets called when insert btn in Insertfield is pressed
    //class needs to be initialised
    public static boolean insertNewDataItem(DataItem dataItem){
        //1.insert new Data Item in to DB
        //2.update overview Calorie left
        if(engine == null){
            Log.e("ERROR ENGINE", "Engine is not initialized");
            return false;
        }
        mDB.insert(dataItem);
        return true;
    }
    public static int calcBMI(int weight, int height){
        //BMI = weight (kg) / (height (m))^2
        //height is in cm so we have to convert it to meter
        float heightinm = (float) height / 100.0f;

        //test if divide by 0
        float bmi = 0;
        if(height != 0) {
            bmi = (float) weight / ((float) heightinm * (float) heightinm);
        }
        return (int) bmi;
    }
    public static int calcCalNeed(boolean men, int height, int weight, int age, float activity){
        //For Men:
        //BMR = 66.5 + (13.75 × weight in kg) + (5.003 × height in cm) - (6.755 × age in years)
        //For Women:
        //BMR = 655.1 + (9.563 × weight in kg) + (1.850 × height in cm) - (4.676 × age in years)
        double calNeed = 0;
        if(!men){
            calNeed = 66.5 + (13.75 * (float) weight) + (5.003 * (float)height) - (6.755 * (float) age);
            return (int) (calNeed * activity);
        }
        calNeed = 655.1 + (9.563* (float) weight) + (1.850 * (float)height) - (4.676 * (float) age);
        return (int) (calNeed * activity);
    }
    public static void createUserPref(String name, boolean man, int height, int weight, int age, int targetWeight, float activity){
        MainActivity.mEditor.putString(MainActivity.USER, name);
        MainActivity.mEditor.putBoolean(MainActivity.ISMAN, man);
        MainActivity.mEditor.putInt(MainActivity.HEIGHT, height);
        MainActivity.mEditor.putInt(MainActivity.WEIGHT, weight);
        MainActivity.mEditor.putInt(MainActivity.AGE, age);
        MainActivity.mEditor.putInt(MainActivity.TARGETWEIGHT, targetWeight);
        MainActivity.mEditor.putFloat(MainActivity.ACTIVITYLEVEL, activity);
        MainActivity.mEditor.commit();
    }
    public static void deleteData(){
        MainActivity.mEditor.clear().commit();
        mDB.deleteAll();
    }
    public static void getPref(){
        Log.i("PREFERENCE", MainActivity.mPrefs.getAll().toString());
    }
    public static int calcUsedToday(){
        Calendar cToday = Calendar.getInstance();
        List<DataItem> dataItemsToday = null;

        dataItemsToday = mDB.selectByDateDataItem(cToday.get(Calendar.YEAR), cToday.get(Calendar.MONTH), cToday.get(Calendar.DATE));

        int calcUsed = 0;
        for(DataItem i : dataItemsToday){
            calcUsed += i.getmCal();
        }
        return calcUsed;
    }
}

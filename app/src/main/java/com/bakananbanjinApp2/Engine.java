package com.bakananbanjinApp2;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Engine {
    public static String FILENAME_USER = "user";
    public static String FILENAME_DATA = "data";
    public static String FILENAME_WEIGHT = "weight";
    public static String FOLDER_NAME = "Data";
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
    public static int calcBMI(float weight, int height){
        //BMI = weight (kg) / (height (m))^2
        //height is in cm so we have to convert it to meter
        float heightinm = (float) height / 100.0f;

        //test if divide by 0
        float bmi = 0;
        if(height != 0) {
            bmi = weight / ((float) heightinm * (float) heightinm);
        }
        return (int) bmi;
    }
    public static int calcCalNeed(boolean men, int height, float weight, int age, float activity){
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
    public static void createUserPref(String name, boolean man, int age, int height, float weight, int targetWeight, float activity){
        MainActivity.mEditor.putString(MainActivity.USER, name);
        MainActivity.mEditor.putBoolean(MainActivity.ISMAN, man);
        MainActivity.mEditor.putInt(MainActivity.HEIGHT, height);
        MainActivity.mEditor.putFloat(MainActivity.WEIGHT, weight);
        MainActivity.mEditor.putInt(MainActivity.AGE, age);
        MainActivity.mEditor.putInt(MainActivity.TARGETWEIGHT, targetWeight);
        MainActivity.mEditor.putFloat(MainActivity.ACTIVITYLEVEL, activity);
        //update Mainactivity user for write file etc should be replaced later but for now it should work
        MainActivity.user.updateUser(name, man, age, height, weight, targetWeight, activity);
        MainActivity.mEditor.commit();
    }
    public static void deleteData(){
        MainActivity.mEditor.clear().commit();
        mDB.deleteAll();
        mDB.update();
    }
    public static User getPref(){

        Log.i("PREFERENCE", MainActivity.mPrefs.getAll().toString());
        return new User(MainActivity.mPrefs.getString(MainActivity.USER, "ERROR"),
                MainActivity.mPrefs.getBoolean(MainActivity.ISMAN, true),
                MainActivity.mPrefs.getInt(MainActivity.AGE, 0),
                MainActivity.mPrefs.getInt(MainActivity.HEIGHT, 0),
                MainActivity.mPrefs.getFloat(MainActivity.WEIGHT, 0f),
                MainActivity.mPrefs.getInt(MainActivity.TARGETWEIGHT, 0),
                MainActivity.mPrefs.getFloat(MainActivity.ACTIVITYLEVEL, 1.2f));
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
    public static boolean backupAll(User user){
        if(!DataReaderWriter.writeFileUser(FILENAME_USER, engine.mContext, user)){
            return false;
        }
        if(!DataReaderWriter.writeFileDB(FILENAME_DATA, engine.mContext, mDB.selectAll())){
            return false;
        }
        return DataReaderWriter.writeFileWeight(FILENAME_WEIGHT, engine.mContext, mDB.selectAllWeight());
    }
    public static String genereteProfilAdvice(User user){
        String advise = "";

        return advise;
    }
    //get all items insert intow the db for autocompletion
    public static String[] getStringAdaptar() {
        Cursor cursor = mDB.selectColumnFromTable(DataSetDB.DB_TABLE_NAME, DataSetDB.DB_ROW_ITEM);
        List<String> adapterString = new ArrayList<>();
        if(cursor == null || cursor.getCount() <= 0){
            String[] temp = {""};
            return temp;
        }
        for(int i = 0; i < cursor.getCount(); i++){

            try {
                cursor.moveToPosition(i);
                adapterString.add(cursor.getString(0));
            } catch (Exception e) {
                Log.i("CURSOR", cursor.toString() +"****"+ cursor.getCount());
            }

        }
        String[] stockArr = new String[adapterString.size()];
        stockArr = adapterString.toArray(stockArr);

        return stockArr;
    }
    public static boolean insertWeight(int weight, Calendar calendar) {
        Engine.insertWeight((float) weight, calendar);
        return true;
    }
    public static boolean insertWeight(float weight, Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        try {
            mDB.insertWeightData(weight, year, month, day, hour, minute);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("INSERT WEIGHT", "error insert weight");
            return false;
        }
    }
    public static boolean insertWeightFromCreateUser(float weight, Calendar calendar) {
        Cursor cursor = mDB.getAllWeight();
        //WEIGHT Table is not empty use date from first entry for the new weight the date the user was created
        if (cursor != null && cursor.moveToFirst()) {
            cursor.moveToFirst();
            try {
                int firstWeightId = cursor.getInt(cursor.getColumnIndexOrThrow(DataSetDB.DB_ROW_ID));
                mDB.updateDataWeight(firstWeightId, DataSetDB.TABLEWEIGHT_ROW_WEIGHT, weight);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("INSERT WEIGHT", "error insert weight");
                return false;
            }
        } else {//WEIGHT TABLE IS EMPTY we create a entrance for the first time
            // WE create a new User use the current date from calendar
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            try {
                mDB.insertWeightData(weight, year, month, day, hour, minute);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("INSERT WEIGHT", "error insert weight");
                return false;
            }
        }
    }
    public static boolean insertIntoDBFromFiles(){

        List<DataItem> dataItemList = DataReaderWriter.readFileData(engine.mContext);
        //check if list is empty if not clear Table and insert new data
        if(dataItemList.isEmpty()){
            Log.i("NODATAITEM", "There where no items to read");
        }
        List<Weight> weightList = DataReaderWriter.readFileWeight(engine.mContext);
        if(weightList.isEmpty()){
            Log.i("NOWEIGHTDATA", "There where no weight to read");
        }
        User user = DataReaderWriter.readFileUser(engine.mContext);
        if(user == null){
            Log.i("NOUSER", "There where no user to read");
            return false;
        }
        //clear all data from DB
        deleteData();
        //insert new data
        mDB.insertList(dataItemList);
        mDB.insertWeightList(weightList);
        createUserPref(user);

        return true;
    }
    private static void createUserPref(User user) {
        createUserPref(user.getUserName(), user.ismIsMan(), user.getUserAge(), user.getUserHeight(), user.getUserWeight(), user.getUserTargetWeight(), user.getUserActivity());
    }
    public static boolean updateWeight(int id, float weight, Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        try {
            mDB.updatetWeightData(id, weight, year, month, day, hour, minute);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("INSERT WEIGHT", "error insert weight");
            return false;
        }
    }
}

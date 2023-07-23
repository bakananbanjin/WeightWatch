package com.bakananbanjinApp2;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.icu.text.DecimalFormat;

import android.util.DisplayMetrics;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Engine {
    public static float TEXTSIZHUGE = 36f;
    public static float TEXTSIZETINY = 12f;
    public static float TEXTSIZEBIG = 24f;
    public static float TEXTSIZENORMAL = 16;
    public static float TEXTSIZESMALL = 12f;
    public static String FILENAME_USER = "user";
    public static String FILENAME_DATA = "data";
    public static String FILENAME_WEIGHT = "weight";
    public static String FOLDER_NAME = "Data";
    public static DataSetDB mDB;
    private static Engine engine;
    private static Context mContext;
    public Engine(Context context) {
        mContext = context;
        mDB = new DataSetDB(context);
        engine = this;
        getTextSize(context);

    }
    //function gets called when insert btn in Insertfield is pressed
    //class needs to be initialised
    public static boolean insertNewDataItem(DataItem dataItem){
        //1.insert new Data Item in to DB
        //2.update overview Calorie left
        if(engine == null){
            //Log.e("ERROR ENGINE", "Engine is not initialized");
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
        DataReaderWriter.deleteProfilpicture(mContext);
        mDB.deleteAll();
        mDB.update();
    }
    public static User getPref(){

        //Log.i("PREFERENCE", MainActivity.mPrefs.getAll().toString());
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
                //Log.e("CURSOR", cursor.toString() +"****"+ cursor.getCount());
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
            //Log.e("INSERT WEIGHT", "error insert weight");
            return false;
        }
    }
    public static boolean insertWeightFromCreateUser(float weight, Calendar calendar) {
        Cursor cursor = mDB.selectAllWeight();
        //WEIGHT Table is not empty use date from first entry for the new weight the date the user was created
        if (cursor != null && cursor.moveToFirst()) {
            cursor.moveToFirst();
            try {
                int firstWeightId = cursor.getInt(cursor.getColumnIndexOrThrow(DataSetDB.DB_ROW_ID));
                mDB.updateDataWeight(firstWeightId, DataSetDB.TABLEWEIGHT_ROW_WEIGHT, weight);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                //Log.e("INSERT WEIGHT", "error insert weight");
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
                //Log.e("INSERT WEIGHT", "error insert weight");
                return false;
            }
        }
    }
    public static boolean insertIntoDBFromFiles(){

        List<DataItem> dataItemList = DataReaderWriter.readFileData(engine.mContext);
        //check if list is empty if not clear Table and insert new data
        if(dataItemList.isEmpty()){
            //Log.e("NODATAITEM", "There where no items to read");
        }
        List<Weight> weightList = DataReaderWriter.readFileWeight(engine.mContext);
        if(weightList.isEmpty()){
            //Log.e("NOWEIGHTDATA", "There where no weight to read");
        }
        User user = DataReaderWriter.readFileUser(engine.mContext);
        if(user == null){
            //Log.e("NOUSER", "There where no user to read");
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
            //Log.e("INSERT WEIGHT", "error insert weight");
            return false;
        }
    }
    public static void initGraphFromDB(){

        //how many days (items) will be shown (xAxis Labels) 7 = one week
        int graphDays = 7;
        //List for last graphDays dates
        List<YearMonthDay> lastDaysList = new ArrayList<>();
        lastDaysList = getLastdays(graphDays);
        //list for all weights of the last graphDays
        List<Float> weightList = new ArrayList<>();
        //List for all call used of the last graphDays
        List<Float> calList = new ArrayList<>();
        //List for xAxisLabel
        List<String> xAxisLabelList = new ArrayList<>();

        for (YearMonthDay i : lastDaysList){
            xAxisLabelList.add(i.toString());
            calList.add(mDB.calculateSumByDate(2023, 6, 16, DataSetDB.DB_TABLE_NAME, DataSetDB.DB_ROW_CAL));
            weightList.add(mDB.calculateSumByDate(2023, 6, 16, DataSetDB.DB_TABLE_NAME, DataSetDB.DB_ROW_CAL));
        }
        //MainActivity.graph.setData(xAxisLabelList, weightList, calList, 2000);
    }
    public static List<YearMonthDay> getLastdays(int dayCount){
        List<YearMonthDay> yearMonthDay = new ArrayList();
        Calendar calendar = Calendar.getInstance();

        // Create a list to store the dates of the last 7 days
        List<String> last7Days = new ArrayList<>();

        // Subtract dayCount days from the current date
        calendar.add(Calendar.DAY_OF_YEAR, -dayCount);

        //iterate through days and insert new value into retun object
        for (int i = 0; i <= dayCount; i++) {
            yearMonthDay.add(new YearMonthDay(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)));
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            //Log.i("GETLASTDAYS", yearMonthDay.get(i).toString());
        }
        return yearMonthDay;
    }
    public static int calcCalNeed(User user) {
       return calcCalNeed(user.ismIsMan(), user.getUserHeight(), user.getUserWeight(), user.getUserAge(), user.getUserActivity());
    }
    public static List<DataItem> getDataItemNewerThanSorted(int dayCount){
        //retrive all items sorted by date time
        //add to list items untill date is reached
        //break loop
        List<DataItem> returnDataItemList = new ArrayList<>();
        Cursor cursor = mDB.selectAll();
        if(cursor.getCount() < 1){
            return null;
        }
        cursor.moveToFirst();
        int dayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int monthOfYear = Calendar.getInstance().get(Calendar.MONTH);
        int year = Calendar.getInstance().get(Calendar.YEAR);
        //day is smaller then day the we also have to go a month back
        if((dayOfMonth - dayCount) < dayCount){
            //get all values from current month
            while(monthOfYear == cursor.getInt(4)){
                returnDataItemList.add(new DataItem(cursor.getInt(0), cursor.getString(1), cursor.getInt(2),
                        cursor.getInt(3), cursor.getInt(4), cursor.getInt(5), cursor.getInt(6),
                        cursor.getInt(7)));
                if(!cursor.moveToNext()){
                    break;
                }
            }
            if(monthOfYear == 0){
                //day is smaller then daycount and we are in jan last year
                //dec has always 31days calc how much days we need from dec
                int daysNeededInDec = 31 + (dayOfMonth - dayCount);
                while(daysNeededInDec < cursor.getInt(5)){
                    returnDataItemList.add(new DataItem(cursor.getInt(0), cursor.getString(1), cursor.getInt(2),
                            cursor.getInt(3), cursor.getInt(4), cursor.getInt(5), cursor.getInt(6),
                            cursor.getInt(7)));
                    if(!cursor.moveToNext()){
                        break;
                    }
                }
                return returnDataItemList;
            }
            //we are not in jan so we need to get how much days the month has
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.MONTH, monthOfYear - 1);
            int numberOfDaysInLastMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            int daysNeeded = numberOfDaysInLastMonth + (dayOfMonth - dayCount);
            while(daysNeeded < cursor.getInt(5)){
                returnDataItemList.add(new DataItem(cursor.getInt(0), cursor.getString(1), cursor.getInt(2),
                        cursor.getInt(3), cursor.getInt(4), cursor.getInt(5), cursor.getInt(6),
                        cursor.getInt(7)));
                if(!cursor.moveToNext()){
                    break;
                }
            }
            return returnDataItemList;
        }
        //day of month is > daycount
        while((dayOfMonth - dayCount) <= cursor.getInt(5)){
            returnDataItemList.add(new DataItem(cursor.getInt(0), cursor.getString(1), cursor.getInt(2),
                    cursor.getInt(3), cursor.getInt(4), cursor.getInt(5), cursor.getInt(6),
                    cursor.getInt(7)));
            if(!cursor.moveToNext()){
                break;
            }
        }


        return returnDataItemList;
    }
    public static int interpolateColor(int startColor, int endColor, float minValue, float maxValue, float value) {
        float trueValue = value;
        if(value > maxValue){
            trueValue = maxValue;
        } else if( value < minValue) {
            trueValue = minValue;
        }
        float ratio = (trueValue - minValue) / (maxValue - minValue);
        int red = (int) (Color.red(startColor) * (1 - ratio) + Color.red(endColor) * ratio);
        int green = (int) (Color.green(startColor) * (1 - ratio) + Color.green(endColor) * ratio);
        int blue = (int) (Color.blue(startColor) * (1 - ratio) + Color.blue(endColor) * ratio);
        return Color.rgb(red, green, blue);
    }
    public static float getLastWeight() {
        Cursor cursor = mDB.getAllWeightOrderdByDate();
        cursor.moveToFirst();

        return cursor.getFloat(DataSetDB.WEIGHTTABELWEIGHT);
    }
    public static void getTextSize(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float screenWidth = displayMetrics.widthPixels;
        float screenHeigth = displayMetrics.heightPixels;

        // Calculate the desired text size based on the screen width
        float scalingFactor;
        if(screenWidth>screenHeigth) {
            scalingFactor = screenWidth / 720f;
        } else {
            scalingFactor = screenHeigth / 720f;
        }
        /*
        TEXTSIZEBIG = 24f * scalingFactor;
        TEXTSIZENORMAL = 16f * scalingFactor;
        TEXTSIZESMALL = 12f * scalingFactor;
        TEXTSIZETINY = 8f * scalingFactor;
        */


    }
    public static List<Weight> profileAdviceWeightNumbers(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 7);
        List <Weight> returnList = new ArrayList<>();
        Cursor cursor = mDB.getAllWeightOrderdByDate();
        cursor.moveToFirst();
        returnList.add(cursorToWeight(cursor));
        cursor.moveToLast();
        returnList.add(cursorToWeight(cursor));
        Float weight7daysAgo  = mDB.calculateAvgByDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), DataSetDB.DB_TABLE_NAME_WEIGHT, DataSetDB.TABLEWEIGHT_ROW_WEIGHT);
        returnList.add(new Weight(weight7daysAgo, calendar));


        return returnList;
    }
    private static Weight cursorToWeight(Cursor cursor) {
        try{
            return new Weight(cursor.getInt(0), cursor.getFloat(1), cursor.getInt(2),
                    cursor.getInt(3), cursor.getInt(4), cursor.getInt(5), cursor.getInt(6));
        } catch(Exception e){
            //Log.e("CURSORTOWEIGHT", "Wrong Cursor, empty data");
            return null;
        }
    }
    public static int dayComparison(Calendar calendar1, Calendar calendar2){
        long timeInMillis1 = calendar1.getTimeInMillis();
        long timeInMillis2 = calendar2.getTimeInMillis();
        // Calculate the difference in milliseconds
        long differenceInMillis = Math.abs(timeInMillis2 - timeInMillis1);

        // Convert milliseconds to days
        long daysDifference = differenceInMillis / (24 * 60 * 60 * 1000);
        return  Math.abs((int)daysDifference);
    }
    public static int dayComparison(int year1, int month1, int day1, int year2, int month2, int day2){
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar1.set(year1, month1, day1);
        calendar2.set(year2, month2, day2);
        return dayComparison(calendar1, calendar2);
    }
    public static String calendarToDatetoString(Calendar calendar){
        return yearMonthDaytoString(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
    }
    public static String yearMonthDaytoString(int year, int month, int day) {
        return (year + "/" + month + "/" + day);
    }
    public static String floatToString(float value){
        DecimalFormat decimalFormat = new DecimalFormat("#.1");
        return decimalFormat.format(value);
    }
}

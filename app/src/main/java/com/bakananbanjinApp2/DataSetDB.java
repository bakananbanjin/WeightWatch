package com.bakananbanjinApp2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DataSetDB {
    public static final int WEIGHTTABELID = 0;
    public static final int WEIGHTTABELWEIGHT = 1;
    public static final int WEIGHTTABELYEAR = 2;
    public static final int WEIGHTTABELMONTH = 3;
    public static final int WEIGHTTABELDAY= 4;
    public static final int WEIGHTTABELHOUR = 5;
    public static final int WEIGHTTABELMIN= 6;

    private static final String DATABASE_NAME = "Data.db";
    private static final int DATABASE_VERSION = 1;
    public static final String DB_TABLE_NAME_WEIGHT = "WEIGHT";
    public static final String TABLEWEIGHT_ROW_WEIGHT = "weight";
    public static final int TABELWEIGHT_ROW_SIZE = 7;
    public static final String DB_TABLE_NAME = "CALINTAKE";
    public static final String DB_ROW_ID = "id";
    public static final String DB_ROW_ITEM = "item";
    public static final String DB_ROW_CAL = "cal";
    public static final String DB_ROW_YEAR = "year";
    public static final String DB_ROW_MONTH = "month";
    public static final String DB_ROW_DAY = "day";
    public static final String DB_ROW_HOUR = "hour";
    public static final String DB_ROW_MIN = "min";
    public static final int TABEL_ROW_SIZE = 8;
    private Context mContext;
    private DataSetDBHelper mDBHelper;
    private SQLiteDatabase mDB;

    public DataSetDB(Context context) {
        mContext = context;
        mDBHelper = new DataSetDBHelper(context);
        mDB = mDBHelper.getWritableDatabase();
        mDB.execSQL("CREATE TABLE IF NOT EXISTS " + DB_TABLE_NAME + " ("
                + DB_ROW_ID + " integer primary key autoincrement not null, "
                + DB_ROW_ITEM + " text not null, "
                + DB_ROW_CAL + " integer not null, "
                + DB_ROW_YEAR + " integer not null, "
                + DB_ROW_MONTH + " integer not null, "
                + DB_ROW_DAY + " integer not null, "
                + DB_ROW_HOUR + " integer not null, "
                + DB_ROW_MIN + " integer not null);");
        mDB.execSQL("CREATE TABLE IF NOT EXISTS " + DB_TABLE_NAME_WEIGHT + " ("
                + DB_ROW_ID + " integer primary key autoincrement not null, "
                + TABLEWEIGHT_ROW_WEIGHT + " float not null, "
                + DB_ROW_YEAR + " integer not null, "
                + DB_ROW_MONTH + " integer not null, "
                + DB_ROW_DAY + " integer not null, "
                + DB_ROW_HOUR + " integer not null, "
                + DB_ROW_MIN + " integer not null);");

    }

    public void createTables() {
        mDB.execSQL("CREATE TABLE IF NOT EXISTS " + DB_TABLE_NAME + " ("
                + DB_ROW_ID + " integer primary key autoincrement not null, "
                + DB_ROW_ITEM + " text not null, "
                + DB_ROW_CAL + " integer not null, "
                + DB_ROW_YEAR + " integer not null, "
                + DB_ROW_MONTH + " integer not null, "
                + DB_ROW_DAY + " integer not null, "
                + DB_ROW_HOUR + " integer not null, "
                + DB_ROW_MIN + " integer not null);");
        mDB.execSQL("CREATE TABLE IF NOT EXISTS " + DB_TABLE_NAME_WEIGHT + " ("
                + DB_ROW_ID + " integer primary key autoincrement not null, "
                + TABLEWEIGHT_ROW_WEIGHT + " float not null, "
                + DB_ROW_YEAR + " integer not null, "
                + DB_ROW_MONTH + " integer not null, "
                + DB_ROW_DAY + " integer not null, "
                + DB_ROW_HOUR + " integer not null, "
                + DB_ROW_MIN + " integer not null);");
    }

    public void close() {
        mDBHelper.close();
    }

    public void insert(DataItem dataItem) {
        if (!mDB.isOpen()) {
            mDB = mDBHelper.getWritableDatabase();
        }
        createTables();
        String insertquery = "INSERT INTO " + DB_TABLE_NAME + " ("
                + DB_ROW_ITEM + ", " + DB_ROW_CAL + ", " + DB_ROW_YEAR + ", " + DB_ROW_MONTH + ", " + DB_ROW_DAY + ", "
                + DB_ROW_HOUR + ", " + DB_ROW_MIN + ") "
                + "VALUES ("
                + "'" + dataItem.getmItemName() + "'," + dataItem.getmCal() + ", " + dataItem.getYear() + ", "
                + dataItem.getMonth() + ", " + dataItem.getDay() + ", " + dataItem.getHour() + ", " + dataItem.getMin()
                + ");";
        Log.i("INSERT INTO DB", insertquery);
        mDB.execSQL(insertquery);
    }

    public void insertList(List<DataItem> dataItems) {
        for (DataItem i : dataItems) {
            insert(i);
        }
    }

    public Cursor selectAll() {
        if (!mDB.isOpen()) {
            mDB = mDBHelper.getWritableDatabase();
        }
        String orderBy = DB_ROW_YEAR + " DESC," + DB_ROW_MONTH + " DESC," + DB_ROW_DAY + " DESC,"
                + DB_ROW_HOUR + " DESC," + DB_ROW_MIN + " DESC";
        Cursor cursor = mDB.query(DB_TABLE_NAME, null, null, null, null, null, orderBy);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor selectByDate(int year, int month, int day) {
        if (!mDB.isOpen()) {
            mDB = mDBHelper.getWritableDatabase();
        }
        String selectByDatequery = "SELECT *  from " + DB_TABLE_NAME + " WHERE "
                + DB_ROW_YEAR + " = " + year + " AND "
                + DB_ROW_MONTH + " = " + month + " AND "
                + DB_ROW_DAY + " = " + day + ";";
        return mDB.rawQuery(selectByDatequery, null);
    }

    public List<DataItem> selectByDateDataItem(int year, int month, int day) {
        if (!mDB.isOpen()) {
            mDB = mDBHelper.getWritableDatabase();
        }
        List<DataItem> tempList = new ArrayList<DataItem>();
        String selectByDatequery = "SELECT *  from " + DB_TABLE_NAME + " WHERE "
                + DB_ROW_YEAR + " = " + year + " AND "
                + DB_ROW_MONTH + " = " + month + " AND "
                + DB_ROW_DAY + " = " + day + ";";
        Cursor cursor = mDB.rawQuery(selectByDatequery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            tempList.add(new DataItem(cursor.getInt(0), cursor.getString(1), cursor.getInt(2),
                    cursor.getInt(3), cursor.getInt(4), cursor.getInt(5), cursor.getInt(6),
                    cursor.getInt(7)));
            cursor.moveToNext();
        }
        return tempList;
    }

    public Cursor SelectByDateTime(int year, int month, int day, int hours, int min) {
        if (!mDB.isOpen()) {
            mDB = mDBHelper.getWritableDatabase();
        }
        String selectByDateTimequery = "SELECT *  from " + DB_TABLE_NAME + " WHERE "
                + DB_ROW_YEAR + " = " + year + " AND "
                + DB_ROW_MONTH + " = " + month + " AND "
                + DB_ROW_DAY + " = " + day
                + DB_ROW_HOUR + " = " + hours + " AND "
                + DB_ROW_MIN + " = " + min + ";";
        Cursor cursor = mDB.rawQuery(selectByDateTimequery, null);

        return cursor;
    }

    //not implemented yet
    public boolean update() {
        if (!mDB.isOpen()) {
            mDB = mDBHelper.getWritableDatabase();
        }
        mDB.execSQL("CREATE TABLE IF NOT EXISTS " + DB_TABLE_NAME + " ("
                + DB_ROW_ID + " integer primary key autoincrement not null, "
                + DB_ROW_ITEM + " text not null, "
                + DB_ROW_CAL + " integer not null, "
                + DB_ROW_YEAR + " integer not null, "
                + DB_ROW_MONTH + " integer not null, "
                + DB_ROW_DAY + " integer not null, "
                + DB_ROW_HOUR + " integer not null, "
                + DB_ROW_MIN + " integer not null);");
        mDB.execSQL("CREATE TABLE IF NOT EXISTS " + DB_TABLE_NAME_WEIGHT + " ("
                + DB_ROW_ID + " integer primary key autoincrement not null, "
                + TABLEWEIGHT_ROW_WEIGHT + " float not null, "
                + DB_ROW_YEAR + " integer not null, "
                + DB_ROW_MONTH + " integer not null, "
                + DB_ROW_DAY + " integer not null, "
                + DB_ROW_HOUR + " integer not null, "
                + DB_ROW_MIN + " integer not null);");

        return true;
    }

    public void deleteByID(int id) {
        if (!mDB.isOpen()) {
            mDB = mDBHelper.getWritableDatabase();
        }
        mDB.delete(DB_TABLE_NAME, DB_ROW_ID + " = " + id, null);
    }

    public void deleteAll() {
        mDB.delete(DB_TABLE_NAME, null, null);
        mDB.delete(DB_TABLE_NAME_WEIGHT, null, null);

    }

    public List<DataItem> selectAllDataItem() {
        //List<DataItem> tempList = new ArrayList<>();
        List<DataItem> tempList2 = new ArrayList<>();
        Cursor cursor = this.selectAll();
        //while dosent give last Entry use for loop
        /*while (cursor.moveToNext()){
            tempList.add(new DataItem(cursor.getInt(0), cursor.getString(1), cursor.getInt(2),
                    cursor.getInt(3), cursor.getInt(4), cursor.getInt(5), cursor.getInt(6),
                    cursor.getInt(7)));
        }
        cursor.moveToFirst();*/
        for (int i = 0; i < cursor.getCount(); i++) {
            tempList2.add(new DataItem(cursor.getInt(0), cursor.getString(1), cursor.getInt(2),
                    cursor.getInt(3), cursor.getInt(4), cursor.getInt(5), cursor.getInt(6),
                    cursor.getInt(7)));
            cursor.moveToNext();
        }
        return tempList2;
    }

    //TEST METHODE TO BE DELETED
    public void drop() {
        mDB.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_NAME);
        mDB.execSQL("CREATE TABLE IF NOT EXISTS " + DB_TABLE_NAME + " ("
                + DB_ROW_ID
                + " integer primary key autoincrement not null, "
                + DB_ROW_ITEM + " text not null, "
                + DB_ROW_CAL + " integer not null, "
                + DB_ROW_YEAR + " integer not null, "
                + DB_ROW_MONTH + " integer not null, "
                + DB_ROW_DAY + " integer not null, "
                + DB_ROW_HOUR + " integer not null, "
                + DB_ROW_MIN + " integer not null);");
    }

    public Cursor selectAllWeight() {
        if (!mDB.isOpen()) {
            mDB = mDBHelper.getWritableDatabase();
        }
        String orderBy = DB_ROW_YEAR + " DESC," + DB_ROW_MONTH + " DESC," + DB_ROW_DAY + " DESC,"
                + DB_ROW_HOUR + " DESC," + DB_ROW_MIN + " DESC";
        Cursor cursor = mDB.query(DB_TABLE_NAME_WEIGHT, null, null, null, null, null, null);
        return cursor;
    }

    public List<Weight> selectAllWeightList(){
        Cursor cursor = getAllWeightOrderdByDate();
        cursor.moveToFirst();
        List<Weight> tempList2 = new ArrayList<>();
        for (int i = 0; i < cursor.getCount(); i++) {
            tempList2.add(new Weight(cursor.getInt(0), cursor.getFloat(1), cursor.getInt(2),
                    cursor.getInt(3), cursor.getInt(4), cursor.getInt(5), cursor.getInt(6)));
            cursor.moveToNext();
        }
        return tempList2;
    }
    public Cursor selectColumnFromTable(String tableName, String column){

        String query;
        query = "SELECT " + column + " FROM " + tableName;
        Cursor cursor = mDB.rawQuery(query, null);

        return cursor;
    }

    public void insertWeightList(List<Weight> weightList) {
        for(Weight i : weightList){
            insertWeight(i);
        }
    }
    public void insertWeight(Weight weight){
        if(!mDB.isOpen()){
            mDB = mDBHelper.getWritableDatabase();
        }
        createTables();
        Calendar c = weight.getCalendar();
        String insertquery = "INSERT INTO " + DB_TABLE_NAME_WEIGHT + " ("
                + TABLEWEIGHT_ROW_WEIGHT + ", "
                + DB_ROW_YEAR + ", "
                + DB_ROW_MONTH + ", "
                + DB_ROW_DAY + ", "
                + DB_ROW_HOUR + ", "
                + DB_ROW_MIN + " ) VALUES ("
                + weight.getWeight() + ", "
                + c.get(Calendar.YEAR) + ", "
                + c.get(Calendar.MONTH) + ", "
                + c.get(Calendar.DAY_OF_MONTH) + ", "
                + c.get(Calendar.HOUR_OF_DAY) + ", "
                + c.get(Calendar.MINUTE)
                +");";

        Log.i("INSERT INTO DB", insertquery);
        mDB.execSQL(insertquery);
    }

    public void updatetWeightData(int id, float weight, int year, int month, int day, int hour, int minute) {
        String updateQuery = "UPDATE " + DB_TABLE_NAME_WEIGHT + " SET "
                + TABLEWEIGHT_ROW_WEIGHT + " = " + weight + ", "
                + DB_ROW_YEAR + " = " + year + ", "
                + DB_ROW_MONTH + " = " + month + ", "
                + DB_ROW_DAY + " = " + day + ", "
                + DB_ROW_HOUR + " = " + hour + ", "
                + DB_ROW_MIN + " = " + minute
                + " WHERE " + DB_ROW_ID + " = " + id + ";";
        mDB.execSQL(updateQuery);
    }

    //innerclass to create Database
    private class DataSetDBHelper extends SQLiteOpenHelper {

        public DataSetDBHelper(@Nullable Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            String createTableQuery = "create table "
                    + DB_TABLE_NAME + " ("
                    + DB_ROW_ID
                    + " integer primary key autoincrement not null, "
                    + DB_ROW_ITEM + " text not null, "
                    + DB_ROW_CAL + " integer not null, "
                    + DB_ROW_YEAR + " integer not null, "
                    + DB_ROW_MONTH + " integer not null, "
                    + DB_ROW_DAY + " integer not null, "
                    + DB_ROW_HOUR + " integer not null, "
                    + DB_ROW_MIN + " integer not null);";
            sqLiteDatabase.execSQL(createTableQuery);
        }

        //function is not needed only when change sturctur of DB. we just call on create
        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_NAME);
            onCreate(sqLiteDatabase);
        }
    }
    public void updateItem (DataItem dataItem){
        String updateQuery = "UPDATE " + DB_TABLE_NAME + " SET "
                + DB_ROW_ITEM + " = '" + dataItem.getmItemName() + "', "
                + DB_ROW_CAL + " = " + dataItem.getmCal() + ", "
                + DB_ROW_YEAR + " = " + dataItem.getYear() + ", "
                + DB_ROW_MONTH + " = " + dataItem.getMonth() + ", "
                + DB_ROW_DAY + " = " + dataItem.getDay() + ", "
                + DB_ROW_HOUR + " = " + dataItem.getHour() + ", "
                + DB_ROW_MIN + " = " + dataItem.getMin()
                + " WHERE " + DB_ROW_ID + " = " + dataItem.getId() + ";";
        Log.i("UPDATE INTO DB", updateQuery);
        mDB.execSQL(updateQuery);

    }
    public void updateDataWeight(int weightId, String tableColumn, float weight) {

        String updateQuery = "UPDATE " + DB_TABLE_NAME_WEIGHT + " SET "
                + TABLEWEIGHT_ROW_WEIGHT + " = " + weight
                + " WHERE " + DB_ROW_ID + " = " + weightId + ";";

        Log.i("UPDATE INTO DB", updateQuery);

        mDB.execSQL(updateQuery);
    }
    public void insertWeightData(float weight, int year, int month, int day, int hour, int minute) {
        ContentValues values = new ContentValues();
        values.put(TABLEWEIGHT_ROW_WEIGHT, weight);
        values.put(DB_ROW_YEAR, year);
        values.put(DB_ROW_MONTH, month);
        values.put(DB_ROW_DAY, day);
        values.put(DB_ROW_HOUR, hour);
        values.put(DB_ROW_MIN, minute);

        mDB.insert(DB_TABLE_NAME_WEIGHT, null, values);
    }
    public Cursor getAllWeightOrderdByDate(){
        String orderBy = DB_ROW_YEAR + " DESC," + DB_ROW_MONTH + " DESC," + DB_ROW_DAY + " DESC,"
                + DB_ROW_HOUR + " DESC," + DB_ROW_MIN + " DESC";

        return mDB.query(DB_TABLE_NAME_WEIGHT, null, null, null, null, null, orderBy);
    }

    public float calculateSumByDate(int year, int month, int day, String tableName, String sumFiled) {
        if (!mDB.isOpen()) {
            mDB = mDBHelper.getWritableDatabase();
        }
        String query = "SELECT SUM(" + sumFiled + ") FROM " + tableName + " WHERE year = ? AND month = ? AND day = ?";
        String[] selectionArgs = {String.valueOf(year), String.valueOf(month), String.valueOf(day)};
        Cursor cursor = mDB.rawQuery(query, selectionArgs);

        float sum = 0;
        if(true){
            if (cursor.moveToFirst()) {
                sum = cursor.getFloat(0);
            }
        }
        return sum;
    }
    public float calculateAvgByDate(int year, int month, int day, String tableName, String sumFiled) {
        if (!mDB.isOpen()) {
            mDB = mDBHelper.getWritableDatabase();
        }
        String query = "SELECT AVG(" + sumFiled + ") FROM " + tableName + " WHERE year = ? AND month = ? AND day = ?";
        String[] selectionArgs = {String.valueOf(year), String.valueOf(month), String.valueOf(day)};
        Cursor cursor = mDB.rawQuery(query, selectionArgs);

        float sum = 0;
        if(true){
            if (cursor.moveToFirst()) {
                sum = cursor.getFloat(0);
            }
        }
        return sum;
    }


}

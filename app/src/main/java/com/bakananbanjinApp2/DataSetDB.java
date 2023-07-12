package com.bakananbanjinApp2;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DataSetDB  {
    private static final String DATABASE_NAME = "Data.db";
    private static final int DATABASE_VERSION = 1;

    public static final String DB_TABLE_NAME = "CALINTAKE";

    public static final String DB_ROW_ID = "id";
    public static final String DB_ROW_ITEM = "item";
    public static final String DB_ROW_CAL = "cal";
    public static final String DB_ROW_YEAR= "year";
    public static final String DB_ROW_MONTH = "month";
    public static final String DB_ROW_DAY = "day";
    public static final String DB_ROW_HOUR= "hour";
    public static final String DB_ROW_MIN = "min";
    public static final String DB_ROW_ACTIVITY = "activity";
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
    }
    public void close(){
        mDBHelper.close();
    }
    public void insert(DataItem dataItem){
        String insertquery = "INSERT INTO " + DB_TABLE_NAME + " ("
                + DB_ROW_ITEM + ", " + DB_ROW_CAL + ", " + DB_ROW_YEAR + ", " + DB_ROW_MONTH+ ", " + DB_ROW_DAY + ", "
                + DB_ROW_HOUR + ", " + DB_ROW_MIN + ") "
                + "VALUES ("
                + "'" + dataItem.getmItemName() + "'," +  dataItem.getmCal() + ", " +  dataItem.getYear() + ", "
                +  dataItem.getMonth() + ", " +  dataItem.getDay() + ", " +  dataItem.getHour() + ", " +  dataItem.getMin()
                +");";
        Log.i("INSERT INTO DB", insertquery);
        mDB.execSQL(insertquery);
    }
    public void insertList(List<DataItem> dataItems){
        for(DataItem i : dataItems) {
            insert(i);
        }
    }
    public Cursor selectAll(){
        String orderBy = DB_ROW_YEAR + " DESC," + DB_ROW_MONTH + " DESC," + DB_ROW_DAY + " DESC,"
                + DB_ROW_HOUR + " DESC," + DB_ROW_MIN + " DESC";
        Cursor cursor = mDB.query(DB_TABLE_NAME, null, null, null, null, null, orderBy);
        if (cursor != null) {
                cursor.moveToFirst();
        }
        return cursor;
    }
    public Cursor selectByDate(int year, int month, int day){
        String selectByDatequery = "SELECT *  from " + DB_TABLE_NAME + " WHERE "
                + DB_ROW_YEAR + " = " + year + " AND "
                + DB_ROW_MONTH + " = " + month + " AND "
                + DB_ROW_DAY + " = " + day + ";";
        return mDB.rawQuery(selectByDatequery, null);
    }
    public List<DataItem> selectByDateDataItem(int year, int month, int day){
        List<DataItem> tempList = new ArrayList<DataItem>();
        String selectByDatequery = "SELECT *  from " + DB_TABLE_NAME + " WHERE "
                + DB_ROW_YEAR + " = " + year + " AND "
                + DB_ROW_MONTH + " = " + month + " AND "
                + DB_ROW_DAY + " = " + day + ";";
        Cursor cursor = mDB.rawQuery(selectByDatequery, null);
        cursor.moveToFirst();
        for(int i =  0; i < cursor.getCount(); i++){
            tempList.add(new DataItem(cursor.getInt(0), cursor.getString(1), cursor.getInt(2),
                    cursor.getInt(3), cursor.getInt(4), cursor.getInt(5), cursor.getInt(6),
                    cursor.getInt(7)));
            cursor.moveToNext();
        }
        return tempList;
    }
    public Cursor SelectByDateTime(int year, int month, int day, int hours, int min){
        String selectByDateTimequery = "SELECT *  from " + DB_TABLE_NAME + " WHERE "
                + DB_ROW_YEAR + " = " + year + " AND "
                + DB_ROW_MONTH + " = " + month + " AND "
                + DB_ROW_DAY + " = " + day
                + DB_ROW_HOUR + " = " + hours + " AND "
                + DB_ROW_MIN + " = " + min + ";";
        return mDB.rawQuery(selectByDateTimequery, null);
    }
    //not implemented yet
    public boolean update(){
        return false;
    }
    public void deleteByID(int id){
        mDB.delete(DB_TABLE_NAME, DB_ROW_ID + " = " + id, null);
    }
    public void deleteAll(){
        mDB.delete(DB_TABLE_NAME, null, null);
    }
    public List<DataItem> selectAllDataItem(){
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
        for(int i =  0; i < cursor.getCount(); i++){
            tempList2.add(new DataItem(cursor.getInt(0), cursor.getString(1), cursor.getInt(2),
                    cursor.getInt(3), cursor.getInt(4), cursor.getInt(5), cursor.getInt(6),
                    cursor.getInt(7)));
            cursor.moveToNext();
        }
        return tempList2;
    }
    //TEST METHODE TO BE DELETED
    public void drop(){
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

}

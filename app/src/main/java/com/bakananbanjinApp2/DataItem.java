package com.bakananbanjinApp2;


import android.util.Log;

import java.util.Calendar;
import java.util.TimeZone;

//class for a DataItem
public class DataItem {
    private String mItemName;
    private int mId;
    private int mCal;
    private Calendar mCalendar;

    public DataItem(String itemName, int cal) {
        this.mItemName = itemName;
        this.mCal = cal;
        mCalendar = Calendar.getInstance();
    }
    public  DataItem(String itemName, int cal, int month, int day, int hour, int min) {
        this.mItemName = itemName;
        this.mCal = cal;
        mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.getInstance().get(Calendar.YEAR), month, day, hour, min, 0 );
    }
    public  DataItem(String itemName, int cal, int year, int month, int day, int hour, int min) {
        this.mItemName = itemName;
        this.mCal = cal;
        mCalendar = Calendar.getInstance();
        mCalendar.set(year, month, day, hour, min, 0 );
    }
    public  DataItem(int id, String itemName, int cal, int year, int month, int day, int hour, int min) {
        this.mItemName = itemName;
        this.mCal = cal;
        this.mId = id;
        mCalendar = Calendar.getInstance();
        mCalendar.set(year, month, day, hour, min, 0 );
    }

    public String getmItemName() {
        return mItemName;
    }

    public void setmItemName(String mItemName) {
        this.mItemName = mItemName;
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public int getmCal() {
        return mCal;
    }
    public void setmCal(int mCal) {
        this.mCal = mCal;
    }
    public Calendar getmCalendar() {
        return mCalendar;
    }
    public int getYear(){
        return mCalendar.get(Calendar.YEAR);
    }
    public int getMonth(){
        return mCalendar.get(Calendar.MONTH);
    }
    public int getDay(){
        return mCalendar.get(Calendar.DATE);
    }
    public int getHour(){
        return mCalendar.get(Calendar.HOUR_OF_DAY);
    }
    public int getMin(){
        return mCalendar.get(Calendar.MINUTE);
    }
    public void setmCalendar(Calendar mCalendar) {
        this.mCalendar = mCalendar;
    }
    public void setDate(int year, int month, int day){
        mCalendar.set(year, month, day);
    }
    public void setTime(int hour, int minute){
        mCalendar.set(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DATE), hour, minute, 0);
    }
    public void setTimeDate(int year, int month, int day, int hour, int min){
        mCalendar.set(year, month, day, hour, min, 0);
    }
    @Override
    public String toString() {
        String output = "DataItem{" +
                "mItemName='" + mItemName + '\'' +
                ", mId=" + mId +
                ", mCal=" + mCal +
                ", mCalendar=" + mCalendar.getTime() +
                '}';

        return output;
    }
    public String toStringtoFile(){
        String output = this.mId
                + ";" + this.mItemName
                + ";" + this.mCal
                + ";" + this.mCalendar.get(Calendar.YEAR)
                + ";" + mCalendar.get(Calendar.MONTH)
                + ";" + mCalendar.get(Calendar.DATE)
                + ";" + mCalendar.get(Calendar.HOUR_OF_DAY)
                + ";" + mCalendar.get(Calendar.MINUTE)
                + "\n";
        return output;
    }
}

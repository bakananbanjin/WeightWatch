package com.bakananbanjinApp2;

import java.util.Calendar;

public class YearMonthDay {
    public int year;
    public int month;
    public int day;

    public YearMonthDay(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }
    public YearMonthDay(Calendar calendar){
        this.year = calendar.get(Calendar.YEAR);
        this.month = calendar.get(Calendar.MONTH);
        this.day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public String toString() {
        String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        if (month >= 0 && month < monthNames.length) {
            return day +  ". " + monthNames[month];
        } else {
            return day +  ". " + month;
        }
    }
}

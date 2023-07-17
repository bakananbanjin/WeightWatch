package com.bakananbanjinApp2;

public class YearMonthDay {
    public int year;
    public int month;
    public int day;

    public YearMonthDay(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
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

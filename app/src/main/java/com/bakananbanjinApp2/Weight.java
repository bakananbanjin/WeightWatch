package com.bakananbanjinApp2;

import androidx.annotation.NonNull;

import java.util.Calendar;

public class Weight {
    private int id;
    private float weight;
    private Calendar calendar;

    public Weight(float weight, Calendar calendar) {
        this.weight = weight;
        this.calendar = calendar;
    }
    public Weight(float weight, int year, int month, int day) {
        this.weight = weight;
        this.calendar = Calendar.getInstance();
        calendar.set(year, month, day);
    }
    public Weight(int weight, int year, int month, int day) {
        this.weight = (float) weight;
        this.calendar = Calendar.getInstance();
        calendar.set(year, month, day);
    }
    public Weight(int id, float weight, int year, int month, int day, int hour, int min) {
        this.id = id;
        this.weight = weight;
        this.calendar = Calendar.getInstance();
        calendar.set(year, month, day);
    }

    public int getId() {
        return id;
    }

    public float getWeight() {
        return weight;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public String toString() {
        return (this.weight + " " + Engine.calendarToDatetoString(this.calendar));
    }
}

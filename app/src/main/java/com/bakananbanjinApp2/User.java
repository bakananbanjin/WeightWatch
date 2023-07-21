package com.bakananbanjinApp2;

import java.util.Calendar;

public class User {
    private String userName;
    private float userWeight;
    private int userHeight;
    private int userAge;
    private int userTargetWeight;
    private boolean mIsMan;
    private float userActivity;

    public User(String name, boolean man, int age, int height, float weight, int targetWeight, float activity) {
        this.userName = name;
        this.userWeight = weight;
        this.userHeight = height;
        this.userAge = age;
        this.userTargetWeight = targetWeight;
        this.mIsMan = man;
        this.userActivity = activity;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserHeight(int userHeight) {
        this.userHeight = userHeight;
    }

    public void setUserAge(int userAge) {
        this.userAge = userAge;
    }

    public void setUserTargetWeight(int userTargetWeight) {
        this.userTargetWeight = userTargetWeight;
    }

    public boolean ismIsMan() {
        return mIsMan;
    }

    public void setmIsMan(boolean mIsMan) {
        this.mIsMan = mIsMan;
    }

    public float getUserActivity() {
        return userActivity;
    }

    public void setUserActivity(float userActivity) {
        this.userActivity = userActivity;
    }

    public float getUserWeight() {
        return userWeight;
    }
    public void setUserWeight(float userWeight) {
        this.userWeight = userWeight;
    }

    public String getUserName() {
        return userName;
    }

    public int getUserHeight() {
        return userHeight;
    }

    public int getUserAge() {
        return userAge;
    }

    public int getUserTargetWeight() {
        return userTargetWeight;
    }

    public void saveUserToPreference(){
        Engine.createUserPref(userName, mIsMan, userAge, userAge, userWeight, userTargetWeight, userActivity);
    }
    public String toStringtoFile(){
        String output = this.userName + ";"
                + this.mIsMan + ";"
                + this.userAge + ";"
                + this.userHeight + ";"
                + this.userWeight + ";"
                + this.userTargetWeight + ";"
                + this.userActivity
                + "\n";
        return output;
    }

    public void updateUser(String name, boolean man, int age, int height, float weight, int targetWeight, float activity) {
        this.userName = name;
        this.userWeight = weight;
        this.userHeight = height;
        this.userAge = age;
        this.userTargetWeight = targetWeight;
        this.mIsMan = man;
        this.userActivity = activity;

    }
}

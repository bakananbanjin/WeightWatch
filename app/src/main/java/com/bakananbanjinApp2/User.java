package com.bakananbanjinApp2;

public class User {
    private String userName;
    private int userWeight;
    private int userHeight;
    private int userAge;
    private int userTargetWeight;
    private boolean mIsMan;
    private float userActivity;

    public User(String userName, int userWeight, int userHeight, int userAge, int userTargetWeight, boolean isMan) {
        new User(userName, userWeight, userHeight, userAge, userTargetWeight, isMan, 1.2f);
    }

    public User(String userName, int userWeight, int userHeight, int userAge, int userTargetWeight, boolean isMan, float userActivity) {
        this.userName = userName;
        this.userWeight = userWeight;
        this.userHeight = userHeight;
        this.userAge = userAge;
        this.userTargetWeight = userTargetWeight;
        this.mIsMan = isMan;
        this.userActivity = userActivity;
    }

    public float getUserActivity() {
        return userActivity;
    }

    public void setUserActivity(float userActivity) {
        this.userActivity = userActivity;
    }

    public int getUserWeight() {
        return userWeight;
    }
    public void setUserWeight(int userWeight) {
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
        Engine.createUserPref(userName, mIsMan, userHeight, userWeight, userAge, userTargetWeight, userActivity);
    }
}

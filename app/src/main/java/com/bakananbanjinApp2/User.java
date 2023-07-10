package com.bakananbanjinApp2;

public class User {
    private String userName;
    private int userWeight;
    private int userHeight;
    private int userAge;
    private int userTargetWeight;


    public User(String userName, int userWeight, int userHeight, int userAge, int userTargetWeight) {
        this.userName = userName;
        this.userWeight = userWeight;
        this.userHeight = userHeight;
        this.userAge = userAge;
        this.userTargetWeight = userTargetWeight;
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
}

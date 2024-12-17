package com.example.smartposture.data.model;

public class ActivityTrainee {
    private int activity_id;
    private int trainee_id;
    private String trainee_username;
    private String status;

    public ActivityTrainee(int activity_id, int trainee_id, String trainee_username, String status) {
        this.activity_id = activity_id;
        this.trainee_id = trainee_id;
        this.trainee_username = trainee_username;
        this.status = status;
    }

    public int getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(int activity_id) {
        this.activity_id = activity_id;
    }

    public int getTrainee_id() {
        return trainee_id;
    }

    public void setTrainee_id(int trainee_id) {
        this.trainee_id = trainee_id;
    }

    public String getTrainee_username() {
        return trainee_username;
    }

    public void setTrainee_username(String trainee_username) {
        this.trainee_username = trainee_username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

package com.example.smartposture.data.response;

import java.util.ArrayList;
import java.util.List;

public class MonthlyWorkoutsResponse {
    private String message;
    private int status;
    private ArrayList<ArrayList<Integer>> workouts;

    public MonthlyWorkoutsResponse(String message, int status, ArrayList<ArrayList<Integer>> workouts) {
        this.message = message;
        this.status = status;
        this.workouts = workouts;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ArrayList<ArrayList<Integer>> getWorkouts() {
        return workouts;
    }

    public void setWorkouts(ArrayList<ArrayList<Integer>> workouts) {
        this.workouts = workouts;
    }
}

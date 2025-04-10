package com.example.smartposture.data.response;

import com.example.smartposture.data.model.Workout;

import java.util.List;

public class WorkoutResponse {
    private String message;
    private List<Workout> workouts;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Workout> getWorkouts() {
        return workouts;
    }


}

package com.example.smartposture.data.response;

import com.example.smartposture.data.model.WorkoutDetail;

public class WorkoutDetailResponse {
    private String message;
    private WorkoutDetail workout;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public WorkoutDetail getWorkouts() {
        return workout;
    }
}

package com.example.smartposture.data.response;

import com.example.smartposture.data.model.ActivityTrainee;

import java.util.Map;

public class ActivityStatisticsResponse {
    private String message;
    private int status;
    private Map<String, ActivityTrainee> trainees;

    public ActivityStatisticsResponse(String message, int status, Map<String, ActivityTrainee> trainees) {
        this.message = message;
        this.status = status;
        this.trainees = trainees;
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

    public Map<String, ActivityTrainee> getTrainees() {
        return trainees;
    }

    public void setTrainees(Map<String, ActivityTrainee> trainees) {
        this.trainees = trainees;
    }
}

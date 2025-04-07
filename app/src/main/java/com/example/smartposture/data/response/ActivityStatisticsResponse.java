package com.example.smartposture.data.response;

import com.example.smartposture.data.model.ActivityStatistics;
import com.example.smartposture.data.model.ActivityTrainee;

import java.util.Map;

public class ActivityStatisticsResponse {
    private String message;
    private int status;
    private ActivityStatistics statistics;

    public ActivityStatisticsResponse(String message, int status, ActivityStatistics statistics) {
        this.message = message;
        this.status = status;
        this.statistics = statistics;
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

    public ActivityStatistics getStatistics() {
        return statistics;
    }

    public void setStatistics(ActivityStatistics statistics) {
        this.statistics = statistics;
    }
}

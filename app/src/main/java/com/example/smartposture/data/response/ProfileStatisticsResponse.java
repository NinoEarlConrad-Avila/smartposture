package com.example.smartposture.data.response;

import com.example.smartposture.data.model.Statistics;

public class ProfileStatisticsResponse {
    private String message;
    private int status;
    private Statistics statistics;

    public ProfileStatisticsResponse(String message, int status, Statistics statistics) {
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

    public Statistics getStatistics() {
        return statistics;
    }

    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }
}

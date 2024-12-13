package com.example.smartposture.data.response;

import com.example.smartposture.data.model.ActivityDetails;

public class ActivityDetailResponse {
    private String message;
    private int status;
    private ActivityDetails activity;

    public ActivityDetailResponse(String message, int status, ActivityDetails activity) {
        this.message = message;
        this.status = status;
        this.activity = activity;
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

    public ActivityDetails getActivity() {
        return activity;
    }

    public void setActivity(ActivityDetails activity) {
        this.activity = activity;
    }
}

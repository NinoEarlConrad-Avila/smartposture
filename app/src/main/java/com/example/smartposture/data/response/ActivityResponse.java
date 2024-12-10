package com.example.smartposture.data.response;

import com.example.smartposture.data.model.Activity;

import java.util.List;

public class ActivityResponse {
    private String message;
    private int status;
    private List<Activity> activity;

    public ActivityResponse(String message, int status, List<Activity> activity) {
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

    public List<Activity> getActivity() {
        return activity;
    }

    public void setActivity(List<Activity> activity) {
        this.activity = activity;
    }
}

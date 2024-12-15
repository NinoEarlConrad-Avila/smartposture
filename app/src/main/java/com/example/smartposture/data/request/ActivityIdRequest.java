package com.example.smartposture.data.request;

public class ActivityIdRequest {
    private int activity_id;
    private int user_id;

    public ActivityIdRequest(int activity_id, int user_id) {
        this.activity_id = activity_id;
        this.user_id = user_id;
    }
}

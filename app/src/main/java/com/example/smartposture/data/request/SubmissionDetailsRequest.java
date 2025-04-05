package com.example.smartposture.data.request;

public class SubmissionDetailsRequest {
    private int activity_id;
    private int trainee_id;

    public SubmissionDetailsRequest(int activity_id, int trainee_id) {
        this.activity_id = activity_id;
        this.trainee_id = trainee_id;
    }
}

package com.example.smartposture.data.response;

import com.example.smartposture.data.model.SubmissionDetails;

public class SubmissionDetailResponse {
    private String message;
    private int status;
    private SubmissionDetails submission;

    public SubmissionDetailResponse(String message, int status, SubmissionDetails submission) {
        this.message = message;
        this.status = status;
        this.submission = submission;
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

    public SubmissionDetails getSubmission() {
        return submission;
    }

    public void setSubmission(SubmissionDetails submission) {
        this.submission = submission;
    }
}

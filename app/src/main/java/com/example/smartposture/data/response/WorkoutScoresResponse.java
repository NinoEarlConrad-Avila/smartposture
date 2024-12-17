package com.example.smartposture.data.response;

import java.util.ArrayList;

public class WorkoutScoresResponse {
    private String message;
    private int status;
    private ArrayList<Float> scores;

    public WorkoutScoresResponse(String message, int status, ArrayList<Float> scores) {
        this.message = message;
        this.status = status;
        this.scores = scores;
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

    public ArrayList<Float> getScores() {
        return scores;
    }

    public void setScores(ArrayList<Float> scores) {
        this.scores = scores;
    }
}

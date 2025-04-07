package com.example.smartposture.data.model;

import java.util.Map;

public class ActivityStatistics {
    private int count_025;
    private int count_050;
    private int count_100;
    private int total_repetitions;
    private float avg_score;
    private int total_submitted;
    private int total_not_submitted;
    private Map<String, ActivityTrainee> trainees;
    public ActivityStatistics(int count_025, int count_050, int count_100, int total_repetitions, float avg_score, int total_submitted, int total_not_submitted, Map<String, ActivityTrainee> trainees) {
        this.count_025 = count_025;
        this.count_050 = count_050;
        this.count_100 = count_100;
        this.total_repetitions = total_repetitions;
        this.avg_score = avg_score;
        this.total_submitted = total_submitted;
        this.total_not_submitted = total_not_submitted;
        this.trainees = trainees;
    }

    public int getCount_025() {
        return count_025;
    }

    public void setCount_025(int count_025) {
        this.count_025 = count_025;
    }

    public int getCount_050() {
        return count_050;
    }

    public void setCount_050(int count_050) {
        this.count_050 = count_050;
    }

    public int getCount_100() {
        return count_100;
    }

    public void setCount_100(int count_100) {
        this.count_100 = count_100;
    }

    public int getTotal_repetitions() {
        return total_repetitions;
    }

    public void setTotal_repetitions(int total_repetitions) {
        this.total_repetitions = total_repetitions;
    }

    public float getAvg_score() {
        return avg_score;
    }

    public void setAvg_score(float avg_score) {
        this.avg_score = avg_score;
    }

    public int getTotal_submitted() {
        return total_submitted;
    }

    public void setTotal_submitted(int total_submitted) {
        this.total_submitted = total_submitted;
    }

    public int getTotal_not_submitted() {
        return total_not_submitted;
    }

    public void setTotal_not_submitted(int total_not_submitted) {
        this.total_not_submitted = total_not_submitted;
    }

    public Map<String, ActivityTrainee> getTrainees() {
        return trainees;
    }

    public void setTrainees(Map<String, ActivityTrainee> trainees) {
        this.trainees = trainees;
    }
}

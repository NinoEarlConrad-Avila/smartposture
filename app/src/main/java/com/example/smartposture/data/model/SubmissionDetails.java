package com.example.smartposture.data.model;

import java.util.List;

public class SubmissionDetails {
    private int count_025;
    private int count_050;
    private int count_100;
    private int total_repetitions;
    private float avg_score;
    private List<ActivityWorkout> workouts;

    public SubmissionDetails(int count_025, int count_050, int count_100, int total_repetitions, float avg_score, List<ActivityWorkout> workouts) {
        this.count_025 = count_025;
        this.count_050 = count_050;
        this.count_100 = count_100;
        this.total_repetitions = total_repetitions;
        this.avg_score = avg_score;
        this.workouts = workouts;
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

    public List<ActivityWorkout> getWorkouts() {
        return workouts;
    }

    public void setWorkouts(List<ActivityWorkout> workouts) {
        this.workouts = workouts;
    }
}

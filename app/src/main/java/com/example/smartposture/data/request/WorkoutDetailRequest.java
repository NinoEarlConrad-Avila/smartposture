package com.example.smartposture.data.request;

public class WorkoutDetailRequest {
    private int workout_id;

    public WorkoutDetailRequest(int workout_id) {
        this.workout_id = workout_id;
    }

    public int getWorkout_id() {
        return workout_id;
    }

    public void setWorkout_id(int workout_id) {
        this.workout_id = workout_id;
    }
}

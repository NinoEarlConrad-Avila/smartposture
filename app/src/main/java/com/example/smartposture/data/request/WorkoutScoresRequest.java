package com.example.smartposture.data.request;

public class WorkoutScoresRequest {
    private int activity_workout_id;
    private int user_id;

    public WorkoutScoresRequest(int activity_workout_id, int user_id) {
        this.activity_workout_id = activity_workout_id;
        this.user_id = user_id;
    }
}

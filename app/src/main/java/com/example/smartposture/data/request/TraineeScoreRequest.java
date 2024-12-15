package com.example.smartposture.data.request;

import java.util.ArrayList;

public class TraineeScoreRequest {
    private int activity_workout_id;
    private int user_id;
    private ArrayList<Float> scores;

    public TraineeScoreRequest(int activity_workout_id, int user_id, ArrayList<Float> scores) {
        this.activity_workout_id = activity_workout_id;
        this.user_id = user_id;
        this.scores = scores;
    }
}

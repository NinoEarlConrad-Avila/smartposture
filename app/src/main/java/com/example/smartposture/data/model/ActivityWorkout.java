package com.example.smartposture.data.model;

public class ActivityWorkout {
    private int activity_workout_id;
    private int workout_id;
    private String workout_name;
    private String workout_description;
    private String workout_img_path;
    private int repetition;

    public ActivityWorkout(int activity_workout_id, int workout_id, String workout_name, String workout_description, String workout_img_path, int repetition) {
        this.activity_workout_id = activity_workout_id;
        this.workout_id = workout_id;
        this.workout_name = workout_name;
        this.workout_description = workout_description;
        this.workout_img_path = workout_img_path;
        this.repetition = repetition;
    }

    public int getActivity_workout_id() {
        return activity_workout_id;
    }

    public void setActivity_workout_id(int activity_workout_id) {
        this.activity_workout_id = activity_workout_id;
    }

    public int getWorkout_id() {
        return workout_id;
    }

    public void setWorkout_id(int workout_id) {
        this.workout_id = workout_id;
    }

    public String getWorkout_name() {
        return workout_name;
    }

    public void setWorkout_name(String workout_name) {
        this.workout_name = workout_name;
    }

    public String getWorkout_description() {
        return workout_description;
    }

    public void setWorkout_description(String workout_description) {
        this.workout_description = workout_description;
    }

    public String getWorkout_img_path() {
        return workout_img_path;
    }

    public void setWorkout_img_path(String workout_img_path) {
        this.workout_img_path = workout_img_path;
    }

    public int getRepetition() {
        return repetition;
    }

    public void setRepetition(int repetition) {
        this.repetition = repetition;
    }
}

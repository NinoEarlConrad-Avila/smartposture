package com.example.smartposture.data.request;

public class CreateActivityRequest {
    private int room_id;
    private String title;
    private String description;
    private String end_date;
    private String end_time;
    private int[] workouts;
    private int[] repetitions;

    public CreateActivityRequest(int room_id, String title, String description, String end_date, String end_time, int[] workouts, int[] repetitions) {
        this.room_id = room_id;
        this.title = title;
        this.description = description;
        this.end_date = end_date;
        this.end_time = end_time;
        this.workouts = workouts;
        this.repetitions = repetitions;
    }
}

package com.example.smartposture.data.model;

import java.util.List;

public class ActivityDetails {
    private String title;
    private String description;
    private String end_date;
    private String end_time;
    private String status;
    private List<ActivityWorkout> workouts;

    public ActivityDetails(String title, String description, String end_date, String end_time, String status, List<ActivityWorkout> workouts) {
        this.title = title;
        this.description = description;
        this.end_date = end_date;
        this.end_time = end_time;
        this.status = status;
        this.workouts = workouts;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ActivityWorkout> getWorkouts() {
        return workouts;
    }

    public void setWorkouts(List<ActivityWorkout> workouts) {
        this.workouts = workouts;
    }
}

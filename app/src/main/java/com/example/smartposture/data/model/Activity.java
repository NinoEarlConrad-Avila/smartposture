package com.example.smartposture.data.model;

public class Activity {
    private int activity_id;
    private String title;
    private String description;
    private String end_date;
    private String end_time;
    private String status;

    public Activity(int activity_id, String title, String description, String end_date, String end_time) {
        this.activity_id = activity_id;
        this.title = title;
        this.description = description;
        this.end_date = end_date;
        this.end_time = end_time;
    }

    public Activity(int activity_id, String title, String description, String end_date, String end_time, String status) {
        this.activity_id = activity_id;
        this.title = title;
        this.description = description;
        this.end_date = end_date;
        this.end_time = end_time;
        this.status = status;
    }
    public int getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(int activity_id) {
        this.activity_id = activity_id;
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
}

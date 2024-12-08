package com.example.smartposture.data.model;

public class Notification {
    private int notification_id;
    private String description;
    private String time;
    private String date;

    public Notification(int notification_id, String description, String time, String date) {
        this.notification_id = notification_id;
        this.description = description;
        this.time = time;
        this.date = date;
    }

    public int getNotification_id() {
        return notification_id;
    }

    public void setNotification_id(int notification_id) {
        this.notification_id = notification_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

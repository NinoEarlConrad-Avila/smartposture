package com.example.smartposture.data.response;

import com.example.smartposture.data.model.Notification;

import java.util.List;

public class NotificationResponse {
    private String message;
    private int status;
    private List<Notification> notifications;

    public NotificationResponse(String message, int status, List<Notification> notifications) {
        this.message = message;
        this.status = status;
        this.notifications = notifications;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }
}

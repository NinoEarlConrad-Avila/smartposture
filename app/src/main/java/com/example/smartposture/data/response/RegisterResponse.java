package com.example.smartposture.data.response;

import com.example.smartposture.data.model.User;

public class RegisterResponse {
    private String message;
    private User user;

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

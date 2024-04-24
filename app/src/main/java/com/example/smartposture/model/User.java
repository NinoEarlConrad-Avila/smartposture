package com.example.smartposture.model;

public class User {
    private String username;
    private String birthdate;

    public User() {
        // Default constructor required for Firebase
    }

    public User(String username, String birthdate) {
        this.username = username;
        this.birthdate = birthdate;
    }

    // Getters and setters for the User class
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }
}

package com.example.smartposture.model;

public class UserModel {
    private String username;
    private String firstname;
    private String lastname;
    private String birthdate;

    public UserModel() {
        // Default constructor required for Firebase
    }

    public UserModel(String username, String firstname, String lastname, String birthdate) {
        this.username = username;
        this.birthdate = birthdate;
        this.firstname = firstname;
        this.lastname = lastname;
    }
    public String getUsername() {
        return username;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }
    public String getBirthdate() {
        return birthdate;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }
}
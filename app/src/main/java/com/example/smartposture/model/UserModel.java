package com.example.smartposture.model;

public class UserModel {
    private String email;
    private String username;
    private String firstname;
    private String lastname;
    private String password;
    private String birthdate;
    private String usertype;

    public UserModel() {
        // Default constructor required for Firebase
    }

    public UserModel(String username, String firstname, String lastname, String birthdate, String userType){
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.birthdate = birthdate;
        this.usertype = userType;
    }
    public UserModel(String email, String username, String firstname, String lastname, String password, String birthdate, String usertype) {
        this.email = email;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.password = password;
        this.birthdate = birthdate;
        this.usertype = usertype;
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

    public String getUsertype() {
        return usertype;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
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

    public void setUserType(String userType) {
        this.usertype = userType;
    }
}

package com.example.smartposture.model;

public class UserModelMetaData {
    private String email;
    private String password;
    private Metadata user_metadata;

    public UserModelMetaData(String email, String password, Metadata user_metadata) {
        this.email = email;
        this.password = password;
        this.user_metadata = user_metadata;
    }

    public static class Metadata {
        private String username;
        private String firstname;
        private String lastname;
        private String birthdate;
        private String usertype;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getFirstname() {
            return firstname;
        }

        public void setFirstname(String firstname) {
            this.firstname = firstname;
        }

        public String getLastname() {
            return lastname;
        }

        public void setLastname(String lastname) {
            this.lastname = lastname;
        }

        public String getBirthdate() {
            return birthdate;
        }

        public void setBirthdate(String birthdate) {
            this.birthdate = birthdate;
        }

        public String getUsertype() {
            return usertype;
        }

        public void setUsertype(String usertype) {
            this.usertype = usertype;
        }

        public Metadata(String username, String firstname, String lastname, String birthdate, String usertype) {
            this.username = username;
            this.firstname = firstname;
            this.lastname = lastname;
            this.birthdate = birthdate;
            this.usertype = usertype;
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Metadata getUser_metadata() {
        return user_metadata;
    }

    public void setUser_metadata(Metadata user_metadata) {
        this.user_metadata = user_metadata;
    }
// Getters and Setters
}

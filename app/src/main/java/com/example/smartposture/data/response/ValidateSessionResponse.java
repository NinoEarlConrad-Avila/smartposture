package com.example.smartposture.data.response;

public class ValidateSessionResponse {
    private String message;
    private int user_id;

    public ValidateSessionResponse(String message, int user_id) {
        this.message = message;
        this.user_id = user_id;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getMessage() {
        return message;
    }

    public int getUserId() {
        return user_id;
    }
}


package com.example.smartposture.data.request;

public class RoomRequest {
    private int user_id;

    public RoomRequest(int user_id) {
        this.user_id = user_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}

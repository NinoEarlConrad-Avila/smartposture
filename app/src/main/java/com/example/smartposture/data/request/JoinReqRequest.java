package com.example.smartposture.data.request;

public class JoinReqRequest {
    private int room_id;
    private int user_id;

    public JoinReqRequest(int room_id, int user_id) {
        this.room_id = room_id;
        this.user_id = user_id;
    }

    public JoinReqRequest(int room_id) {
        this.room_id = room_id;
    }

    public int getRoom_id() {
        return room_id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}

package com.example.smartposture.data.request;

public class JoinRoomRequest {
    private int user_id;
    private String username;
    private int room_id;

    public JoinRoomRequest(int user_id, String username, int room_id) {
        this.user_id = user_id;
        this.username = username;
        this.room_id = room_id;
    }
}

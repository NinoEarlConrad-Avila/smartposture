package com.example.smartposture.data.request;

public class CreateRoomRequest {
    private String room_name;
    private String room_code;
    private int room_creator;
    private String room_creator_username;

    public CreateRoomRequest(String room_name, String room_code, int room_creator, String room_creator_username) {
        this.room_name = room_name;
        this.room_code = room_code;
        this.room_creator = room_creator;
        this.room_creator_username = room_creator_username;
    }
}

package com.example.smartposture.data.model;

public class Room {
    private int room_id;
    private String room_name;
    private String room_code;
    private int room_creator;
    private String creator_username;

    private int request_status;

    public Room(int room_id, String room_name, String room_code, int room_creator, String creator_username, int request_status) {
        this.room_id = room_id;
        this.room_name = room_name;
        this.room_code = room_code;
        this.room_creator = room_creator;
        this.creator_username = creator_username;
        this.request_status = request_status;
    }

    public Room(int room_id, String room_name, String room_code){
        this.room_id = room_id;
        this.room_name = room_name;
        this.room_code = room_code;
    }

    public int getRoom_id() {
        return room_id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public String getRoom_code() {
        return room_code;
    }

    public void setRoom_code(String room_code) {
        this.room_code = room_code;
    }

    public int getRoom_creator() {
        return room_creator;
    }

    public void setRoom_creator(int room_creator) {
        this.room_creator = room_creator;
    }

    public String getCreator_username() {
        return creator_username;
    }

    public void setCreator_username(String creator_username) {
        this.creator_username = creator_username;
    }

    public int getRequest_status() {
        return request_status;
    }

    public void setRequest_status(int request_status) {
        this.request_status = request_status;
    }
}

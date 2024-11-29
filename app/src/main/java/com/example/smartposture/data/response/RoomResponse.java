package com.example.smartposture.data.response;

import com.example.smartposture.data.model.Room;

import java.util.List;

public class RoomResponse {
    private String message;
    private List<Room> rooms;

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> room) {
        this.rooms =room;
    }
}

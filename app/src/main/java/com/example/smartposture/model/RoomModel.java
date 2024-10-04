package com.example.smartposture.model;

import java.io.Serializable;

public class RoomModel implements Serializable {
    private String roomCode;
    private String roomCreator;
    private int roomID;
    private String roomName;

    public RoomModel() {}

    public RoomModel(String roomCode, String roomCreator, int roomID, String roomName) {
        this.roomCode = roomCode;
        this.roomCreator = roomCreator;
        this.roomID = roomID;
        this.roomName = roomName;
    }

    // Getters and Setters
    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public String getRoomCreator() {
        return roomCreator;
    }

    public void setRoomCreator(String roomCreator) {
        this.roomCreator = roomCreator;
    }

    public int getRoomID() {
        return roomID;
    }

    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}


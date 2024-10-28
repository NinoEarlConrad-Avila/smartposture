package com.example.smartposture.model;

public class JoinRoomRequestModel {
    private long roomId;
    private int status;
    private String username;
    public JoinRoomRequestModel() {
    }

    public JoinRoomRequestModel(long roomId, int status, String username) {
        this.roomId = roomId;
        this.status = status;
        this.username = username;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

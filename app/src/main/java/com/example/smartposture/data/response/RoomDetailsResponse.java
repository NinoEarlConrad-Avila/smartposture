package com.example.smartposture.data.response;

import com.example.smartposture.data.model.Room;

public class RoomDetailsResponse {
    private String message;
    private int status;
    private Room details;

    public RoomDetailsResponse(String message, int status, Room details) {
        this.message = message;
        this.status = status;
        this.details = details;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public Room getRoom() {
        return details;
    }
}

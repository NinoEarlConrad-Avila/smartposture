package com.example.smartposture.data.request;

public class TraineeActivityRequest {
    private int room_id;
    private int user_id;

    public TraineeActivityRequest(int room_id, int user_id) {
        this.room_id = room_id;
        this.user_id = user_id;
    }
}

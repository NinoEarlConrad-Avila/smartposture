package com.example.smartposture.data.request;

public class ActivityStatisticsRequest {
    private int room_id;
    private int activity_id;

    public ActivityStatisticsRequest(int room_id, int activity_id) {
        this.room_id = room_id;
        this.activity_id = activity_id;
    }

    public int getRoom_id() {
        return room_id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

    public int getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(int activity_id) {
        this.activity_id = activity_id;
    }
}

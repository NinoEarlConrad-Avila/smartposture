package com.example.smartposture.data.request;

public class MonthlyWorkoutsRequest {
    private int user_id;
    private int year;

    public MonthlyWorkoutsRequest(int user_id, int year) {
        this.user_id = user_id;
        this.year = year;
    }
}

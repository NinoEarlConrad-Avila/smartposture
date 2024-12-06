package com.example.smartposture.data.response;

import com.example.smartposture.data.model.Trainee;

import java.util.List;

public class RoomTraineesResponse {
    private String message;
    private List<Trainee> trainees;

    public RoomTraineesResponse(String message, List<Trainee> trainees) {
        this.message = message;
        this.trainees = trainees;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Trainee> getTrainees() {
        return trainees;
    }

    public void setTrainees(List<Trainee> trainees) {
        this.trainees = trainees;
    }
}

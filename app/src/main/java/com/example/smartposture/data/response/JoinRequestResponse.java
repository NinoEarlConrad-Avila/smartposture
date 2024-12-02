package com.example.smartposture.data.response;

import com.example.smartposture.data.model.JoinRequest;

import java.util.List;

public class JoinRequestResponse {
    private String message;
    private List<JoinRequest> requests;

    public JoinRequestResponse(String message, List<JoinRequest> requests) {
        this.message = message;
        this.requests = requests;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<JoinRequest> getRequests() {
        return requests;
    }

    public void setRequests(List<JoinRequest> requests) {
        this.requests = requests;
    }
}

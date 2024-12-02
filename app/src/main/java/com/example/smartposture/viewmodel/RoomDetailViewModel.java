package com.example.smartposture.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartposture.data.repository.RoomDetailRepository;
import com.example.smartposture.data.response.JoinRequestResponse;
import com.example.smartposture.data.response.RoomDetailsResponse;

public class RoomDetailViewModel extends ViewModel {

    private final RoomDetailRepository roomDetailRepository;

    public RoomDetailViewModel(){
        roomDetailRepository = new RoomDetailRepository();
    }

    public LiveData<RoomDetailsResponse> fetchRoomDetails(int roomId) {
        return roomDetailRepository.fetchRoomDetails(roomId);
    }

    public LiveData<JoinRequestResponse> fetchJoinRequests(int roomId) {
        return roomDetailRepository.fetchJoinRequests(roomId);
    }

    public LiveData<String> acceptJoinRequest(int roomId, int userId) {
        return roomDetailRepository.acceptJoinRequest(roomId, userId);
    }

    public LiveData<String> rejectJoinRequest(int roomId, int userId) {
        return roomDetailRepository.rejectJoinRequest(roomId, userId);
    }
}

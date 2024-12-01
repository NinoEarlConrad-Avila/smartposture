package com.example.smartposture.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartposture.data.repository.RoomDetailRepository;
import com.example.smartposture.data.response.RoomDetailsResponse;

public class RoomDetailViewModel extends ViewModel {

    private final RoomDetailRepository roomDetailRepository;

    public RoomDetailViewModel(){
        roomDetailRepository = new RoomDetailRepository();
    }

    public LiveData<RoomDetailsResponse> fetchRoomDetails(int workoutId) {
        return roomDetailRepository.fetchRoomDetails(workoutId);
    }
}

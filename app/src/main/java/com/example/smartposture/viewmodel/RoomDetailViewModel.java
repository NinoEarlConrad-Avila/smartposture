package com.example.smartposture.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartposture.data.repository.RoomDetailRepository;
import com.example.smartposture.data.model.JoinRequest;
import com.example.smartposture.data.response.RoomDetailsResponse;

import java.util.ArrayList;
import java.util.List;

public class RoomDetailViewModel extends ViewModel {

    private final RoomDetailRepository roomDetailRepository;
    private final MutableLiveData<List<JoinRequest>> joinRequestsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingStateLiveData = new MutableLiveData<>();

    public RoomDetailViewModel() {
        roomDetailRepository = new RoomDetailRepository();
    }
    public LiveData<Boolean> getLoadingState() {
        return loadingStateLiveData;
    }

    public LiveData<RoomDetailsResponse> fetchRoomDetails(int roomId) {
        return roomDetailRepository.fetchRoomDetails(roomId);
    }

    public LiveData<List<JoinRequest>> fetchJoinRequests(int roomId) {
        loadingStateLiveData.setValue(true);
        roomDetailRepository.fetchJoinRequests(roomId).observeForever(response -> {
            if (response != null && response.getRequests() != null) {
                joinRequestsLiveData.setValue(response.getRequests());
            } else {
                joinRequestsLiveData.setValue(new ArrayList<>());
            }
            loadingStateLiveData.setValue(false);
        });
        return joinRequestsLiveData;
    }

    public void acceptJoinRequest(int roomId, int userId) {
        roomDetailRepository.acceptJoinRequest(roomId, userId).observeForever(result -> {
            if ("Success".equals(result)) {
                fetchJoinRequests(roomId);
            }
        });
    }

    public void rejectJoinRequest(int roomId, int userId) {
        roomDetailRepository.rejectJoinRequest(roomId, userId).observeForever(result -> {
            if ("Success".equals(result)) {
                fetchJoinRequests(roomId);
            }
        });
    }
}
package com.example.smartposture.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartposture.data.model.Trainee;
import com.example.smartposture.data.repository.RoomDetailRepository;
import com.example.smartposture.data.model.JoinRequest;
import com.example.smartposture.data.response.RoomDetailsResponse;

import java.util.ArrayList;
import java.util.List;

public class RoomDetailViewModel extends ViewModel {

    private final RoomDetailRepository roomDetailRepository;
    private final MutableLiveData<List<JoinRequest>> joinRequestsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Trainee>> roomTraineesLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Trainee>> availableTraineesLiveData = new MutableLiveData<>();
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
        loadingStateLiveData.setValue(true);
        roomDetailRepository.acceptJoinRequest(roomId, userId).observeForever(result -> {
            if ("Success".equals(result)) {
                fetchJoinRequests(roomId);
            }
        });
    }

    public void rejectJoinRequest(int roomId, int userId) {
        loadingStateLiveData.setValue(true);
        roomDetailRepository.rejectJoinRequest(roomId, userId).observeForever(result -> {
            if ("Success".equals(result)) {
                fetchJoinRequests(roomId);
            }
        });
    }

    public LiveData<List<Trainee>> fetchRoomTrainees(int roomId) {
        loadingStateLiveData.setValue(true);
        roomDetailRepository.fetchRoomTrainees(roomId).observeForever(response -> {
            if (response != null && response.getTrainees() != null) {
                roomTraineesLiveData.setValue(response.getTrainees());
            } else {
                roomTraineesLiveData.setValue(new ArrayList<>());
            }
            loadingStateLiveData.setValue(false);
        });
        return roomTraineesLiveData;
    }

    public LiveData<List<Trainee>> fetchAvailableTrainees(int roomId) {
        loadingStateLiveData.setValue(true);
        roomDetailRepository.fetchAvailableTrainees(roomId).observeForever(response -> {
            if (response != null && response.getTrainees() != null) {
                availableTraineesLiveData.setValue(response.getTrainees());
            } else {
                availableTraineesLiveData.setValue(new ArrayList<>());
            }
            loadingStateLiveData.setValue(false);
        });
        return availableTraineesLiveData;
    }

    public void removeTrainee(int roomId, int userId) {
        loadingStateLiveData.setValue(true);
        roomDetailRepository.removeTrainee(roomId, userId).observeForever(result -> {
            if ("Success".equals(result)) {
                fetchRoomTrainees(roomId);
            }
        });
    }

    public void addTrainee(int roomId, int userId) {
        loadingStateLiveData.setValue(true);
        roomDetailRepository.addTrainee(roomId, userId).observeForever(result -> {
            if ("Success".equals(result)) {
                fetchAvailableTrainees(roomId);
            }
        });
    }
}

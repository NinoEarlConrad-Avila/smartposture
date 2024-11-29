package com.example.smartposture.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartposture.data.model.Room;
import com.example.smartposture.data.repository.RoomRepository;
import com.example.smartposture.data.request.RoomRequest;

import java.util.List;

public class RoomViewModel extends ViewModel {
    private final MutableLiveData<List<Room>> roomsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final RoomRepository repository = RoomRepository.getInstance();

    public MutableLiveData<List<Room>> getRoomsLiveData() {
        return roomsLiveData;
    }

    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void fetchRooms(int userId) {
        RoomRequest request = new RoomRequest(userId);

        repository.fetchTraineeRooms(request, new RoomRepository.RoomCallback() {
            @Override
            public void onSuccess(List<Room> rooms) {
                roomsLiveData.postValue(rooms);
            }

            @Override
            public void onFailure(String errorMessage) {
                errorLiveData.postValue(errorMessage);
            }
        });
    }
}

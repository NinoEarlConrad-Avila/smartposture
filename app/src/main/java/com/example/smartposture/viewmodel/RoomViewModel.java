package com.example.smartposture.viewmodel;

import androidx.lifecycle.LiveData;
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
    private final MutableLiveData<Room> selectedRoomLiveData = new MutableLiveData<>();

    public LiveData<Room> getSelectedRoomLiveData() {
        return selectedRoomLiveData;
    }
    public void selectRoom(Room room) {
        selectedRoomLiveData.setValue(room);
    }
    public MutableLiveData<List<Room>> getRoomsLiveData() {
        return roomsLiveData;
    }

    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void updateRooms(List<Room> newRooms) {
        roomsLiveData.postValue(newRooms);
    }
    public void fetchTraineeRooms(int userId) {
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

    public void fetchTraineeAvailableRooms(int userId) {
        RoomRequest request = new RoomRequest(userId);

        repository.fetchTraineeAvailableRooms(request, new RoomRepository.RoomCallback() {
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

    public void fetchTrainerRooms(int userId) {
        RoomRequest request = new RoomRequest(userId);

        repository.fetchTrainerRooms(request, new RoomRepository.RoomCallback() {
            @Override
            public void onSuccess(List<Room> rooms) {
                updateRooms(rooms);
            }

            @Override
            public void onFailure(String errorMessage) {
                errorLiveData.postValue(errorMessage);
            }
        });
    }
}

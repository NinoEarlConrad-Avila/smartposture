package com.example.smartposture.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartposture.data.model.Room;
import com.example.smartposture.data.repository.RoomRepository;
import com.example.smartposture.data.request.CreateRoomRequest;
import com.example.smartposture.data.request.JoinReqRequest;
import com.example.smartposture.data.request.JoinRoomRequest;
import com.example.smartposture.data.request.RoomRequest;

import java.util.List;

public class RoomViewModel extends ViewModel {
    private final RoomRepository repository = RoomRepository.getInstance();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Room> selectedRoomLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Room>> roomsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingStateLiveData = new MutableLiveData<>();
    private MutableLiveData<String> roomCreationStatus = new MutableLiveData<>();
    private MutableLiveData<String> joinRequestStatus = new MutableLiveData<>();
    private MutableLiveData<String> cancelRequestStatus = new MutableLiveData<>();

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
    public LiveData<Boolean> getLoadingState() {
        return loadingStateLiveData;
    }

    public LiveData<String> getRoomCreationStatus() {
        return roomCreationStatus;
    }

    public LiveData<String> getJoinRequestStatus() {
        return joinRequestStatus;
    }
    public LiveData<String> getCancelRequestStatus() {
        return cancelRequestStatus;
    }

    public void updateRoomCreationStatus(String status) {
        roomCreationStatus.setValue(status);
    }
    public void createRoom(CreateRoomRequest request) {
        repository.createRoom(request, new RoomRepository.CreateRoomCallback() {
            @Override
            public void onSuccess(String message) {
                roomCreationStatus.postValue(message);
            }

            @Override
            public void onFailure(String errorMessage) {
                roomCreationStatus.postValue(errorMessage);
            }
        });
    }

    public void fetchTraineeRooms(int userId) {
        loadingStateLiveData.setValue(true);
        RoomRequest request = new RoomRequest(userId);

        repository.fetchTraineeRooms(request, new RoomRepository.RoomCallback() {
            @Override
            public void onSuccess(List<Room> rooms) {
                roomsLiveData.postValue(rooms);
                loadingStateLiveData.setValue(false);
            }

            @Override
            public void onFailure(String errorMessage) {
                errorLiveData.postValue(errorMessage);
                loadingStateLiveData.setValue(false);
            }
        });
    }

    public void fetchTrainerRooms(int userId) {
        loadingStateLiveData.setValue(true);
        RoomRequest request = new RoomRequest(userId);

        repository.fetchTrainerRooms(request, new RoomRepository.RoomCallback() {
            @Override
            public void onSuccess(List<Room> rooms) {
                roomsLiveData.postValue(rooms);
                loadingStateLiveData.setValue(false);
            }

            @Override
            public void onFailure(String errorMessage) {
                errorLiveData.postValue(errorMessage);
                loadingStateLiveData.setValue(false);
            }
        });
    }

    public void fetchTraineeAvailableRooms(int userId) {
        loadingStateLiveData.setValue(true);
        RoomRequest request = new RoomRequest(userId);

        repository.fetchTraineeAvailableRooms(request, new RoomRepository.RoomCallback() {
            @Override
            public void onSuccess(List<Room> rooms) {
                roomsLiveData.postValue(rooms);
                loadingStateLiveData.setValue(false);
            }

            @Override
            public void onFailure(String errorMessage) {
                errorLiveData.postValue(errorMessage);
                loadingStateLiveData.setValue(false);
            }
        });
    }

    public void requestJoinRoom(int userId, String username, int roomId) {
        JoinRoomRequest request = new JoinRoomRequest(userId, username, roomId);
        repository.requestJoinRoom(request, new RoomRepository.CreateRoomCallback() {
            @Override
            public void onSuccess(String message) {
                joinRequestStatus.postValue(message);
            }

            @Override
            public void onFailure(String errorMessage) {
                joinRequestStatus.postValue(errorMessage);
            }
        });
    }

    public void cancelJoinRequest(int roomId, int userId) {
        JoinReqRequest request = new JoinReqRequest(roomId, userId);
        repository.cancelJoinRequest(request, new RoomRepository.CreateRoomCallback() {
            @Override
            public void onSuccess(String message) {
                cancelRequestStatus.postValue(message);
            }

            @Override
            public void onFailure(String errorMessage) {
                cancelRequestStatus.postValue(errorMessage);
            }
        });
    }

    public void joinRoomCode(int roomId, int userId) {
        loadingStateLiveData.setValue(true);
        repository.joinRoomCode(roomId, userId).observeForever(result -> {
            if ("Success".equals(result)) {
//                fetchTraineeAvailableRooms(roomId);
            }
        });
    }
}

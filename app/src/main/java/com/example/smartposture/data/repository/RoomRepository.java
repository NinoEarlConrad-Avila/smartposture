package com.example.smartposture.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.smartposture.data.api.ApiService;
import com.example.smartposture.data.model.Room;
import com.example.smartposture.data.request.CreateRoomRequest;
import com.example.smartposture.data.request.JoinReqRequest;
import com.example.smartposture.data.request.JoinRoomRequest;
import com.example.smartposture.data.request.RoomRequest;
import com.example.smartposture.data.api.ApiClient;
import com.example.smartposture.data.response.ApiResponse;
import com.example.smartposture.data.response.RoomResponse;
import com.google.android.gms.common.api.Api;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoomRepository {
    private final ApiService apiService;
    private static RoomRepository instance;

    public RoomRepository() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    public static RoomRepository getInstance() {
        if (instance == null) {
            instance = new RoomRepository();
        }
        return instance;
    }

    public void fetchTraineeRooms(RoomRequest request, RoomCallback callback) {
        Call<RoomResponse> call = apiService.getTraineeRooms(request);

        call.enqueue(new Callback<RoomResponse>() {
            @Override
            public void onResponse(Call<RoomResponse> call, Response<RoomResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RoomResponse roomResponse = response.body();

                    if ("No rooms".equals(roomResponse.getMessage()) || roomResponse.getRooms() == null) {
                        callback.onSuccess(Collections.emptyList());
                    } else {
                        callback.onSuccess(roomResponse.getRooms());
                    }
                } else {
                    callback.onFailure("Failed to fetch rooms: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<RoomResponse> call, Throwable t) {
                callback.onFailure("API call failed: " + t.getMessage());
            }
        });
    }

    public void fetchTraineeAvailableRooms(RoomRequest request, RoomCallback callback) {
        Call<RoomResponse> call = apiService.getTraineeAvailableRooms(request);

        call.enqueue(new Callback<RoomResponse>() {
            @Override
            public void onResponse(Call<RoomResponse> call, Response<RoomResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RoomResponse roomResponse = response.body();

                    if ("No rooms".equals(roomResponse.getMessage()) || roomResponse.getRooms() == null) {
                        callback.onSuccess(Collections.emptyList());
                    } else {
                        callback.onSuccess(roomResponse.getRooms());
                    }
                } else {
                    callback.onFailure("Failed to fetch workouts: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<RoomResponse> call, Throwable t) {
                callback.onFailure("API call failed: " + t.getMessage());
            }
        });
    }
    public void fetchTrainerRooms(RoomRequest request, RoomCallback callback) {
        Call<RoomResponse> call = apiService.getTrainerRooms(request);

        call.enqueue(new Callback<RoomResponse>() {
            @Override
            public void onResponse(Call<RoomResponse> call, Response<RoomResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RoomResponse roomResponse = response.body();

                    if ("No rooms".equals(roomResponse.getMessage()) || roomResponse.getRooms() == null) {
                        callback.onSuccess(Collections.emptyList());
                    } else {
                        callback.onSuccess(roomResponse.getRooms());
                    }
                } else {
                    callback.onFailure("Failed to fetch rooms: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<RoomResponse> call, Throwable t) {
                callback.onFailure("API call failed: " + t.getMessage());
            }
        });
    }

    public void createRoom(CreateRoomRequest request, CreateRoomCallback callback) {
        apiService.createRoom(request).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.getStatus() == 0) {
                        callback.onFailure(apiResponse.getMessage());
                    } else {
                        callback.onSuccess(apiResponse.getMessage());
                    }
                } else {
                    callback.onFailure("Failed to create room: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                callback.onFailure("API call failed: " + t.getMessage());
            }
        });
    }

    public void requestJoinRoom(JoinRoomRequest request, CreateRoomCallback callback) {
        Call<ApiResponse> call = apiService.requestJoinRoom(request);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.getStatus() == 0) {
                        callback.onFailure(apiResponse.getMessage());
                    } else {
                        callback.onSuccess(apiResponse.getMessage());
                    }
                } else {
                    callback.onFailure("Failed to sent join request: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                callback.onFailure("API call failed: " + t.getMessage());
            }
        });
    }

    public void cancelJoinRequest(JoinReqRequest request, CreateRoomCallback callback) {
        Call<ApiResponse> call = apiService.cancelJoinRequest(request);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.getStatus() == 0) {
                        callback.onFailure(apiResponse.getMessage());
                    } else {
                        callback.onSuccess(apiResponse.getMessage());
                    }
                } else {
                    callback.onFailure("Failed to cancel join request: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                callback.onFailure("API call failed: " + t.getMessage());
            }
        });
    }

    public interface CreateRoomCallback {
        void onSuccess(String message);
        void onFailure(String errorMessage);
    }

    public interface RoomCallback {
        void onSuccess(List<Room> rooms);
        void onFailure(String errorMessage);
    }
}


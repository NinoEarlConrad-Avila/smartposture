package com.example.smartposture.data.repository;

import com.example.smartposture.data.api.ApiService;
import com.example.smartposture.data.model.Room;
import com.example.smartposture.data.request.RoomRequest;
import com.example.smartposture.data.api.ApiClient;
import com.example.smartposture.data.response.RoomResponse;

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

    public interface RoomCallback {
        void onSuccess(List<Room> rooms);
        void onFailure(String errorMessage);
    }
}


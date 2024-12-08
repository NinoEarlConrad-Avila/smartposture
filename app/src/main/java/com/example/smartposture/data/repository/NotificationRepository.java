package com.example.smartposture.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.smartposture.data.api.ApiClient;
import com.example.smartposture.data.api.ApiService;
import com.example.smartposture.data.request.RoomIdRequest;
import com.example.smartposture.data.request.UserIdRequest;
import com.example.smartposture.data.response.NotificationResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationRepository {
    private final ApiService apiService;

    public NotificationRepository(){
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    public LiveData<NotificationResponse> fetchUserNotifications(int userId) {
        MutableLiveData<NotificationResponse> liveData = new MutableLiveData<>();
        UserIdRequest request = new UserIdRequest(userId);

        apiService.getUserNotification(request).enqueue(new Callback<NotificationResponse>() {
            @Override
            public void onResponse(Call<NotificationResponse> call, Response<NotificationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(response.body());
                } else {
                    liveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<NotificationResponse> call, Throwable t) {
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<NotificationResponse> fetchRoomNotifications(int roomId) {
        MutableLiveData<NotificationResponse> liveData = new MutableLiveData<>();
        RoomIdRequest request = new RoomIdRequest(roomId);

        apiService.getRoomNotification(request).enqueue(new Callback<NotificationResponse>() {
            @Override
            public void onResponse(Call<NotificationResponse> call, Response<NotificationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(response.body());
                } else {
                    liveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<NotificationResponse> call, Throwable t) {
                liveData.postValue(null);
            }
        });
        return liveData;
    }
}

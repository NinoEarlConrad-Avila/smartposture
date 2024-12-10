package com.example.smartposture.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.smartposture.data.api.ApiClient;
import com.example.smartposture.data.api.ApiService;
import com.example.smartposture.data.request.RoomIdRequest;
import com.example.smartposture.data.response.ActivityResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityRepository {
    private final ApiService apiService;

    public ActivityRepository(){
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    public LiveData<ActivityResponse> fetchActiveActivities(int roomId) {
        MutableLiveData<ActivityResponse> liveData = new MutableLiveData<>();
        RoomIdRequest request = new RoomIdRequest(roomId);

        apiService.getActiveActivities(request).enqueue(new Callback<ActivityResponse>() {
            @Override
            public void onResponse(Call<ActivityResponse> call, Response<ActivityResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(response.body());
                } else {
                    liveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<ActivityResponse> call, Throwable t) {
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<ActivityResponse> fetchInactiveActivities(int roomId) {
        MutableLiveData<ActivityResponse> liveData = new MutableLiveData<>();
        RoomIdRequest request = new RoomIdRequest(roomId);

        apiService.getInactiveActivities(request).enqueue(new Callback<ActivityResponse>() {
            @Override
            public void onResponse(Call<ActivityResponse> call, Response<ActivityResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(response.body());
                } else {
                    liveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<ActivityResponse> call, Throwable t) {
                liveData.postValue(null);
            }
        });
        return liveData;
    }
}

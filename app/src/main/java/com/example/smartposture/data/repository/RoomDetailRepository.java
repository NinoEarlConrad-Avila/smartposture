package com.example.smartposture.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.smartposture.data.api.ApiClient;
import com.example.smartposture.data.api.ApiService;
import com.example.smartposture.data.request.RoomDetailsRequest;
import com.example.smartposture.data.response.RoomDetailsResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoomDetailRepository {
    private final ApiService apiService;

    public RoomDetailRepository(){
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    public LiveData<RoomDetailsResponse> fetchRoomDetails(int roomId) {
        MutableLiveData<RoomDetailsResponse> data = new MutableLiveData<>();

        RoomDetailsRequest request = new RoomDetailsRequest(roomId);
        apiService.getRoomDetails(request).enqueue(new Callback<RoomDetailsResponse>() {
            @Override
            public void onResponse(Call<RoomDetailsResponse> call, Response<RoomDetailsResponse> response) {
                Log.d("Repository", "Response: " +response.body());
                if (response.isSuccessful() && response.body() != null) {
                    data.postValue(response.body());
                } else {
                    data.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<RoomDetailsResponse> call, Throwable t) {
                data.postValue(null);
            }
        });
        return data;
    }
}

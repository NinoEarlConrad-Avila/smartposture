package com.example.smartposture.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.smartposture.data.api.ApiClient;
import com.example.smartposture.data.api.ApiService;
import com.example.smartposture.data.request.ActivityIdRequest;
import com.example.smartposture.data.request.CreateActivityRequest;
import com.example.smartposture.data.request.RoomIdRequest;
import com.example.smartposture.data.request.TraineeScoreRequest;
import com.example.smartposture.data.response.ActivityDetailResponse;
import com.example.smartposture.data.response.ActivityResponse;
import com.example.smartposture.data.response.ApiResponse;

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

    public LiveData<String> addRoomActivity(CreateActivityRequest request) {
        MutableLiveData<String> liveData = new MutableLiveData<>();
        apiService.addRoomActivity(request).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if(response.body().getMessage().equals("Success") && response.body().getStatus() == 1){
                        liveData.postValue("Success");
                    } else {
                        liveData.postValue(response.body().getMessage());
                    }
                } else {
                    liveData.postValue("Failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                liveData.postValue("Error: " + t.getMessage());
            }
        });
        return liveData;
    }

    public LiveData<ActivityDetailResponse> fetchActivityDetails(ActivityIdRequest request) {
        MutableLiveData<ActivityDetailResponse> liveData = new MutableLiveData<>();

        apiService.getActivityDetails(request).enqueue(new Callback<ActivityDetailResponse>() {
            @Override
            public void onResponse(Call<ActivityDetailResponse> call, Response<ActivityDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(response.body());
                } else {
                    liveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<ActivityDetailResponse> call, Throwable t) {
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<String> addTraineeScore(TraineeScoreRequest request) {
        MutableLiveData<String> liveData = new MutableLiveData<>();
        apiService.addTraineeScores(request).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if(response.body().getMessage().equals("Success") && response.body().getStatus() == 1){
                        liveData.postValue("Success");
                    } else {
                        liveData.postValue(response.body().getMessage());
                    }
                } else {
                    liveData.postValue("Failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                liveData.postValue("Error: " + t.getMessage());
            }
        });
        return liveData;
    }
}

package com.example.smartposture.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.smartposture.data.api.ApiClient;
import com.example.smartposture.data.api.ApiService;
import com.example.smartposture.data.request.WorkoutDetailRequest;
import com.example.smartposture.data.response.WorkoutDetailResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkoutDetailRepository {
    private final ApiService apiService;

    public WorkoutDetailRepository() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    public LiveData<WorkoutDetailResponse> getWorkoutDetail(int workoutId) {
        MutableLiveData<WorkoutDetailResponse> data = new MutableLiveData<>();

        WorkoutDetailRequest request = new WorkoutDetailRequest(workoutId);
        apiService.getWorkoutDetail(request).enqueue(new Callback<WorkoutDetailResponse>() {
            @Override
            public void onResponse(Call<WorkoutDetailResponse> call, Response<WorkoutDetailResponse> response) {
                Log.d("Repository", "Response: " +response.body());
                if (response.isSuccessful() && response.body() != null) {
                    data.postValue(response.body());
                } else {
                    data.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<WorkoutDetailResponse> call, Throwable t) {
                data.postValue(null);
            }
        });

        return data;
    }
}

package com.example.smartposture.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.smartposture.data.api.ApiClient;
import com.example.smartposture.data.api.ApiService;
import com.example.smartposture.data.model.WorkoutDetail;
import com.example.smartposture.data.request.WorkoutDetailRequest;
import com.example.smartposture.data.response.ActivityDetailResponse;
import com.example.smartposture.data.response.ApiResponse;
import com.example.smartposture.data.response.WorkoutDetailResponse;
import com.example.smartposture.data.response.WorkoutResponse;
import com.example.smartposture.data.model.Workout;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkoutRepository {
    private static final String TAG = "WorkoutRepository";
    private final ApiService apiService;

    public WorkoutRepository() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    public void fetchWorkouts(WorkoutCallback callback) {
        Call<WorkoutResponse> call = apiService.getWorkouts();

        call.enqueue(new Callback<WorkoutResponse>() {
            @Override
            public void onResponse(Call<WorkoutResponse> call, Response<WorkoutResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getWorkouts());
                } else {
                    callback.onFailure("Failed to fetch workouts: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<WorkoutResponse> call, Throwable t) {
                Log.e(TAG, "API call failed: ", t);
                callback.onFailure("API call failed: " + t.getMessage());
            }
        });
    }

    public LiveData<WorkoutDetailResponse> fetchWorkoutDetail(WorkoutDetailRequest request) {
        MutableLiveData<WorkoutDetailResponse> liveData = new MutableLiveData<>();
        apiService.getWorkoutDetail(request).enqueue(new Callback<WorkoutDetailResponse>() {
            @Override
            public void onResponse(Call<WorkoutDetailResponse> call, Response<WorkoutDetailResponse> response) {
                Log.d("Repository", "Response: " +response.body());
                if (response.isSuccessful() && response.body().getWorkouts() != null) {
                    liveData.postValue(response.body());
                } else {
                    liveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<WorkoutDetailResponse> call, Throwable t) {
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public interface WorkoutCallback {
        void onSuccess(List<Workout> workouts);
        void onFailure(String errorMessage);
    }
}

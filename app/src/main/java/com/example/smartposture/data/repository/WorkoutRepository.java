package com.example.smartposture.data.repository;

import android.util.Log;

import com.example.smartposture.data.api.ApiClient;
import com.example.smartposture.data.api.ApiService;
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

    public interface WorkoutCallback {
        void onSuccess(List<Workout> workouts);
        void onFailure(String errorMessage);
    }
}

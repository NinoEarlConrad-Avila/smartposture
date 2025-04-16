package com.example.smartposture.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.smartposture.data.api.ApiClient;
import com.example.smartposture.data.api.ApiService;
import com.example.smartposture.data.request.MonthlyWorkoutsRequest;
import com.example.smartposture.data.request.UserIdRequest;
import com.example.smartposture.data.response.MonthlyWorkoutsResponse;
import com.example.smartposture.data.response.ProfileStatisticsResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileRepository {
    private final ApiService apiService;
    public ProfileRepository() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    public LiveData<ArrayList<ArrayList<Integer>>> fetchMonthlyWorkouts(MonthlyWorkoutsRequest request) {
        MutableLiveData<ArrayList<ArrayList<Integer>>> liveData = new MutableLiveData<>();

        apiService.getMonthlyWorkouts(request).enqueue(new Callback<MonthlyWorkoutsResponse>() {
            @Override
            public void onResponse(Call<MonthlyWorkoutsResponse> call, Response<MonthlyWorkoutsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(response.body().getWorkouts());
                } else {
                    liveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<MonthlyWorkoutsResponse> call, Throwable t) {
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<ProfileStatisticsResponse> fetchProfileStatistics(UserIdRequest request) {
        MutableLiveData<ProfileStatisticsResponse> liveData = new MutableLiveData<>();

        apiService.getProfileStatistics(request).enqueue(new Callback<ProfileStatisticsResponse>() {
            @Override
            public void onResponse(Call<ProfileStatisticsResponse> call, Response<ProfileStatisticsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(response.body());
                } else {
                    liveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<ProfileStatisticsResponse> call, Throwable t) {
                liveData.postValue(null);
            }
        });
        return liveData;
    }
}

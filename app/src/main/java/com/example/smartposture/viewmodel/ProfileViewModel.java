package com.example.smartposture.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartposture.data.request.LogoutRequest;
import com.example.smartposture.data.response.LogoutResponse;
import com.example.smartposture.data.api.ApiService;
import com.example.smartposture.data.api.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileViewModel extends ViewModel {

    private final MutableLiveData<LogoutResponse> logoutResult = new MutableLiveData<>();
    private final ApiService apiService;

    public ProfileViewModel() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    public LiveData<LogoutResponse> getLogoutResult() {
        return logoutResult;
    }

    public void logoutUser(String token) {
        if (token != null) {
            LogoutRequest request = new LogoutRequest(token);
            apiService.logoutUser(request).enqueue(new Callback<LogoutResponse>() {
                @Override
                public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        logoutResult.setValue(new LogoutResponse(response.body().getMessage(), true));
                    } else {
                        logoutResult.setValue(new LogoutResponse("Logout failed.", false));
                    }
                }

                @Override
                public void onFailure(Call<LogoutResponse> call, Throwable t) {
                    logoutResult.setValue(new LogoutResponse("Error: " + t.getMessage(), false));
                }
            });
        } else {
            logoutResult.setValue(new LogoutResponse("No token found. Logged out successfully.", true));
        }
    }
}

package com.example.smartposture.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartposture.data.api.ApiClient;
import com.example.smartposture.data.api.ApiService;
import com.example.smartposture.data.model.User;
import com.example.smartposture.data.request.LoginRequest;
import com.example.smartposture.data.response.LoginResponse;
import com.example.smartposture.util.ApiErrorUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends ViewModel {
    private LoginResponse loginResponse = null;
    private final MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<User> loggedInUser = new MutableLiveData<>();

    public LiveData<Boolean> isLoginSuccessful() {
        return loginSuccess;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<User> getLoggedInUser() {
        return loggedInUser;
    }

    public void loginUser(String username, String password) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        LoginRequest loginRequest = new LoginRequest(username, password);

        apiService.loginUser(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    loggedInUser.postValue(loginResponse.getUser());
                    loginSuccess.postValue(true);
                } else {
                    // Error: Use ApiErrorUtil to parse and display the error message
                    String errorMessageText = ApiErrorUtil.parseError(response);
                    errorMessage.postValue(errorMessageText);
                    loginSuccess.postValue(false);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                errorMessage.postValue("Network error: " + t.getMessage());
                loginSuccess.postValue(false);
            }
        });
    }
}

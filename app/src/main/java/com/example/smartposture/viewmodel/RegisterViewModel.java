package com.example.smartposture.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartposture.data.api.ApiClient;
import com.example.smartposture.data.api.ApiService;
import com.example.smartposture.data.model.User;
import com.example.smartposture.data.request.RegisterRequest;
import com.example.smartposture.data.response.RegisterResponse;
import com.example.smartposture.util.ApiErrorUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterViewModel extends ViewModel {
    private RegisterResponse registerResponse = null;
    private final MutableLiveData<Boolean> registerSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<User> loggedInUser = new MutableLiveData<>();

    public LiveData<Boolean> isLoginSuccessful() {
        return registerSuccess;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<User> getLoggedInUser() {
        return loggedInUser;
    }

    public void registerUser(String email, String password, String username, String firstname, String lastname, String birthdate, String usertype){
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        RegisterRequest registerRequest = new RegisterRequest(email, password, username, firstname, lastname,birthdate, usertype);

        apiService.registerUser(registerRequest).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful() && response.body() != null){
                    registerResponse = response.body();
                    loggedInUser.postValue(registerResponse.getUser());
                    registerSuccess.postValue(true);
                } else {
                    String errorMessageText = ApiErrorUtil.parseError(response);
                    errorMessage.postValue(errorMessageText);
                    registerSuccess.postValue(false);
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                errorMessage.postValue("Error: " + t.getMessage());
                registerSuccess.postValue(false);
            }
        });
    }
}

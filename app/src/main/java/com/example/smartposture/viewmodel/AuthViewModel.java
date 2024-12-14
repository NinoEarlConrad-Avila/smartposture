package com.example.smartposture.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartposture.data.api.ApiClient;
import com.example.smartposture.data.api.ApiService;
import com.example.smartposture.data.model.User;
import com.example.smartposture.data.request.LoginRequest;
import com.example.smartposture.data.request.LogoutRequest;
import com.example.smartposture.data.request.RegisterRequest;
import com.example.smartposture.data.request.ValidateSessionRequest;
import com.example.smartposture.data.response.LoginResponse;
import com.example.smartposture.data.response.LogoutResponse;
import com.example.smartposture.data.response.RegisterResponse;
import com.example.smartposture.data.response.ValidateSessionResponse;
import com.example.smartposture.util.ApiErrorUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthViewModel extends ViewModel {
    private final ApiService apiService;
    private LoginResponse loginResponse = null;
    private RegisterResponse registerResponse = null;
    private final MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> registerSuccess = new MutableLiveData<>();
    private final MutableLiveData<LogoutResponse> logoutResult = new MutableLiveData<>();
    private MutableLiveData<Boolean> sessionValid = new MutableLiveData<>();
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
    public LiveData<LogoutResponse> getLogoutResult() {
        return logoutResult;
    }
    public LiveData<Boolean> getSessionValid() {
        return sessionValid;
    }
    public AuthViewModel(){
        apiService = ApiClient.getClient().create(ApiService.class);
    }
    public void loginUser(String username, String password) {
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

    public void registerUser(String email, String password, String username, String firstname, String lastname, String birthdate, String usertype){
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

    public void validateSessionToken(String token) {
        if (token == null || token.isEmpty()) {
            sessionValid.setValue(false);
            return;
        }

        ValidateSessionRequest request = new ValidateSessionRequest(token);
        Call<ValidateSessionResponse> call = apiService.validateSession(request);

        call.enqueue(new Callback<ValidateSessionResponse>() {
            @Override
            public void onResponse(Call<ValidateSessionResponse> call, Response<ValidateSessionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String message = response.body().getMessage();
                    if ("Session valid".equals(message)) {
                        sessionValid.setValue(true);
                    } else {
                        sessionValid.setValue(false);
                    }
                } else {
                    errorMessage.setValue("Response failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ValidateSessionResponse> call, Throwable t) {
                errorMessage.setValue("API call failed: " + t.getMessage());
            }
        });
    }
}

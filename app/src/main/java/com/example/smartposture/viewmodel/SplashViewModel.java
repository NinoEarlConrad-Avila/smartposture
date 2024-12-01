package com.example.smartposture.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.smartposture.data.api.ApiClient;
import com.example.smartposture.data.api.ApiService;
import com.example.smartposture.data.request.ValidateSessionRequest;
import com.example.smartposture.data.sharedpreference.SharedPreferenceManager;
import com.example.smartposture.data.response.ValidateSessionResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashViewModel extends AndroidViewModel {

    private ApiService apiService;
    private MutableLiveData<Boolean> sessionValidLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>();

    public SplashViewModel(Application application) {
        super(application);
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    public LiveData<Boolean> getSessionValidLiveData() {
        return sessionValidLiveData;
    }

    public LiveData<String> getErrorMessageLiveData() {
        return errorMessageLiveData;
    }

    public void validateSessionToken() {
        SharedPreferenceManager spManager = new SharedPreferenceManager(getApplication());
        String token = spManager.getSessionToken();
        ValidateSessionRequest request = new ValidateSessionRequest(token);
        Call<ValidateSessionResponse> call = apiService.validateSession(request);

        if (token != null) {
            call.enqueue(new Callback<ValidateSessionResponse>() {
                @Override
                public void onResponse(Call<ValidateSessionResponse> call, Response<ValidateSessionResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String message = response.body().getMessage();
                        if ("Session valid".equals(message)) {
                            sessionValidLiveData.setValue(true);
                        } else {
                            sessionValidLiveData.setValue(false);
                        }
                    } else {
                        Log.d("Failure", "Response Failed");
                        errorMessageLiveData.setValue("Response failed: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<ValidateSessionResponse> call, Throwable t) {
                    Log.d("Failure", "API Call Failed");
                    errorMessageLiveData.setValue("API call failed: " + t.getMessage());
                }
            });
        } else {
            sessionValidLiveData.setValue(false);
        }
    }
}

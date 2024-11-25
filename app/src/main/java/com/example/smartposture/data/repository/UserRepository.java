package com.example.smartposture.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.smartposture.data.api.ApiClient;
//import com.example.smartposture.data.api.ApiService;
import com.example.smartposture.data.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
//    private final ApiService apiService;
//
//    public UserRepository() {
//        apiService = ApiClient.getClient().create(ApiService.class);
//    }
//
//    public LiveData<Boolean> registerUser(User user) {
//        MutableLiveData<Boolean> successLiveData = new MutableLiveData<>();
//
//        apiService.registerUser(user).enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(Call<Void> call, Response<Void> response) {
//                successLiveData.setValue(response.isSuccessful());
//            }
//
//            @Override
//            public void onFailure(Call<Void> call, Throwable t) {
//                successLiveData.setValue(false);
//            }
//        });
//
//        return successLiveData;
//    }
}

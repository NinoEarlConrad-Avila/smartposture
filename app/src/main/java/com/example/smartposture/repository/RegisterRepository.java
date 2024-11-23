package com.example.smartposture.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.smartposture.model.UserModelMetaData;
import com.example.smartposture.data.api.ApiClient;
//import com.example.smartposture.data.api.ApiService;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterRepository {

//    private final ApiService apiService;
//
//    private static final String BASE_URL = "https://mvkbxhwchebqyfzudbcc.supabase.co";
//    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im12a2J4aHdjaGVicXlmenVkYmNjIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzExMjEyODgsImV4cCI6MjA0NjY5NzI4OH0.V9GGH00BIZkaa-K9U9vZrUU5g3a0bLiB_qo5qp1hVx0";
//
//    public RegisterRepository() {
//        apiService = ApiClient.getClient(BASE_URL, API_KEY).create(ApiService.class);
//    }
//
//    public LiveData<UserModelMetaData> registerUser(UserModelMetaData userModel) {
//        MutableLiveData<UserModelMetaData> liveData = new MutableLiveData<>();
//
//        apiService.registerUser(API_KEY, userModel).enqueue(new Callback<UserModelMetaData>() {
//            @Override
//            public void onResponse(Call<UserModelMetaData> call, Response<UserModelMetaData> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    Log.d("RegisterRepository", "Registration successful: " + response.body());
//                    liveData.setValue(response.body());
//                } else {
//                    handleErrorResponse(response);
//                    liveData.setValue(null);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<UserModelMetaData> call, Throwable t) {
//                Log.e("RegisterRepository", "Network error: " + t.getMessage(), t);
//                liveData.setValue(null);
//            }
//        });
//
//        return liveData;
//    }
//
//    private void handleErrorResponse(Response<UserModelMetaData> response) {
//        try {
//            if (response.errorBody() != null) {
//                Log.e("RegisterRepository", "Error: " + response.errorBody().string());
//            } else {
//                Log.e("RegisterRepository", "Unknown error");
//            }
//        } catch (IOException e) {
//            Log.e("RegisterRepository", "Error parsing error response", e);
//        }
//    }
}

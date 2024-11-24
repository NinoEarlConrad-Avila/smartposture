package com.example.smartposture.data.api;

import com.example.smartposture.data.request.LoginRequest;
import com.example.smartposture.data.model.User;
import com.example.smartposture.data.request.RegisterRequest;
import com.example.smartposture.data.response.LoginResponse;
import com.example.smartposture.data.response.RegisterResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("register")
    Call<RegisterResponse> registerUser(@Body RegisterRequest registerRequest);

    @POST("login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);
}

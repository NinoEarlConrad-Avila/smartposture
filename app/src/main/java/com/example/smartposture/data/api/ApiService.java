package com.example.smartposture.data.api;

import com.example.smartposture.data.request.LoginRequest;
import com.example.smartposture.data.model.User;
import com.example.smartposture.data.response.LoginResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
//    @POST("api/register")
//    Call<ResponseBody> registerUser(@Body User user);

    @POST("login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);
}

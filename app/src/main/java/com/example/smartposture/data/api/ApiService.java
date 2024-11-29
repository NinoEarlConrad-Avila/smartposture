package com.example.smartposture.data.api;

import com.example.smartposture.data.request.LoginRequest;
import com.example.smartposture.data.model.User;
import com.example.smartposture.data.request.RegisterRequest;
import com.example.smartposture.data.request.RoomRequest;
import com.example.smartposture.data.request.WorkoutDetailRequest;
import com.example.smartposture.data.response.LoginResponse;
import com.example.smartposture.data.response.RegisterResponse;
import com.example.smartposture.data.response.RoomResponse;
import com.example.smartposture.data.response.WorkoutDetailResponse;
import com.example.smartposture.data.response.WorkoutResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @POST("register")
    Call<RegisterResponse> registerUser(@Body RegisterRequest registerRequest);

    @POST("login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @GET ("home/getWorkouts")
    Call<WorkoutResponse> getWorkouts();

    @POST("home/getWorkoutDetail")
    Call<WorkoutDetailResponse> getWorkoutDetail(@Body WorkoutDetailRequest workoutDetailRequest);

    @POST("room/getTraineeRooms")
    Call<RoomResponse> getTraineeRooms(@Body RoomRequest roomRequest);

    @POST("room/getTraineeAvailableRooms")
    Call<RoomResponse> getTraineeAvailableRooms(@Body RoomRequest roomRequest);
}

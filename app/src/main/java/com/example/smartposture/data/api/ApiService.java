package com.example.smartposture.data.api;

import com.example.smartposture.data.request.LoginRequest;
import com.example.smartposture.data.request.LogoutRequest;
import com.example.smartposture.data.request.RegisterRequest;
import com.example.smartposture.data.request.RoomDetailsRequest;
import com.example.smartposture.data.request.RoomRequest;
import com.example.smartposture.data.request.ValidateSessionRequest;
import com.example.smartposture.data.request.WorkoutDetailRequest;
import com.example.smartposture.data.response.JoinRequestResponse;
import com.example.smartposture.data.response.LoginResponse;
import com.example.smartposture.data.response.LogoutResponse;
import com.example.smartposture.data.response.RegisterResponse;
import com.example.smartposture.data.response.RoomDetailsResponse;
import com.example.smartposture.data.response.RoomResponse;
import com.example.smartposture.data.response.ValidateSessionResponse;
import com.example.smartposture.data.response.WorkoutDetailResponse;
import com.example.smartposture.data.response.WorkoutResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @POST("register")
    Call<RegisterResponse> registerUser(@Body RegisterRequest registerRequest);

    @POST("login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @POST("validateSession")
    Call<ValidateSessionResponse> validateSession(@Body ValidateSessionRequest request);

    @POST("logout")
    Call<LogoutResponse> logoutUser(@Body LogoutRequest request);

    @GET ("home/getWorkouts")
    Call<WorkoutResponse> getWorkouts();

    @POST("home/getWorkoutDetail")
    Call<WorkoutDetailResponse> getWorkoutDetail(@Body WorkoutDetailRequest workoutDetailRequest);

    @POST("room/getTraineeRooms")
    Call<RoomResponse> getTraineeRooms(@Body RoomRequest roomRequest);

    @POST("room/getTraineeAvailableRooms")
    Call<RoomResponse> getTraineeAvailableRooms(@Body RoomRequest roomRequest);

    @POST("room/getTrainerRooms")
    Call<RoomResponse> getTrainerRooms(@Body RoomRequest roomRequest);

    @POST("room/getRoomDetails")
    Call<RoomDetailsResponse> getRoomDetails(@Body RoomDetailsRequest roomDetailsRequest);

    @POST("room/getRoomJoinRequests")
    Call<JoinRequestResponse> getRoomJoinRequests(@Body RoomDetailsRequest joinRequest);
}

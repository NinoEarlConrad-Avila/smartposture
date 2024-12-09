package com.example.smartposture.data.api;

import com.example.smartposture.data.request.CreateRoomRequest;
import com.example.smartposture.data.request.JoinReqRequest;
import com.example.smartposture.data.request.JoinRoomRequest;
import com.example.smartposture.data.request.LoginRequest;
import com.example.smartposture.data.request.LogoutRequest;
import com.example.smartposture.data.request.RegisterRequest;
import com.example.smartposture.data.request.RoomDetailsRequest;
import com.example.smartposture.data.request.RoomIdRequest;
import com.example.smartposture.data.request.RoomRequest;
import com.example.smartposture.data.request.UserIdRequest;
import com.example.smartposture.data.request.ValidateSessionRequest;
import com.example.smartposture.data.request.WorkoutDetailRequest;
import com.example.smartposture.data.response.ActivityResponse;
import com.example.smartposture.data.response.JoinRequestResponse;
import com.example.smartposture.data.response.LoginResponse;
import com.example.smartposture.data.response.LogoutResponse;
import com.example.smartposture.data.response.NotificationResponse;
import com.example.smartposture.data.response.RegisterResponse;
import com.example.smartposture.data.response.ApiResponse;
import com.example.smartposture.data.response.RoomDetailsResponse;
import com.example.smartposture.data.response.RoomResponse;
import com.example.smartposture.data.response.RoomTraineesResponse;
import com.example.smartposture.data.response.ValidateSessionResponse;
import com.example.smartposture.data.response.WorkoutDetailResponse;
import com.example.smartposture.data.response.WorkoutResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    // Auth API endpoints
    @POST("register")
    Call<RegisterResponse> registerUser(@Body RegisterRequest registerRequest);

    @POST("login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @POST("validateSession")
    Call<ValidateSessionResponse> validateSession(@Body ValidateSessionRequest request);

    @POST("logout")
    Call<LogoutResponse> logoutUser(@Body LogoutRequest request);

    // Home API endpoints
    @GET ("home/getWorkouts")
    Call<WorkoutResponse> getWorkouts();

    @POST("home/getWorkoutDetail")
    Call<WorkoutDetailResponse> getWorkoutDetail(@Body WorkoutDetailRequest workoutDetailRequest);

    // Room API endpoints
    @POST("room/getTraineeRooms")
    Call<RoomResponse> getTraineeRooms(@Body RoomRequest roomRequest);

    @POST("room/getTraineeAvailableRooms")
    Call<RoomResponse> getTraineeAvailableRooms(@Body RoomRequest roomRequest);

    @POST("room/getTrainerRooms")
    Call<RoomResponse> getTrainerRooms(@Body RoomRequest roomRequest);

    @POST("room/getRoomDetails")
    Call<RoomDetailsResponse> getRoomDetails(@Body RoomDetailsRequest roomDetailsRequest);

    @POST("room/getRoomJoinRequests")
    Call<JoinRequestResponse> getRoomJoinRequests(@Body JoinReqRequest joinRequest);

    @POST("room/createRoom")
    Call<ApiResponse> createRoom(@Body CreateRoomRequest createRoom);

    @POST("room/getRoomTrainees")
    Call<RoomTraineesResponse> getRoomTrainees(@Body RoomDetailsRequest request);

    @POST("room/getAvailableTrainees")
    Call<RoomTraineesResponse> getAvailableTrainees(@Body RoomDetailsRequest request);

    @POST("room/acceptJoinRequest")
    Call<JoinRequestResponse> acceptJoinRequest(@Body JoinReqRequest joinRequest);

    @POST("room/rejectJoinRequest")
    Call<JoinRequestResponse> rejectJoinRequest(@Body JoinReqRequest joinRequest);

    @POST("room/addTraineeRoom")
    Call<ApiResponse> addTrainee(@Body JoinReqRequest request);

    @POST("room/removeRoomTrainee")
    Call<ApiResponse> removeTrainee(@Body JoinReqRequest request);

    @POST("room/requestJoinRoom")
    Call<ApiResponse> requestJoinRoom(@Body JoinRoomRequest request);

    @POST("room/cancelJoinRequest")
    Call<ApiResponse> cancelJoinRequest(@Body JoinReqRequest request);

    // Notification API endpoints
    @POST("notif/getUserNotifications")
    Call<NotificationResponse> getUserNotification(@Body UserIdRequest request);

    @POST("notif/getRoomNotifications")
    Call<NotificationResponse> getRoomNotification(@Body RoomIdRequest request);

    // Activity API endpoints
    @POST("activity/getActiveActivities")
    Call<ActivityResponse> getActiveActivities(@Body RoomIdRequest request);

    @POST("activity/getInactiveActivities")
    Call<ActivityResponse> getInactiveActivities(@Body RoomIdRequest request);
}

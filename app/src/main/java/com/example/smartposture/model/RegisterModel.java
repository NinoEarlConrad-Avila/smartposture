package com.example.smartposture.model;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class RegisterModel {
    private final FirebaseAuth mAuth;
    private final DatabaseReference mDatabase;

    public RegisterModel() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
    }

    public void registerUser(String username, String email, String password, String firstname, String lastname,
                             String birthdate, OnRegisterCompleteListener listener) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String userID = user.getUid();
                            UserModel newUser = new UserModel(username, firstname, lastname, birthdate, "trainee");
                            writeUserDetails(newUser, userID, listener);
                        } else {
                            listener.onError("Failed to get user information.");
                        }
                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        listener.onError("Registration failed: " + errorMessage);
                    }
                });
    }

    private void writeUserDetails(UserModel userDetails, String uid, OnRegisterCompleteListener listener) {
        Log.d("User ID", uid);

        String currentDate = getCurrentDateFormatted();

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("username", userDetails.getUsername());
        userInfo.put("firstname", userDetails.getFirstname());
        userInfo.put("lastname", userDetails.getLastname());
        userInfo.put("birthdate", userDetails.getBirthdate());
        userInfo.put("usertype", userDetails.getUserType());

        Map<String, Object> dailyStats = new HashMap<>();
        dailyStats.put("pushup", 0);
        dailyStats.put("squat", 0);

        Map<String, Object> userStats = new HashMap<>();
        userStats.put(currentDate, dailyStats);

        Map<String, Object> userDetailsMap = new HashMap<>();
        userDetailsMap.put("info", userInfo);
        userDetailsMap.put("stats", userStats);

        mDatabase.child(uid).setValue(userDetailsMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Write Success", "User details written successfully.");
                        listener.onSuccess();
                    } else {
                        Log.e("Write Error", "Failed to write user details: " + Objects.requireNonNull(task.getException()).getMessage());
                        listener.onError("Failed to write user details.");
                    }
                });
    }

    public static String getCurrentDateFormatted() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    public interface OnRegisterCompleteListener {
        void onSuccess();
        void onError(String message);
    }
}

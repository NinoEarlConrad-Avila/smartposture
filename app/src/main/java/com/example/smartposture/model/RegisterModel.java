package com.example.smartposture.model;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;

public class RegisterModel {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    public RegisterModel() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
    }

    public void registerUser(String username, String email, String password, String firstname, String lastname,
                             String birthdate, RegisterResultCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String userID = user.getUid();
                            User newUser = new User(username, firstname, lastname, birthdate);
                            writeUserDetails(newUser, userID);
                            callback.onSuccess();
                        } else {
                            callback.onError("Failed to get user information.");
                        }
                    } else {
                        String errorMessage = task.getException().getMessage();
                        callback.onError("Registration failed: " + errorMessage);
                    }
                });
    }

    public static String getCurrentDateFormatted() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    private void writeUserDetails(User userDetails, String uid) {
        Log.d("User id", uid);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("username", userDetails.getUsername());
        userInfo.put("firstname", userDetails.getFirstname());
        userInfo.put("lastname", userDetails.getLastname());
        userInfo.put("birthdate", userDetails.getBirthdate());

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
                        Log.d("Write success", "User details written successfully.");
                    } else {
                        Log.e("Write error", "Failed to write user details: " + task.getException().getMessage());
                    }
                });
    }

    public interface RegisterResultCallback {
        void onSuccess();
        void onError(String message);
    }
}

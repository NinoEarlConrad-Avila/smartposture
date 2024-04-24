package com.example.smartposture.model;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterModel {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    public RegisterModel() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
    }

    public void registerUser(String username, String email, String password, String birthdate, RegisterResultCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String userID = user.getUid();
                            User newUser = new User(username, birthdate); // Assuming User class constructor includes username
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

    private void writeUserDetails(User userDetails, String uid) {
        Log.d("User id", uid);

        mDatabase.child(uid).setValue(userDetails)
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

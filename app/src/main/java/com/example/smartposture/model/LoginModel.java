package com.example.smartposture.model;


import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginModel {
    private FirebaseAuth mAuth;

    public LoginModel() {
        mAuth = FirebaseAuth.getInstance();
    }

    public void loginUser(String email, String password, LoginResultCallback callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            DatabaseReference database = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
                            database.child("username").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String userName = dataSnapshot.getValue(String.class);
                                    callback.onSuccess(userName != null ? userName : "No Name");
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    callback.onError("Failed to fetch user data");
                                }
                            });
                        } else {
                            callback.onError("User is not logged in");
                        }
                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Login failed";
                        callback.onError(errorMessage);
                    }
                });
    }

    public interface LoginResultCallback {
        void onSuccess(String userName);
        void onError(String message);
    }
}

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
    private final FirebaseAuth mAuth;

    public LoginModel() {
        mAuth = FirebaseAuth.getInstance();
    }

    public void loginUser(String email, String password, LoginResultCallback callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            fetchUserData(user.getUid(), callback);
                        } else {
                            callback.onError("User is not logged in");
                        }
                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Login failed";
                        callback.onError(errorMessage);
                    }
                });
    }

    private void fetchUserData(String userId, LoginResultCallback callback) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("users").child(userId).child("info");

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("username").getValue(String.class);
                    String firstname = dataSnapshot.child("firstname").getValue(String.class);
                    String lastname = dataSnapshot.child("lastname").getValue(String.class);
                    String userType = dataSnapshot.child("usertype").getValue(String.class);
                    String birthdate = dataSnapshot.child("birthdate").getValue(String.class);

                    UserModel user = new UserModel(username, firstname, lastname, birthdate, userType);
                    callback.onSuccess(user);
                } else {
                    callback.onError("No data found for user");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError("Failed to fetch user data: " + databaseError.getMessage());
            }
        });
    }

    public interface LoginResultCallback {
        void onSuccess(UserModel user);
        void onError(String message);
    }
}

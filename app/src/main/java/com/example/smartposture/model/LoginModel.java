package com.example.smartposture.model;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
                        callback.onSuccess();
                    } else {
                        String errorMessage = task.getException().getMessage();
                        callback.onError("Incorrect username or password");
                    }
                });
    }

    public interface LoginResultCallback {
        void onSuccess();
        void onError(String message);
    }
}

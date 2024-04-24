package com.example.smartposture.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.smartposture.model.LoginModel;

public class LoginViewModel extends ViewModel {
    private LoginModel userRepository;

    public LoginViewModel() {
        userRepository = new LoginModel();
    }

    public void loginUser(String email, String password, LoginModel.LoginResultCallback callback) {
        userRepository.loginUser(email, password, callback);
    }
}

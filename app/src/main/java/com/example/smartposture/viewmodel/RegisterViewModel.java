package com.example.smartposture.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.smartposture.model.RegisterModel;

import java.util.Date;

public class RegisterViewModel extends ViewModel {
    private RegisterModel userRepository;

    public RegisterViewModel() {
        userRepository = new RegisterModel();
    }

    public void registerUser(String username, String email, String password, String firstname, String lastname, String birthdate, RegisterModel.RegisterResultCallback callback) {
        userRepository.registerUser(username, email, password, firstname, lastname, birthdate, callback);
    }
}

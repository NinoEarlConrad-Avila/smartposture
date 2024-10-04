package com.example.smartposture.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartposture.model.RegisterModel;

public class RegisterViewModel extends ViewModel {
    private final RegisterModel registerModel;
    private final MutableLiveData<String> registrationResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> registrationSuccess = new MutableLiveData<>();

    public RegisterViewModel() {
        registerModel = new RegisterModel();
    }

    public void registerUser(String username, String email, String password, String firstname, String lastname, String birthdate) {
        registerModel.registerUser(username, email, password, firstname, lastname, birthdate, new RegisterModel.OnRegisterCompleteListener() {
            @Override
            public void onSuccess() {
                registrationSuccess.setValue(true);
                registrationResult.setValue("Registration successful!");
            }

            @Override
            public void onError(String message) {
                registrationSuccess.setValue(false);
                registrationResult.setValue(message);
            }
        });
    }

    public LiveData<String> getRegistrationResult() {
        return registrationResult;
    }

    public LiveData<Boolean> isRegistrationSuccessful() {
        return registrationSuccess;
    }
}

package com.example.smartposture.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartposture.model.RegisterModel;
import com.example.smartposture.model.UserModel;

public class RegisterViewModel extends ViewModel {
    private final RegisterModel registerModel;
    private final MutableLiveData<UserModel> registrationResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> registrationSuccess = new MutableLiveData<>();
    private static final String PREFS_NAME = "UserDetails";
    public RegisterViewModel() {
        registerModel = new RegisterModel();
    }

    public void registerUser(String username, String email, String password, String firstname, String lastname, String birthdate) {
        registerModel.registerUser(username, email, password, firstname, lastname, birthdate, new RegisterModel.OnRegisterCompleteListener() {
            @Override
            public void onSuccess(UserModel user) {
                registrationSuccess.setValue(true);
                registrationResult.setValue(user);
            }

            @Override
            public void onError(String message) {
                registrationSuccess.setValue(false);
            }
        });
    }

    public void saveUserDetails(Context context, UserModel userModel) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("USER_NAME", userModel.getUsername());
        editor.putString("FIRST_NAME", userModel.getFirstname());
        editor.putString("LAST_NAME", userModel.getLastname());
        editor.putString("USER_TYPE", userModel.getUsertype());
        editor.putString("BIRTH_DATE", userModel.getBirthdate());
        editor.apply();
    }

    public LiveData<UserModel> getRegistrationResult() {
        return registrationResult;
    }

    public LiveData<Boolean> isRegistrationSuccessful() {
        return registrationSuccess;
    }
}

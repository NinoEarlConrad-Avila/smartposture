package com.example.smartposture.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartposture.model.LoginModel;

public class LoginViewModel extends ViewModel {
    private final LoginModel loginModel;
    private final MutableLiveData<String> loginResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>();

    public LoginViewModel() {
        loginModel = new LoginModel();
    }

    public void loginUser(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            loginResult.setValue("Email and password must not be empty.");
            loginSuccess.setValue(false);
            return;
        }

        loginModel.loginUser(email, password, new LoginModel.LoginResultCallback() {
            @Override
            public void onSuccess(String userName) {
                loginSuccess.setValue(true);
                loginResult.setValue(userName);
            }

            @Override
            public void onError(String message) {
                loginSuccess.setValue(false);
                loginResult.setValue(message);
            }
        });
    }

    public LiveData<String> getLoginResult() {
        return loginResult;
    }

    public LiveData<Boolean> isLoginSuccessful() {
        return loginSuccess;
    }
}

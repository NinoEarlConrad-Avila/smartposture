package com.example.smartposture.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartposture.model.LoginModel;
import com.example.smartposture.model.UserModel;
import com.example.smartposture.model.UserModelMetaData;

public class LoginViewModel extends ViewModel {
    private final LoginModel loginModel;
    private final MutableLiveData<UserModel> loginResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>();

    public LoginViewModel() {
        loginModel = new LoginModel();
    }

    public void loginUser(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            loginSuccess.setValue(false);
            return;
        }

        loginModel.loginUser(email, password, new LoginModel.LoginResultCallback() {

            @Override
            public void onSuccess(UserModel user) {
                loginSuccess.setValue(true);
                loginResult.setValue(user);
            }

            @Override
            public void onError(String message) {
                loginSuccess.setValue(false);
            }
        });
    }

    public LiveData<UserModel> getLoginResult() {
        return loginResult;
    }

    public LiveData<Boolean> isLoginSuccessful() {
        return loginSuccess;
    }
}

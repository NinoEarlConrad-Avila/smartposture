// RegisterViewModel.java
package com.example.smartposture.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartposture.model.UserModel;
import com.example.smartposture.model.UserModelMetaData;
import com.example.smartposture.repository.RegisterRepository;

public class RegisterViewModel extends ViewModel {

    private final RegisterRepository registerRepository;
    private static final String PREFS_NAME = "UserDetails";

    public RegisterViewModel() {
        registerRepository = new RegisterRepository();
    }

    public LiveData<UserModelMetaData> registerUser(UserModelMetaData userModel) {
        return null;
    }

    public void saveUserDetails(Context context, UserModelMetaData user) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("EMAIL", user.getEmail());
        editor.putString("USER_NAME", user.getUser_metadata().getUsername());
        editor.putString("FIRST_NAME", user.getUser_metadata().getFirstname());
        editor.putString("LAST_NAME", user.getUser_metadata().getLastname());
        editor.putString("USER_TYPE", user.getUser_metadata().getUsertype());
        editor.putString("BIRTH_DATE", user.getUser_metadata().getBirthdate());
        editor.apply();
    }
}

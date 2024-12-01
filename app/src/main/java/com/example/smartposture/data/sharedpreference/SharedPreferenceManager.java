package com.example.smartposture.data.sharedpreference;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.smartposture.data.model.User;

public class SharedPreferenceManager {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_SESSION_TOKEN = "sessionToken";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_FIRST_NAME = "firstname";
    private static final String KEY_LAST_NAME = "lastname";
    private static final String KEY_BIRTH_DATE = "birthdate";
    private static final String KEY_USER_TYPE = "user_type";

    private static SharedPreferenceManager instance;
    private SharedPreferences sharedPreferences;

    public SharedPreferenceManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPreferenceManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferenceManager(context.getApplicationContext());
        }
        return instance;
    }
    public void saveSessionData(User user) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_USER_ID, user.getUser_id());
        editor.putString(KEY_USERNAME, user.getUsername());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_FIRST_NAME, user.getFirstname());
        editor.putString(KEY_LAST_NAME, user.getLastname());
        editor.putString(KEY_BIRTH_DATE, user.getBirthdate());
        editor.putString(KEY_USER_TYPE, user.getUsertype());
        editor.putString(KEY_SESSION_TOKEN, user.getToken());
        editor.apply();
    }

    public String getSessionToken() {
        return sharedPreferences.getString(KEY_SESSION_TOKEN, null);
    }

    public int getUserId() {
        return sharedPreferences.getInt(KEY_USER_ID, -1);
    }

    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, null);
    }

    public String getUserType() {
        return sharedPreferences.getString(KEY_USER_TYPE, null);
    }

    public User getUserDetails() {
        int userId = getUserId();
        String username = getUsername();
        String email = sharedPreferences.getString(KEY_EMAIL, null);
        String firstname = sharedPreferences.getString(KEY_FIRST_NAME, null);
        String lastname = sharedPreferences.getString(KEY_LAST_NAME, null);
        String birthdate = sharedPreferences.getString(KEY_BIRTH_DATE, null);
        String userType = getUserType();
        String token = getSessionToken();

        if (username != null && !username.isEmpty()) {
            return new User(userId, username, email, firstname, lastname, birthdate, userType, token);
        }
        return null;
    }


    public void clearSession() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}

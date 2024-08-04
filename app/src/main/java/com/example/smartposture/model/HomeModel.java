package com.example.smartposture.model;

import android.content.Context;
import android.content.SharedPreferences;

public class HomeModel {
    private static final String PREFS_NAME = "smart_posture_prefs";
    private static final String KEY_PUSHUP_COUNT = "pushup_count";
    // for counting pushups (can be removed)
    private static final String KEY_SQUAT_COUNT = "squat_count";
    // for counting squats (can be removed)

    private SharedPreferences sharedPreferences;

    public HomeModel(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    public int getPushupCount() {
        return sharedPreferences.getInt(KEY_PUSHUP_COUNT, 0);
    }

    public void setPushupCount(int count) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_PUSHUP_COUNT, count);
        editor.apply();
    }
    public int getSquatCount() {
        return sharedPreferences.getInt(KEY_SQUAT_COUNT, 0);
    }
    public void setSquatCount(int count) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_SQUAT_COUNT, count);
        editor.apply();
    }

}

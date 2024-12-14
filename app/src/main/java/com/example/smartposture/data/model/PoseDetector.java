package com.example.smartposture.data.model;

import android.content.Context;
import android.content.SharedPreferences;

public class PoseDetector {
    private static final String PREFS_NAME = "smart_posture_prefs";

    private SharedPreferences sharedPreferences;

    public PoseDetector(Context context){
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}

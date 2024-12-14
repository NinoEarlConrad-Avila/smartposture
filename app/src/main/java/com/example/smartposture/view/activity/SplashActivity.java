package com.example.smartposture.view.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartposture.R;
import com.example.smartposture.data.sharedpreference.SharedPreferenceManager;
import com.example.smartposture.viewmodel.AuthViewModel;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_SCREEN_DELAY = 2000;
    private LinearLayout splashScreen;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        splashScreen = findViewById(R.id.splash);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        splashScreen.startAnimation(fadeIn);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        authViewModel.getSessionValid().observe(this, isSessionValid -> {
            if (Boolean.TRUE.equals(isSessionValid)) {
                navigateToMainActivity();
            } else {
                navigateToLoginActivity();
            }
        });

        authViewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null) {
                Log.d("Error", errorMessage);
                navigateToLoginActivity();
            }
        });

        new Handler().postDelayed(() -> {
            String token = new SharedPreferenceManager(this).getSessionToken();
            authViewModel.validateSessionToken(token);
        }, SPLASH_SCREEN_DELAY);
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    private void navigateToLoginActivity() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }
}

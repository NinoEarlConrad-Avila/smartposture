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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartposture.R;
import com.example.smartposture.viewmodel.SplashViewModel;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_SCREEN_DELAY = 2000;
    private LinearLayout splashScreen;
    private SplashViewModel splashViewModel;

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

        splashViewModel = new ViewModelProvider(this).get(SplashViewModel.class);

        splashViewModel.getSessionValidLiveData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isSessionValid) {
                if (isSessionValid != null) {
                    if (isSessionValid) {
                        navigateToMainActivity();
                    } else {
                        navigateToLoginActivity();
                    }
                }
            }
        });

        splashViewModel.getErrorMessageLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String errorMessage) {
                if (errorMessage != null) {
                    Log.d("Error", errorMessage);
                    navigateToLoginActivity();
                }
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                splashViewModel.validateSessionToken();
            }
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

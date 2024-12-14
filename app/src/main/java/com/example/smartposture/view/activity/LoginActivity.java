package com.example.smartposture.view.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartposture.R;
import com.example.smartposture.data.model.User;
import com.example.smartposture.data.sharedpreference.SharedPreferenceManager;
import com.example.smartposture.viewmodel.AuthViewModel;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private TextView registerTextView;

    private AuthViewModel authViewModel;
    private Animation animation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        initViews();
        setupViewModel();
        setupListeners();
    }

    private void initViews() {
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.btnLogin);
        registerTextView = findViewById(R.id.txtSignUp);

        ImageView logo = findViewById(R.id.logo);
        animation = AnimationUtils.loadAnimation(this, R.anim.logo_bounce);
        logo.startAnimation(animation);
    }

    private void setupViewModel() {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        authViewModel.isLoginSuccessful().observe(this, success -> {
            if (success != null && success) {
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
            }
        });

        authViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        authViewModel.getLoggedInUser().observe(this, this::handleLoginSuccess);
    }

    private void setupListeners() {
        loginButton.setOnClickListener(v -> validateAndLogin());

        registerTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    private void validateAndLogin() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
        } else {
            authViewModel.loginUser(username, password);
        }
    }

    private void handleLoginSuccess(User user) {
        if (user != null) {
            saveUserDetails(user);
            navigateToMain();
        }
    }

    private void saveUserDetails(User user) {
        SharedPreferenceManager spManager = SharedPreferenceManager.getInstance(this);
        spManager.saveSessionData(user);
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }
}

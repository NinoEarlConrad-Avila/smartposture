package com.example.smartposture.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartposture.R;
import com.example.smartposture.data.model.User;
import com.example.smartposture.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private LoginViewModel loginViewModel;

    private TextView registerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.btnLogin);
        registerTextView = findViewById(R.id.txtSignUp);

        registerTextView.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Initialize ViewModel
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        setupObservers();

//         Handle login button click
        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
            } else {
                loginViewModel.loginUser(username, password);
            }
        });
    }

    private void setupObservers() {
        // Observe login success
        loginViewModel.isLoginSuccessful().observe(this, success -> {
            if (success != null && success) {
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
            }
        });

        // Observe error messages
        loginViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        // Observe logged-in user details
        loginViewModel.getLoggedInUser().observe(this, user -> {
            if (user != null) {
                saveUserDetails(user);
                navigateToMain(user);
            }
        });
    }

    private void saveUserDetails(User user) {
        getSharedPreferences("user_data", MODE_PRIVATE)
                .edit()
                .putString("username", user.getUsername())
                .putString("email", user.getEmail())
                .putString("firstname", user.getFirstname())
                .putString("lastname", user.getLastname())
                .putString("usertype", user.getUsertype())
                .putString("birthdate", user.getBirthdate())
                .apply();
    }

    private void navigateToMain(User user) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
        finish();
    }
}

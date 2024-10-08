package com.example.smartposture.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import com.example.smartposture.R;
import com.example.smartposture.model.UserModel;
import com.example.smartposture.viewmodel.LoginViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;
    private LoginViewModel loginVM;
    private static final String PREFS_NAME = "UserDetails";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.user);
        passwordEditText = findViewById(R.id.pass);
        TextView forgotPassword = findViewById(R.id.clickhere);
        Button loginButton = findViewById(R.id.btnLogin);
        Button loginAsGuestButton = findViewById(R.id.guest);
        loginVM = new ViewModelProvider(this).get(LoginViewModel.class);

        TextView signUpTextView = findViewById(R.id.textSignUp);
        signUpTextView.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        setupObservers();

        loginButton.setOnClickListener(view -> {
            String email = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            loginVM.loginUser(email, password);
        });

        loginAsGuestButton.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("USER_NAME", "Guest");
            intent.putExtra("isGuest", true);
            startActivity(intent);
            finish();
        });

        forgotPassword.setOnClickListener(view -> showForgotPasswordDialog());
    }

    private void setupObservers() {
        loginVM.getLoginResult().observe(this, user -> {
            if (user != null) {
                saveUserDetails(user);
                Toast.makeText(LoginActivity.this, "Login successful: " + user.getUsername(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        loginVM.isLoginSuccessful().observe(this, success -> {
            if (success != null && !success) {
                Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToMain(UserModel userModel) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("USER_NAME", userModel.getUsername());
        startActivity(intent);
        finish();
    }

    private void showForgotPasswordDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_forgot_password, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        EditText emailInput = dialogView.findViewById(R.id.email_input);
        Button resetButton = dialogView.findViewById(R.id.reset_password_button);

        AlertDialog dialog = builder.create();
        dialog.show();

        resetButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            if (!email.isEmpty()) {
                sendPasswordResetEmail(email);
                dialog.dismiss();
            } else {
                emailInput.setError("Email is required");
            }
        });
    }

    private void sendPasswordResetEmail(String email) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Reset email sent", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserDetails(UserModel userModel) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("USER_NAME", userModel.getUsername());
        editor.putString("FIRST_NAME", userModel.getFirstname());
        editor.putString("LAST_NAME", userModel.getLastname());
        editor.putString("USER_TYPE", userModel.getUsertype());
        editor.putString("BIRTH_DATE", userModel.getBirthdate());
        editor.apply();
    }

}

package com.example.smartposture.view.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartposture.R;
import com.example.smartposture.data.model.User;
import com.example.smartposture.data.sharedpreference.SharedPreferenceManager;
import com.example.smartposture.viewmodel.AuthViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    private EditText emailEditText, usernameEditText, firstNameEditText, lastNameEditText, passwordEditText, confirmPasswordEditText;
    private TextInputEditText birthDateEditText;
    private TextView signInTextView;
    private AuthViewModel authViewModel;
    private final Calendar myCalendar = Calendar.getInstance();
    private Button registerButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        usernameEditText = findViewById(R.id.username);
        firstNameEditText = findViewById(R.id.firstName);
        lastNameEditText = findViewById(R.id.lastName);
        birthDateEditText = findViewById(R.id.birthDate);
        confirmPasswordEditText = findViewById(R.id.confirmPassword);
        signInTextView = findViewById(R.id.txt_login);
        registerButton = findViewById(R.id.button);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        setupObservers();

        signInTextView.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        birthDateEditText.setOnClickListener(view -> new DatePickerDialog(RegisterActivity.this,
                this::onDateSet, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show());
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String username = usernameEditText.getText().toString().trim();
                String firstname = firstNameEditText.getText().toString().trim();
                String lastname = lastNameEditText.getText().toString().trim();
                String birthdate = Objects.requireNonNull(birthDateEditText.getText()).toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();
                Log.d("RegisterActivity", "Password: " +password +"\nConfirm Password: " +confirmPassword);

                if (email.isEmpty() || username.isEmpty() || firstname.isEmpty() || lastname.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || birthdate.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                } else {
                    authViewModel.registerUser(email, password, username, firstname, lastname, birthdate, "trainee");
                }
            }
        });
    }

    private void setupObservers() {
        // Observe login success
        authViewModel.isLoginSuccessful().observe(this, success -> {
            if (success != null && success) {
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
            }
        });

        // Observe error messages
        authViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        // Observe logged-in user details
        authViewModel.getLoggedInUser().observe(this, user -> {
            if (user != null) {
                saveUserDetails(user);
                navigateToMain(user);
            }
        });
    }


    private void onDateSet(DatePicker view, int year, int month, int day) {
        myCalendar.set(Calendar.YEAR, year);
        myCalendar.set(Calendar.MONTH, month);
        myCalendar.set(Calendar.DAY_OF_MONTH, day);
        updateBirthDateEditText();
    }
    private void updateBirthDateEditText() {
        String myFormat = "yyyy/MM/dd"; // Updated to full year format
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        birthDateEditText.setText(dateFormat.format(myCalendar.getTime()));
    }

    private void navigateToMain(User user) {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
        finish();
    }

    private void saveUserDetails(User user) {
        SharedPreferenceManager spManager = SharedPreferenceManager.getInstance(this);
        spManager.saveSessionData(user);
    }
}
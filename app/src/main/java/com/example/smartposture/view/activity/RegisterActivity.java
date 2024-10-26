package com.example.smartposture.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartposture.R;
import com.example.smartposture.viewmodel.RegisterViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    private EditText emailEditText;
    private EditText usernameEditText;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private TextInputEditText birthDateEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private TextView signInTextView;
    private RegisterViewModel registerVM;
    private final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameEditText = findViewById(R.id.username);
        emailEditText = findViewById(R.id.email);
        firstNameEditText = findViewById(R.id.firstName);
        lastNameEditText = findViewById(R.id.lastName);
        birthDateEditText = findViewById(R.id.birthDate);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirmPassword);
        signInTextView = findViewById(R.id.txt_login);
        Button registerButton = findViewById(R.id.button);
        registerVM = new ViewModelProvider(this).get(RegisterViewModel.class);

        setupObservers();

        birthDateEditText.setOnClickListener(view -> new DatePickerDialog(RegisterActivity.this,
                this::onDateSet, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show());

        registerButton.setOnClickListener(view -> handleRegistration());

        signInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupObservers() {
        registerVM.getRegistrationResult().observe(this, user -> {
            if (user != null) {
                registerVM.saveUserDetails(this, user);
                Log.d("RegisterActivity", "Logged User: " + user.getUsername());
                String message = "User registered: " + user.getUsername();
                Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });

        registerVM.isRegistrationSuccessful().observe(this, success -> {
            if (success != null && success) {
                registerVM.getRegistrationResult().observe(this, registeredUser -> {
                    if (registeredUser != null) {
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        intent.putExtra("USER_NAME", registeredUser.getUsername());
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
    }



    private void onDateSet(DatePicker view, int year, int month, int day) {
        myCalendar.set(Calendar.YEAR, year);
        myCalendar.set(Calendar.MONTH, month);
        myCalendar.set(Calendar.DAY_OF_MONTH, day);
        updateBirthDateEditText();
    }

    private void handleRegistration() {
        String email = emailEditText.getText().toString();
        String username = usernameEditText.getText().toString();
        String firstname = firstNameEditText.getText().toString();
        String lastname = lastNameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();
        String birthdate = Objects.requireNonNull(birthDateEditText.getText()).toString();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || birthdate.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(confirmPassword)) {
            Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
        } else {
            registerVM.registerUser(username, email, password, firstname, lastname, birthdate);
        }
    }

    private void updateBirthDateEditText() {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        birthDateEditText.setText(dateFormat.format(myCalendar.getTime()));
    }
}

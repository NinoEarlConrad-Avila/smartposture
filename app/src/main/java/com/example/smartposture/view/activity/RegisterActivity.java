package com.example.smartposture.view.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartposture.R;
import com.example.smartposture.model.UserModel;
import com.example.smartposture.model.UserModelMetaData;
import com.example.smartposture.viewmodel.RegisterViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    private EditText emailEditText, usernameEditText, firstNameEditText, lastNameEditText, passwordEditText, confirmPasswordEditText;
    private TextInputEditText birthDateEditText;
    private TextView signInTextView;
    private RegisterViewModel registerVM;
    private final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        registerVM = new ViewModelProvider(this).get(RegisterViewModel.class);

        birthDateEditText.setOnClickListener(view -> new DatePickerDialog(RegisterActivity.this,
                this::onDateSet, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show());

        Button registerButton = findViewById(R.id.button);
        registerButton.setOnClickListener(view -> handleRegistration());

        signInTextView.setOnClickListener(view -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private void initViews() {
        emailEditText = findViewById(R.id.email);
        usernameEditText = findViewById(R.id.username);
        firstNameEditText = findViewById(R.id.firstName);
        lastNameEditText = findViewById(R.id.lastName);
        birthDateEditText = findViewById(R.id.birthDate);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirmPassword);
        signInTextView = findViewById(R.id.txt_login);
    }

    private void onDateSet(DatePicker view, int year, int month, int day) {
        myCalendar.set(Calendar.YEAR, year);
        myCalendar.set(Calendar.MONTH, month);
        myCalendar.set(Calendar.DAY_OF_MONTH, day);
        updateBirthDateEditText();
    }

    private void handleRegistration() {
        String email = emailEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();
        String firstname = firstNameEditText.getText().toString().trim();
        String lastname = lastNameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        String birthdate = Objects.requireNonNull(birthDateEditText.getText()).toString().trim();

        if (email.isEmpty() || username.isEmpty() || firstname.isEmpty() || lastname.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || birthdate.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create UserModel with Metadata
        UserModelMetaData.Metadata metadata = new UserModelMetaData.Metadata(username, firstname, lastname, birthdate, "trainee"); // Replace "UserType" as required
        UserModelMetaData newUser = new UserModelMetaData(email, password, metadata);

        registerVM.registerUser(newUser).observe(this, user -> {
            if (user != null) {
                // Save user details locally
                registerVM.saveUserDetails(RegisterActivity.this, user);

                // Log success
                Log.d("RegisterActivity", "Registered User: " + user.getUser_metadata().getUsername());
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();

                // Navigate to MainActivity
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                intent.putExtra("USER_NAME", user.getUser_metadata().getUsername());
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateBirthDateEditText() {
        String myFormat = "MM/dd/yyyy"; // Updated to full year format
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        birthDateEditText.setText(dateFormat.format(myCalendar.getTime()));
    }
}

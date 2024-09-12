package com.example.smartposture.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartposture.Home;
import com.example.smartposture.MainActivity;
import com.example.smartposture.R;
import com.example.smartposture.model.RegisterModel;
import com.example.smartposture.viewmodel.RegisterViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {
    private EditText emailEditText;
    private EditText usernameEditText;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText birthdateEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button registerButton;
    private RegisterViewModel registerVM;
    private TextView signInTextView;
    final Calendar myCalendar= Calendar.getInstance();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameEditText = findViewById(R.id.username);
        emailEditText = findViewById(R.id.email);
        firstNameEditText = findViewById(R.id.firstName);
        lastNameEditText = findViewById(R.id.lastName);
        birthdateEditText = (EditText) findViewById(R.id.birthDate);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirmPassword);
        registerButton = findViewById(R.id.button);
        registerVM = new ViewModelProvider(this).get(RegisterViewModel.class);

        signInTextView = findViewById(R.id.txtlogin);
        EditText editText;

        signInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString();
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();
                String birthdate = birthdateEditText.getText().toString();

                if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || birthdate.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                } else {
                    registerVM.registerUser(username, email, password, birthdate, new RegisterModel.RegisterResultCallback() {
                        @Override
                        public void onSuccess() {
                            startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                            finish();
                        }

                        @Override
                        public void onError(String message) {
                            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

        });
        birthdateEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Check if the touch event is ACTION_UP and within the bounds of the drawableEnd
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (birthdateEditText.getRight() - birthdateEditText.getCompoundDrawables()[2].getBounds().width())) {
                        showDatePickerDialog(birthdateEditText);
                        return true; // Handle the touch event
                    }
                }
                return false; // Pass the touch event further
            }
        });

}
    private void showDatePickerDialog(EditText birthdateEditText) {
        // Get the current date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create and show the DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterActivity.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Set the selected date on the EditText
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    birthdateEditText.setText(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }
}
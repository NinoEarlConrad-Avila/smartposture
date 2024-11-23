//package com.example.smartposture.view.activity;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.lifecycle.ViewModelProvider;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//import androidx.appcompat.app.AlertDialog;
//
//import com.example.smartposture.R;
//import com.example.smartposture.model.UserModel;
//import com.example.smartposture.model.UserModelMetaData;
//import com.example.smartposture.viewmodel.LoginViewModel;
//import com.google.firebase.auth.FirebaseAuth;
//
//import java.util.Objects;
//
//public class LoginActivity extends AppCompatActivity {
//    private EditText usernameEditText;
//    private EditText passwordEditText;
//    private LoginViewModel loginVM;
//    private static final String PREFS_NAME = "UserDetails";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//
//        usernameEditText = findViewById(R.id.user);
//        passwordEditText = findViewById(R.id.pass);
//        TextView forgotPassword = findViewById(R.id.clickhere);
//        Button loginButton = findViewById(R.id.btnLogin);
//        Button loginAsGuestButton = findViewById(R.id.guest);
//        loginVM = new ViewModelProvider(this).get(LoginViewModel.class);
//
//        TextView signUpTextView = findViewById(R.id.textSignUp);
//        signUpTextView.setOnClickListener(view -> {
//            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
//            startActivity(intent);
//        });
//
//        setupObservers();
//
//        loginButton.setOnClickListener(view -> {
//            String email = usernameEditText.getText().toString().trim();
//            String password = passwordEditText.getText().toString().trim();
//            loginVM.loginUser(email, password);
//        });
//
//        loginAsGuestButton.setOnClickListener(view -> {
//            clearUserDetails();
//
//            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//            intent.putExtra("USER_NAME", "Guest");
//            intent.putExtra("isGuest", true);
//            startActivity(intent);
//            finish();
//        });
//
//        forgotPassword.setOnClickListener(view -> showForgotPasswordDialog());
//    }
//
//    private void setupObservers() {
//        loginVM.getLoginResult().observe(this, user -> {
//            if (user != null) {
////                saveUserDetails(user);
////                Toast.makeText(LoginActivity.this, "Login successful: " + user.getUser_metadata().getUsername(), Toast.LENGTH_SHORT).show();
//
//                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });
//
//        loginVM.isLoginSuccessful().observe(this, success -> {
//            if (success != null && !success) {
//                Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//    private void clearUserDetails() {
//        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.clear(); // Clears all stored data
//        editor.apply();
//    }
//
//    private void navigateToMain(UserModelMetaData userModel) {
//        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//        intent.putExtra("USER_NAME", userModel.getUser_metadata().getUsername());
//        startActivity(intent);
//        finish();
//    }
//
//    private void showForgotPasswordDialog() {
//        LayoutInflater inflater = getLayoutInflater();
//        View dialogView = inflater.inflate(R.layout.dialog_forgot_password, null);
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setView(dialogView);
//
//        EditText emailInput = dialogView.findViewById(R.id.email_input);
//        Button resetButton = dialogView.findViewById(R.id.reset_password_button);
//
//        AlertDialog dialog = builder.create();
//        dialog.show();
//
//        resetButton.setOnClickListener(v -> {
//            String email = emailInput.getText().toString().trim();
//            if (!email.isEmpty()) {
//                sendPasswordResetEmail(email);
//                dialog.dismiss();
//            } else {
//                emailInput.setError("Email is required");
//            }
//        });
//    }
//
//    private void sendPasswordResetEmail(String email) {
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        auth.sendPasswordResetEmail(email)
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        Toast.makeText(LoginActivity.this, "Reset email sent", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(LoginActivity.this, "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//
//    private void saveUserDetails(UserModelMetaData userModel) {
//        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//
//        // Get the Metadata object
//        UserModelMetaData.Metadata metadata = userModel.getUser_metadata();
//        if (metadata != null) {
//            editor.putString("USER_NAME", metadata.getUsername());
//            editor.putString("FIRST_NAME", metadata.getFirstname());
//            editor.putString("LAST_NAME", metadata.getLastname());
//            editor.putString("USER_TYPE", metadata.getUsertype());
//            editor.putString("BIRTH_DATE", metadata.getBirthdate());
//        }
//
//        editor.apply();
//    }
//
//}

package com.example.smartposture.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartposture.R;
import com.example.smartposture.data.api.ApiClient;
import com.example.smartposture.data.api.ApiService;
import com.example.smartposture.data.model.User;
import com.example.smartposture.data.request.LoginRequest;
import com.example.smartposture.data.response.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText edtUsername, edtPassword;
    private Button btnLogin, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        edtUsername = findViewById(R.id.user);
        edtPassword = findViewById(R.id.pass);
        btnLogin = findViewById(R.id.btnLogin);
//        btnRegister = findViewById(R.id.btnRegister);

        // Login button click listener
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtUsername.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
                } else {
                    login(username, password);
                }
            }
        });

        // Register button click listener
//        btnRegister.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
//                startActivity(intent);
//            }
//        });
    }

    private void login(String username, String password) {
        // Initialize API service
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        LoginRequest loginRequest = new LoginRequest(username, password);

        // Make API call
        apiService.loginUser(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    // Show success message
                    Toast.makeText(LoginActivity.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();

                    // Save user details and navigate to MainActivity
                    User user = loginResponse.getUser();
                    saveUserDetails(user);

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("user", user); // Optional: Pass user details to the next activity
                    startActivity(intent);
                    finish();
                } else {
                    // Handle invalid credentials
                    Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // Handle API call failure
                Log.d("LoginActivity", "Error: " + t.getMessage());
                Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Save user details in SharedPreferences
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
}



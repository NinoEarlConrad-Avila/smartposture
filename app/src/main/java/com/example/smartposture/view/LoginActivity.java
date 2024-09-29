package com.example.smartposture.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import com.example.smartposture.Home;
import com.example.smartposture.R;
import com.example.smartposture.model.LoginModel;
import com.example.smartposture.viewmodel.LoginViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private LoginViewModel loginVM;
    private TextView signUpTextView;

    private Button loginAsGuestButton;
    private TextView forgotPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.user);
        passwordEditText = findViewById(R.id.pass);
        forgotPassword = findViewById(R.id.clickhere);
        loginButton = findViewById(R.id.btnLogin);
        loginAsGuestButton = findViewById(R.id.guest);
        loginVM = new ViewModelProvider(this).get(LoginViewModel.class);

        signUpTextView = findViewById(R.id.textSignUp);
        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if(email.isEmpty() && password.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Input email and password", Toast.LENGTH_SHORT).show();
                }else if(email.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Input email", Toast.LENGTH_SHORT).show();
                }else if(password.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Input password", Toast.LENGTH_SHORT).show();
                }
                else {
                    loginVM.loginUser(email, password, new LoginModel.LoginResultCallback() {
                        @Override
                        public void onSuccess(String userName) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("USER_NAME", userName);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onError(String message) {
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        loginAsGuestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                intent.putExtra("exer", "pushup");
                intent.putExtra("USER_NAME", "Guest");
                startActivity(intent);
                finish();
            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotPasswordDialog();
            }
        });
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
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Reset email sent", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}



package com.example.smartposture.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartposture.Home;
import com.example.smartposture.R;
import com.example.smartposture.model.LoginModel;
import com.example.smartposture.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private LoginViewModel loginVM;
    private TextView signUpTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.user);
        passwordEditText = findViewById(R.id.pass);
        loginButton = findViewById(R.id.btnLogin);
        loginVM = new ViewModelProvider(this).get(LoginViewModel.class);

        signUpTextView = findViewById(R.id.txtSignUp);
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
                            Intent intent = new Intent(LoginActivity.this, Home.class);
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
    }
}



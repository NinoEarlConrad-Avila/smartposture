package com.example.smartposture;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        TextView userNameTextView = findViewById(R.id.userNameTextView);
        String userName = getIntent().getStringExtra("USER_NAME");
        userNameTextView.setText("Hello, " +userName);
    }
}

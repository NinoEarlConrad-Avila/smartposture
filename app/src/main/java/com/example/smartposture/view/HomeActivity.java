package com.example.smartposture.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartposture.R;
import com.example.smartposture.viewmodel.HomeViewModel;

public class HomeActivity extends AppCompatActivity {
    private ImageButton pushupImgBtn;
    private ImageButton squatImgBtn;
    private TextView pushupCount;
    private TextView squatCount;
    private HomeViewModel homeViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        pushupImgBtn = findViewById(R.id.imgBtnPushup);
        squatImgBtn = findViewById(R.id.imgBtnSquat);
        pushupCount = findViewById(R.id.pushupCount);
        squatCount = findViewById(R.id.squatsCount);

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        pushupImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, PoseDetectorActivity.class);
                intent.putExtra("exer", "pushup");
                startActivity(intent);
            }
        });

        squatImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, PoseDetectorActivity.class);
                intent.putExtra("exer", "squat");
                startActivity(intent);
            }
        });

        homeViewModel.getPushupCount().observe(this, newCount -> {
            pushupCount.setText(String.valueOf(newCount));
        });

        homeViewModel.getSquatCount().observe(this, newCount -> {
            squatCount.setText(String.valueOf(newCount));
        });

    }
}
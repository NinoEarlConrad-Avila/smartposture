package com.example.smartposture.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartposture.R;
import com.example.smartposture.viewmodel.HomeViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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

        homeViewModel.getPushupCount().observe(this, newCount -> {
            pushupCount.setText(String.valueOf(newCount));
        });

        homeViewModel.getSquatCount().observe(this, newCount -> {
            squatCount.setText(String.valueOf(newCount));
        });

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            homeViewModel.fetchStats(currentUser.getUid());
        }
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

        homeViewModel.getSquatCount().observe(this, newCount -> {
            squatCount.setText(String.valueOf(newCount));
            homeViewModel.updateSquatCountInFirebase(newCount);
        });

        homeViewModel.getPushupCount().observe(this, newCount -> {
            pushupCount.setText(String.valueOf(newCount));
            homeViewModel.updatePushupCountInFirebase(newCount);
        });
    }
}
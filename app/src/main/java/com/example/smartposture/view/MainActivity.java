package com.example.smartposture.view;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.example.smartposture.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment(), false, addBundle());
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                loadFragment(new HomeFragment(), false, addBundle());
                return true;
            } else if (item.getItemId() == R.id.nav_room) {
                loadFragment(new SelectRoomFragment(), false, addBundle());
                return true;
            } else if (item.getItemId() == R.id.nav_workout) {
                loadFragment(new WorkoutFragment(), false, addBundle());
                return true;
            } else if (item.getItemId() == R.id.nav_profile) {
                loadFragment(new ProfileFragment(), false, addBundle());
                return true;
            }else{
                return false;
            }
        });
    }

    private void loadFragment(Fragment fragment, boolean addToBackStack, Bundle bundle) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (bundle != null) {
            fragment.setArguments(bundle);
        }

        transaction.replace(R.id.container, fragment);

        if (addToBackStack) {
            transaction.addToBackStack(fragment.getClass().getSimpleName());
        }
        transaction.commit();
    }

    private Bundle addBundle(){
        Bundle bundle = new Bundle();
        boolean isGuest = getIntent().getBooleanExtra("isGuest", false);

        bundle.putBoolean("isGuest", isGuest);
        bundle.putString("USER_NAME", getIntent().getStringExtra("USER_NAME"));
        return bundle;
    }
}

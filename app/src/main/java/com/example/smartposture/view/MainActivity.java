package com.example.smartposture.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.smartposture.R;
import com.example.smartposture.model.UserModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "UserDetails";
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
            } else {
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

    private Bundle addBundle() {
        Bundle bundle = new Bundle();
        boolean isGuest = getIntent().getBooleanExtra("isGuest", false);

        bundle.putBoolean("isGuest", isGuest);
        bundle.putString("USER_NAME", getIntent().getStringExtra("USER_NAME"));
        return bundle;
    }

    public static UserModel getUserDetails(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("USER_NAME", null);
        String firstname = sharedPreferences.getString("FIRST_NAME", null);
        String lastname = sharedPreferences.getString("LAST_NAME", null);
        String userType = sharedPreferences.getString("USER_TYPE", null);
        String birthdate = sharedPreferences.getString("BIRTH_DATE", null);

        return new UserModel(username, firstname, lastname, birthdate, userType);
    }
}

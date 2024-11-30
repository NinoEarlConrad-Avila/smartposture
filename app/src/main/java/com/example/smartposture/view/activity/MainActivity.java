package com.example.smartposture.view.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.smartposture.R;
import com.example.smartposture.data.model.User;
import com.example.smartposture.model.UserModel;
import com.example.smartposture.view.fragment.HomeFragment;
import com.example.smartposture.view.fragment.ProfileFragment;
import com.example.smartposture.view.fragment.SelectRoomFragment;
import com.example.smartposture.view.fragment.WorkoutFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "UserDetails";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment(), false, addBundle());
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                loadFragment(new HomeFragment(), false, addBundle());
                return true;
            } else if (item.getItemId() == R.id.nav_room) {
                if (isGuestUser()) {
                    showSnackbar("Guest users cannot access this feature.");
                    return false; // Prevent navigation
                }
                loadFragment(new SelectRoomFragment(), false, addBundle());
                return true;
            } else if (item.getItemId() == R.id.nav_workout) {
                loadFragment(new WorkoutFragment(), false, addBundle());
                return true;
            } else if (item.getItemId() == R.id.nav_profile) {
                if (isGuestUser()) {
                    showSnackbar("Guest users cannot access this feature.");
                    return false; // Prevent navigation
                }
                loadFragment(new ProfileFragment(), false, addBundle());
                return true;
            } else {
                return false;
            }
        });
    }

    public void loadFragment(Fragment fragment, boolean addToBackStack, Bundle bundle) {
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

    private boolean isGuestUser() {
        boolean isGuest = getIntent().getBooleanExtra("isGuest", false);
        Log.d("MainActivity", "Is guest user: " + isGuest);
        return isGuest;
    }

    private void showSnackbar(String message) {
        // Use the container view for the Snackbar
        Snackbar snackbar = Snackbar.make(findViewById(R.id.container), message, Snackbar.LENGTH_SHORT);
        // Optionally, customize the Snackbar view
//        snackbar.setAction("OK", v -> snackbar.dismiss());
        snackbar.show();
    }

//    public static UserModel getUserDetails(Context context) {
//        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
//        String username = sharedPreferences.getString("USER_NAME", null);
//        String firstname = sharedPreferences.getString("FIRST_NAME", null);
//        String lastname = sharedPreferences.getString("LAST_NAME", null);
//        String userType = sharedPreferences.getString("USER_TYPE", null);
//        String birthdate = sharedPreferences.getString("BIRTH_DATE", null);
//
//        return new UserModel(username, firstname, lastname, birthdate, userType);
//    }

//    public static User getNewUserDetails(Context context) {
//        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
//        int user_id = sharedPreferences.getInt("USER_ID", 0);
//        String email = sharedPreferences.getString("EMAIL", null);
//        String userType = sharedPreferences.getString("USER_TYPE", null);
//
//        return new User(user_id, email, userType);
//    }

    public static void saveUserDetails(Context context, User user) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("USER_ID", user.getUser_id());
        editor.putString("USERNAME", user.getUsername());
        editor.putString("EMAIL", user.getEmail());
        editor.putString("FIRSTNAME", user.getFirstname());
        editor.putString("LASTNAME", user.getLastname());
        editor.putString("BIRTHDATE", user.getBirthdate());
        editor.putString("USER_TYPE", user.getUsertype());
        editor.apply();
    }

    public static User getUserDetails(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("USER_ID", 0);
        String username = sharedPreferences.getString("USERNAME", null);
        String email = sharedPreferences.getString("EMAIL", null);
        String firstname = sharedPreferences.getString("FIRSTNAME", null);
        String lastname = sharedPreferences.getString("LASTNAME", null);
        String birthdate = sharedPreferences.getString("BIRTHDATE", null);
        String userType = sharedPreferences.getString("USER_TYPE", null);

        return new User(userId, email, username, firstname, lastname, birthdate, userType);
    }
}

package com.example.smartposture.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartposture.R;
import com.example.smartposture.data.sharedpreference.SharedPreferenceManager;
import com.example.smartposture.view.activity.LoginActivity;
import com.example.smartposture.viewmodel.ProfileViewModel;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    private SharedPreferenceManager spManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        spManager = SharedPreferenceManager.getInstance(requireContext());

        String token = spManager.getSessionToken();

        profileViewModel.getLogoutResult().observe(getViewLifecycleOwner(), logoutResponse -> {
            if (logoutResponse.isSuccess()) {
                spManager.clearSession();
                Toast.makeText(requireContext(), logoutResponse.getMessage(), Toast.LENGTH_SHORT).show();
                navigateToLogin();
            } else {
                Toast.makeText(requireContext(), logoutResponse.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        Button logoutButton = view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> profileViewModel.logoutUser(token));

        return view;
    }

    private void navigateToLogin() {
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}

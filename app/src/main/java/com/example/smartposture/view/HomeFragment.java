package com.example.smartposture.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.smartposture.R;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        TextView username = view.findViewById(R.id.txtUsername);
        ImageView notification = view.findViewById(R.id.notification);

        if (getArguments() != null && getArguments().getBoolean("isGuest", false)) {
            username.setText("Guest");
        } else {
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
            String loggedUser = sharedPreferences.getString("USER_NAME", "");
            username.setText(loggedUser);
        }

        notification.setOnClickListener(v -> {
            NotificationFragment notif = new NotificationFragment();

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, notif)
                    .addToBackStack("Notification")
                    .commit();
        });
        return view;

    }


}

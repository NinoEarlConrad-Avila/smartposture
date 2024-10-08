package com.example.smartposture.view;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.smartposture.R;
import com.example.smartposture.model.UserModel;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        TextView username = view.findViewById(R.id.txtUsername);
        ImageView notification = view.findViewById(R.id.notification);

        UserModel user = MainActivity.getUserDetails(requireContext());
        if (user != null && user.getUsername() != null) {
            username.setText(user.getUsername());
        } else {
            username.setText("Guest");
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

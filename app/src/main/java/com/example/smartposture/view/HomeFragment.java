package com.example.smartposture.view;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

        ImageView notification = view.findViewById(R.id.notification);

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

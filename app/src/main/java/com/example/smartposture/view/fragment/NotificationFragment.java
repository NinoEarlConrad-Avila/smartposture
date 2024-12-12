package com.example.smartposture.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.smartposture.R;
import com.example.smartposture.data.adapter.NotificationAdapter;
import com.example.smartposture.data.sharedpreference.SharedPreferenceManager;
import com.example.smartposture.view.activity.MainActivity;
import com.example.smartposture.viewmodel.NotificationViewModel;

import java.util.ArrayList;

public class NotificationFragment extends BaseFragment {
    private NotificationViewModel notificationViewModel;
    private NotificationAdapter notificationAdapter;
    private RelativeLayout layoutPreLoader, layoutNoNotifications;
    private ImageView preloaderImage;
    private Animation bounceAnimation;
    private RecyclerView notificationRecyclerView;
    private SharedPreferenceManager spManager;
    private String notificationType;
    private ImageButton backButton;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        notificationType = requireArguments().getString("notification_type", "");

        if (getActivity() != null) {
            ((MainActivity) getActivity()).setBottomNavVisibility(View.GONE);
        }

        spManager = getSharedPreferenceManager();

        layoutPreLoader = view.findViewById(R.id.preloaderLayout);
        preloaderImage = view.findViewById(R.id.preloaderImage);
        layoutNoNotifications = view.findViewById(R.id.noNotification);
        notificationRecyclerView = view.findViewById(R.id.notificationRecyclerView);
        backButton = view.findViewById(R.id.backButton);
        bounceAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.logo_bounce);
        preloaderImage.startAnimation(bounceAnimation);

        notificationViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
        notificationAdapter = new NotificationAdapter(new ArrayList<>());

        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        notificationRecyclerView.setAdapter(notificationAdapter);

        observeViewModel();
        if(notificationType.equals("user")){
            int userId = spManager.getUserId();
            fetchUserNotifications(userId);
        }else if(notificationType.equals("room")){
            int roomId = requireArguments().getInt("room_id", -1);
            fetchRoomNotifications(roomId);
        }

        backButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });
        return view;
    }

    private void observeViewModel() {
        notificationViewModel.getLoadingState().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                layoutPreLoader.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });

        notificationViewModel.getUserNotifications().observe(getViewLifecycleOwner(), notifications -> {
            if (notifications != null && !notifications.isEmpty()) {
                notificationAdapter = new NotificationAdapter(notifications);
                notificationRecyclerView.setAdapter(notificationAdapter);
                notificationRecyclerView.setVisibility(View.VISIBLE);
                layoutNoNotifications.setVisibility(View.GONE);
            } else {
                notificationRecyclerView.setVisibility(View.GONE);
                layoutNoNotifications.setVisibility(View.VISIBLE);
            }
        });

        notificationViewModel.getRoomNotifications().observe(getViewLifecycleOwner(), notifications -> {
            if (notifications != null && !notifications.isEmpty()) {
                notificationAdapter = new NotificationAdapter(notifications);
                notificationRecyclerView.setAdapter(notificationAdapter);
                notificationRecyclerView.setVisibility(View.VISIBLE);
                layoutNoNotifications.setVisibility(View.GONE);
            } else {
                notificationRecyclerView.setVisibility(View.GONE);
                layoutNoNotifications.setVisibility(View.VISIBLE);
            }
        });
    }

    private void fetchUserNotifications(int userId) {
        notificationViewModel.fetchUserNotification(userId);
    }

    private void fetchRoomNotifications(int roomId) {
        notificationViewModel.fetchRoomNotification(roomId);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() != null) {
            if (notificationType.equals("room")){
                ((MainActivity) getActivity()).setBottomNavVisibility(View.GONE);
            }else if (notificationType.equals("user")){
                ((MainActivity) getActivity()).setBottomNavVisibility(View.VISIBLE);
            }
        }
    }
}

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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartposture.R;
import com.example.smartposture.data.adapter.WorkoutAdapter;
import com.example.smartposture.data.sharedpreference.SharedPreferenceManager;
import com.example.smartposture.view.activity.MainActivity;
import com.example.smartposture.viewmodel.WorkoutViewModel;

import java.util.ArrayList;

public class HomeFragment extends BaseFragment {

    private WorkoutViewModel workoutsViewModel;
    private WorkoutAdapter workoutAdapter;
    private TextView usernameTextView;
    private RelativeLayout layoutPreLoader, layoutNoWorkouts;
    private ImageView preloaderImage, notification;
    private Animation bounceAnimation;
    private SharedPreferenceManager spManager;
    private RecyclerView workoutRecyclerView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        usernameTextView = view.findViewById(R.id.txtUsername);
        workoutRecyclerView = view.findViewById(R.id.workoutRecyclerView);
        layoutPreLoader = view.findViewById(R.id.preloaderLayout);
        preloaderImage = view.findViewById(R.id.preloaderImage);
        layoutNoWorkouts = view.findViewById(R.id.noWorkout);
        notification = view.findViewById(R.id.notification);

        bounceAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.logo_bounce);

        spManager = getSharedPreferenceManager();
        setUsername();

        workoutAdapter = new WorkoutAdapter(new ArrayList<>(), this::navigateToWorkoutDetails);
        workoutRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        workoutRecyclerView.setAdapter(workoutAdapter);

        workoutsViewModel = new ViewModelProvider(this).get(WorkoutViewModel.class);

        showPreloader();
        observeViewModel();

        notification.setOnClickListener(v -> navigateToNotification());

        workoutsViewModel.fetchWorkouts();

        return view;
    }

    private void setUsername() {
        String username = spManager.getUsername();
        usernameTextView.setText(username != null ? username : "Guest");
        Log.d("HomeFragment", "USER ID: " + spManager.getUserId());
    }

    private void observeViewModel() {
        workoutsViewModel.getWorkoutsLiveData().observe(getViewLifecycleOwner(), workouts -> {
            if (workouts != null && !workouts.isEmpty()) {
                workoutAdapter.setWorkouts(workouts);
                workoutRecyclerView.setVisibility(View.VISIBLE);
                layoutNoWorkouts.setVisibility(View.GONE);
            } else {
                workoutRecyclerView.setVisibility(View.GONE);
                layoutNoWorkouts.setVisibility(View.VISIBLE);
            }
            hidePreloader();
        });

        workoutsViewModel.getLoadingState().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                workoutRecyclerView.setVisibility(View.GONE);
                layoutNoWorkouts.setVisibility(View.GONE);
                showPreloader();
            } else {
                hidePreloader();
            }
        });

        workoutsViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), errorMessage -> {
            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            hidePreloader();
            workoutRecyclerView.setVisibility(View.GONE);
            layoutNoWorkouts.setVisibility(View.VISIBLE);
        });
    }

    private void showPreloader() {
        layoutPreLoader.setVisibility(View.VISIBLE);
        preloaderImage.startAnimation(bounceAnimation);
    }

    private void hidePreloader() {
        layoutPreLoader.setVisibility(View.GONE);
        preloaderImage.clearAnimation();
    }

    private void navigateToWorkoutDetails(int workoutId) {
        Bundle bundle = new Bundle();
        bundle.putInt("workout_id", workoutId);

        WorkoutDetailFragment fragment = new WorkoutDetailFragment();
        fragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setBottomNavVisibility(View.VISIBLE);
        }
    }

    private void navigateToNotification() {
        Bundle bundle = new Bundle();
        bundle.putString("notification_type", "user");
        bundle.putString("exer", "squat");

        NotificationFragment fragment = new NotificationFragment();
        fragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack("HomeFragment")
                .commit();
    }
}

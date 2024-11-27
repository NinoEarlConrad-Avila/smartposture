package com.example.smartposture.view.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartposture.R;
import com.example.smartposture.data.adapter.WorkoutAdapter;
import com.example.smartposture.data.model.User;
import com.example.smartposture.view.activity.MainActivity;
import com.example.smartposture.viewmodel.WorkoutViewModel;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private WorkoutViewModel workoutsViewModel;
    private WorkoutAdapter workoutAdapter;
    private TextView usernameTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        usernameTextView = view.findViewById(R.id.txtUsername);
        RecyclerView workoutRecyclerView = view.findViewById(R.id.workoutRecyclerView);

        User user = MainActivity.getNewUserDetails(requireContext());
        if (user != null && user.getUsername() != null) {
            usernameTextView.setText(user.getUsername());
        } else {
            usernameTextView.setText("Guest");
        }

        workoutAdapter = new WorkoutAdapter(new ArrayList<>(), workoutId -> navigateToWorkoutDetails(workoutId));
        workoutRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        workoutRecyclerView.setAdapter(workoutAdapter);

        workoutsViewModel = new ViewModelProvider(this).get(WorkoutViewModel.class);

        workoutsViewModel.getWorkoutsLiveData().observe(getViewLifecycleOwner(), workouts -> {
            if (workouts != null && !workouts.isEmpty()) {
                workoutAdapter.setWorkouts(workouts);
            } else {
                Toast.makeText(getContext(), "No workouts available.", Toast.LENGTH_SHORT).show();
            }
        });

        workoutsViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), errorMessage -> {
            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
        });

        workoutsViewModel.fetchWorkouts();

        return view;
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
}

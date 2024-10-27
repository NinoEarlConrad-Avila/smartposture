package com.example.smartposture.view.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.smartposture.R;
import com.example.smartposture.adapter.HomeActivitiesAdapter;
import com.example.smartposture.adapter.HomeWorkoutsAdapter;
import com.example.smartposture.model.ActivityModel;
import com.example.smartposture.model.UserModel;
import com.example.smartposture.model.WorkoutModel;
import com.example.smartposture.view.activity.MainActivity;
import com.example.smartposture.viewmodel.HomeActivitiesViewModel;
import com.example.smartposture.viewmodel.HomeWorkoutsViewModel;

import java.util.List;

public class HomeFragment extends Fragment {

    private HomeActivitiesViewModel activitiesViewModel;
    private HomeWorkoutsViewModel workoutsViewModel;
    private HomeActivitiesAdapter activitiesAdapter;
    private HomeWorkoutsAdapter workoutsAdapter;
    private TextView noActivitiesTextView;
    private RecyclerView activitiesRecyclerView;
    private LinearLayout activitiesLayout; // Added to handle visibility when userType is null

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        TextView username = view.findViewById(R.id.txtUsername);
        ImageView notification = view.findViewById(R.id.notification);
        activitiesRecyclerView = view.findViewById(R.id.activitiesRecyclerView);
        RecyclerView workoutRecyclerView = view.findViewById(R.id.workoutRecyclerView);
        noActivitiesTextView = view.findViewById(R.id.noActivities);
        activitiesLayout = view.findViewById(R.id.activitiesLinearLayout); // Reference to the LinearLayout

        UserModel user = MainActivity.getUserDetails(requireContext());
        String usertype = user != null ? user.getUsertype() : null;

        if (user != null && user.getUsername() != null) {
            username.setText(user.getUsername());
        } else {
            username.setText("Guest");
        }

        // Initialize ViewModels
        activitiesViewModel = new ViewModelProvider(this).get(HomeActivitiesViewModel.class);
        workoutsViewModel = new ViewModelProvider(this).get(HomeWorkoutsViewModel.class);

        activitiesAdapter = new HomeActivitiesAdapter();
        // Setup RecyclerViews
        activitiesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        workoutRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        activitiesRecyclerView.setAdapter(activitiesAdapter);

        // Observe activities data
        activitiesViewModel.getActivities().observe(getViewLifecycleOwner(), new Observer<List<ActivityModel>>() {
            @Override
            public void onChanged(List<ActivityModel> updatedActivities) {
                // Clear the previous activities and set the new ones
                displayActivities(usertype, updatedActivities); // Call to update UI based on new data
            }
        });


        // Observe workouts data
        workoutsViewModel.getWorkouts().observe(getViewLifecycleOwner(), new Observer<List<WorkoutModel>>() {
            @Override
            public void onChanged(List<WorkoutModel> workoutModels) {
                workoutsAdapter = new HomeWorkoutsAdapter(workoutModels);
                workoutRecyclerView.setAdapter(workoutsAdapter);
            }
        });

        // Handle visibility based on user type
        if (usertype == null) {
            activitiesLayout.setVisibility(View.GONE); // Hide activities if userType is null
        } else {
            activitiesLayout.setVisibility(View.VISIBLE); // Show activities otherwise
        }

        return view;
    }

    private void displayActivities(String userType, List<ActivityModel> activeActivities) {
        if (activeActivities == null || activeActivities.isEmpty()) {
            // Show a message when no active activities are found
            noActivitiesTextView.setVisibility(View.VISIBLE);
            noActivitiesTextView.setText("No active activities found.");
            activitiesRecyclerView.setVisibility(View.GONE);
        } else {
            // Populate the RecyclerView with the list of active activities
            noActivitiesTextView.setVisibility(View.GONE);
            activitiesRecyclerView.setVisibility(View.VISIBLE);
            activitiesAdapter.setActivities(activeActivities); // Update adapter with new activities
        }
    }
}

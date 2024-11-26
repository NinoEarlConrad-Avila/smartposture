//package com.example.smartposture.view.fragment;
//
//import android.os.Bundle;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.lifecycle.Observer;
//import androidx.lifecycle.ViewModelProvider;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.example.smartposture.R;
//import com.example.smartposture.adapter.HomeActivitiesAdapter;
//import com.example.smartposture.adapter.HomeWorkoutsAdapter;
//import com.example.smartposture.data.model.User;
//import com.example.smartposture.model.ActivityModel;
//import com.example.smartposture.model.UserModel;
//import com.example.smartposture.model.WorkoutModel;
//import com.example.smartposture.view.activity.MainActivity;
//import com.example.smartposture.viewmodel.HomeActivitiesViewModel;
//import com.example.smartposture.viewmodel.HomeWorkoutsViewModel;
//
//import java.util.List;
//
//public class HomeFragment extends Fragment {
//
//    private HomeActivitiesViewModel activitiesViewModel;
//    private HomeWorkoutsViewModel workoutsViewModel;
//    private HomeActivitiesAdapter activitiesAdapter;
//    private HomeWorkoutsAdapter workoutsAdapter;
//    private TextView noActivitiesTextView, textActivities;
//    private RecyclerView activitiesRecyclerView;
//    private LinearLayout activitiesLayout; // Added to handle visibility when userType is null
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_home, container, false);
//
//        TextView username = view.findViewById(R.id.txtUsername);
//        ImageView notification = view.findViewById(R.id.notification);
//        RecyclerView workoutRecyclerView = view.findViewById(R.id.workoutRecyclerView);
//
//        User user = MainActivity.getNewUserDetails(requireContext());
//        String usertype = user != null ? user.getUsertype() : null;
//
//        if (user != null && user.getUsername() != null) {
//            username.setText(user.getUsername());
//        } else {
//            username.setText("Guest");
//        }
//        Log.d("Test", "Usertype: " +usertype);
//        if(usertype.equals("trainee")){
//            textActivities.setText("Today's Activities");
//        }else{
//            textActivities.setText("Active Activities");
//        }
////        // Initialize ViewModels
////        workoutsViewModel = new ViewModelProvider(this).get(HomeWorkoutsViewModel.class);
//
////        activitiesAdapter = new HomeActivitiesAdapter();
//        // Setup RecyclerViews
////        workoutRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
//
////        activitiesRecyclerView.setAdapter(activitiesAdapter);
//
//        // Observe activities data
////        activitiesViewModel.getActivities().observe(getViewLifecycleOwner(), new Observer<List<ActivityModel>>() {
////            @Override
////            public void onChanged(List<ActivityModel> updatedActivities) {
////                // Clear the previous activities and set the new ones
////                displayActivities(usertype, updatedActivities); // Call to update UI based on new data
////            }
////        });
//
//
//        // Observe workouts data
////        workoutsViewModel.getWorkouts().observe(getViewLifecycleOwner(), new Observer<List<WorkoutModel>>() {
////            @Override
////            public void onChanged(List<WorkoutModel> workoutModels) {
////                workoutsAdapter = new HomeWorkoutsAdapter(workoutModels);
////                workoutRecyclerView.setAdapter(workoutsAdapter);
////            }
////        });
//
//        return view;
//    }
//}

package com.example.smartposture.view.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

        // Initialize views
        usernameTextView = view.findViewById(R.id.txtUsername);
        RecyclerView workoutRecyclerView = view.findViewById(R.id.workoutRecyclerView);

        // Retrieve user details
        User user = MainActivity.getNewUserDetails(requireContext());
        if (user != null && user.getUsername() != null) {
            usernameTextView.setText(user.getUsername());
        } else {
            usernameTextView.setText("Guest");
        }

        // Initialize RecyclerView and Adapter
        workoutAdapter = new WorkoutAdapter(new ArrayList<>());
        workoutRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        workoutRecyclerView.setAdapter(workoutAdapter);

        // Initialize ViewModel
        workoutsViewModel = new ViewModelProvider(this).get(WorkoutViewModel.class);


        // Observe workouts data
        workoutsViewModel.getWorkoutsLiveData().observe(getViewLifecycleOwner(), workouts -> {
            if (workouts != null && !workouts.isEmpty()) {
                workoutAdapter.setWorkouts(workouts);
            } else {
                Toast.makeText(getContext(), "No workouts available.", Toast.LENGTH_SHORT).show();
            }
        });

        // Observe error messages
        workoutsViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), errorMessage -> {
            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
        });

        // Fetch workouts
        workoutsViewModel.fetchWorkouts();

        return view;
    }
}

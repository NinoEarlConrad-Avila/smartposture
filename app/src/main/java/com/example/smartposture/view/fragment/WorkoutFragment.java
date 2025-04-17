package com.example.smartposture.view.fragment;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.smartposture.R;
import com.example.smartposture.data.adapter.WorkoutAdapter;
import com.example.smartposture.data.model.Room;
import com.example.smartposture.view.activity.MainActivity;
import com.example.smartposture.viewmodel.WorkoutViewModel;

import java.util.ArrayList;
import java.util.List;

public class WorkoutFragment extends Fragment {

    private WorkoutViewModel workoutsViewModel;
    private WorkoutAdapter workoutAdapter;
    private RelativeLayout layoutPreLoader, layoutNoWorkouts;
    private ImageView preloaderImage;
    private Animation bounceAnimation;
    private RecyclerView workoutRecyclerView;
    private EditText searchWorkout;
    private List<Room> allRooms = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workout, container, false);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
            new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                }
            });
        searchWorkout = view.findViewById(R.id.searchWorkout);
        layoutPreLoader = view.findViewById(R.id.preloaderLayout);
        preloaderImage = view.findViewById(R.id.preloaderImage);
        layoutNoWorkouts = view.findViewById(R.id.noWorkout);
        workoutRecyclerView = view.findViewById(R.id.workoutRecyclerView);

        bounceAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.logo_bounce);

        workoutAdapter = new WorkoutAdapter(new ArrayList<>(), this::navigateToWorkoutDetails);
        workoutRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        workoutRecyclerView.setAdapter(workoutAdapter);

        workoutsViewModel = new ViewModelProvider(this).get(WorkoutViewModel.class);

        showPreloader();
        observeViewModel();

        workoutsViewModel.fetchWorkouts();

        searchWorkout.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String query = charSequence.toString().toLowerCase();

                workoutAdapter.filter(query);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        return view;
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

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setBottomNavVisibility(View.VISIBLE);
        }
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

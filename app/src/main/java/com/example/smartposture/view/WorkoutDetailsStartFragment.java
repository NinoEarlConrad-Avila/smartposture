package com.example.smartposture.view;

import com.bumptech.glide.Glide;
import com.example.smartposture.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class WorkoutDetailsStartFragment extends Fragment {
    private static final String ARG_CARD_TITLE = "CARD_TITLE";
    private static final String ARG_CARD_PATH = "CARD_PATH";
    private static final String ARG_CARD_ID = "CARD_ID";
    private static final String ARG_CARD_DESCRIPTION = "CARD_DESCRIPTION";
    private static final String ARG_CARD_GUIDE = "CARD_GUIDE";

    public static WorkoutDetailsStartFragment newInstance(String cardTitle, String cardPath, int cardId, String cardDescription, String cardGuide) {
        WorkoutDetailsStartFragment fragment = new WorkoutDetailsStartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CARD_TITLE, cardTitle);
        args.putString(ARG_CARD_PATH, cardPath);
        args.putInt(ARG_CARD_ID, cardId);
        args.putString(ARG_CARD_DESCRIPTION, cardDescription);
        args.putString(ARG_CARD_GUIDE, cardGuide);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workout_details_start, container, false);

        String cardTitle = getArguments().getString(ARG_CARD_TITLE);
        String cardPath = getArguments().getString(ARG_CARD_PATH);
        String cardId = getArguments().getString(ARG_CARD_ID);
        String cardDescription = getArguments().getString(ARG_CARD_DESCRIPTION);
        String cardGuide = getArguments().getString(ARG_CARD_GUIDE);


        TextView workoutTitleTextView = view.findViewById(R.id.workout_title);
        TextView workoutTopTitleTextView = view.findViewById(R.id.workout_top_title);
        TextView workoutDescription = view.findViewById(R.id.description);
        TextView workoutGuide = view.findViewById(R.id.guide);
        ImageView imageView = view.findViewById(R.id.workout_image);
        ImageButton backButton = view.findViewById(R.id.back_to_workout_fragment);
        Button startButton = view.findViewById(R.id.start_button);

        workoutTitleTextView.setText(cardTitle);
        workoutTopTitleTextView.setText(cardTitle);
        workoutDescription.setText(cardDescription);
        workoutGuide.setText(cardGuide);

        int resourceId = getResources().getIdentifier(cardPath, "drawable", requireActivity().getPackageName());
        if (resourceId != 0) {
            Glide.with(this)
                    .load(resourceId)
                    .into(imageView);
        } else {
            Glide.with(this)
                    .load(R.drawable.default_image)
                    .into(imageView);
        }

        backButton.setOnClickListener(v -> {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            Fragment workoutFragment = fragmentManager.findFragmentByTag("WorkoutFragment");

            if (workoutFragment instanceof WorkoutFragment) {
                ((WorkoutFragment) workoutFragment).enableWorkoutCards(); // Enable the workout cards
                Log.d("WorkoutDetailsStartFragment", "Enabling workout cards");
            } else {
                Log.d("WorkoutDetailsStartFragment", "WorkoutFragment not found or is not an instance");
            }

            requireActivity().getSupportFragmentManager().popBackStack();
        });




        startButton.setOnClickListener(v -> {
            PoseDetectorFragment poseDetector = new PoseDetectorFragment();

            Bundle args = new Bundle();
            String type = null;
            if (cardTitle.equals("Squats"))
                type = "squat";
            else if (cardTitle.equals("Push Up"))
                type = "pushup";

            args.putString("exer", type);

            poseDetector.setArguments(args);

            BottomNavigationView bottomNavigation = requireActivity().findViewById(R.id.bottom_navigation);
            bottomNavigation.setVisibility(View.GONE);

            requireActivity().getSupportFragmentManager().popBackStack("WorkoutDetailsStartFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, poseDetector)
                    .addToBackStack("PoseDetector")
                    .commit();
        });
        return view;
    }
}
package com.example.smartposture.view;

import com.bumptech.glide.Glide;
import com.example.smartposture.R;

import android.os.Bundle;
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

public class WorkoutDetailsStartFragment extends Fragment {
    private static final String ARG_CARD_TITLE = "CARD_TITLE";
    private static final String ARG_CARD_PATH = "CARD_PATH";
    private static final String ARG_CARD_ID = "CARD_ID";

    public static WorkoutDetailsStartFragment newInstance(String cardTitle, String cardPath, int cardId) {
        WorkoutDetailsStartFragment fragment = new WorkoutDetailsStartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CARD_TITLE, cardTitle);
        args.putString(ARG_CARD_PATH, cardPath);
        args.putInt(ARG_CARD_ID, cardId);
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

        TextView workoutTitleTextView = view.findViewById(R.id.workout_title);
        ImageView imageView = view.findViewById(R.id.workout_image);
        ImageButton backButton = view.findViewById(R.id.back_to_workout_fragment);
        Button startButton = view.findViewById(R.id.start_button);

        workoutTitleTextView.setText(cardTitle);

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
            // Pop the current fragment off the back stack, returning to WorkoutFragment
            requireActivity().getSupportFragmentManager().popBackStack();
        });

//        startButton.setOnClickListener(v -> {
//            // Create the new fragment instance
//            PoseDetectorActivity workoutInProgressFragment = new PoseDetectorActivity();
//
//            // Navigate to the next fragment
//            requireActivity().getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.container, workoutInProgressFragment)
//                    .addToBackStack("WorkoutInProgress")  // Add to back stack to support back navigation
//                    .commit();
//        });

        return view;
    }
}

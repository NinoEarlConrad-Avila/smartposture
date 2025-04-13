package com.example.smartposture.view.fragment;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartposture.data.sharedpreference.SharedPreferenceManager;
import com.example.smartposture.util.CustomGraph;
import com.example.smartposture.viewmodel.ActivityViewModel;
import com.example.smartposture.R;
import java.util.ArrayList;

public class WorkoutSummaryFragment extends BaseFragment {
    private TextView repCount, totalScore;
    private Button submit;
    private ActivityViewModel activityViewModel;
    private SharedPreferenceManager spManager;
    private boolean isGuest;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workout_summary, container, false);

        int activityWorkoutId = requireArguments().getInt("activity_workout_id", -1);
        int activityId = requireArguments().getInt("activity_id", -1);
        int goalRep = requireArguments().getInt("rep_goal", -1);
        spManager = getSharedPreferenceManager();
        int userId = spManager.getUserId();
        ArrayList<Float> floatList = (ArrayList<Float>) getArguments().getSerializable("floatList");

        repCount = view.findViewById(R.id.repCount);
        totalScore = view.findViewById(R.id.totalScore);
        submit = view.findViewById(R.id.submit);
        Button submit = view.findViewById(R.id.submit);
        ProgressBar loadingSpinner = view.findViewById(R.id.loadingSpinner);
        Log.d("Guest", ""+spManager.isGuestSession());
        if (getArguments() != null) {
            isGuest = getArguments().getBoolean("isGuest", false);
        }

        activityViewModel = new ViewModelProvider(this).get(ActivityViewModel.class);
        submit.setOnClickListener(v -> {
            if (userId == -1){
                navigateToHome();
            }
            else if (floatList != null && floatList.size() > 1) {
                ArrayList<Float> subList = new ArrayList<>(floatList.subList(1, floatList.size()));

                loadingSpinner.setVisibility(View.VISIBLE);
                submit.setEnabled(false);
                submit.setText("");

                activityViewModel.addTraineeScore(activityWorkoutId, userId, subList);
            } else {
                Log.e("WorkoutDetailFragment", "Insufficient data in floatList");
            }
        });

        activityViewModel.getTraineeScoreStatus().observe(getViewLifecycleOwner(), status -> {
            loadingSpinner.setVisibility(View.GONE);
            submit.setEnabled(true);
            if ("Success".equals(status)) {
                if (goalRep != -1){
                    navigateToActivityDetail(activityId);
                } else {
                    navigateToHome();
                }
            } else if (status != null) {
                Toast.makeText(requireContext(), "Failed to create activity: " + status, Toast.LENGTH_LONG).show();
            }
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                    }
                });


        if (floatList != null) {
            for (Float value : floatList) {
                Log.d("WorkoutSummaryFragment", "Value: " + value);
            }

            repCount.setText(String.valueOf(floatList.size()-1));
            totalScore.setText(String.valueOf(floatList.get(0)));

            CustomGraph.setGraphViewData(view, floatList, getContext());
        } else {
            Log.d("WorkoutSummaryFragment", "No data found in floatList.");
        }
        return view;
    }

    private void navigateToActivityDetail(int activityId) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

        fragmentManager.popBackStack("ActivityDetailFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);

        Bundle bundle = new Bundle();
        bundle.putInt("room_id", requireArguments().getInt("room_id", -1));
        bundle.putInt("activity_id", activityId);

        ActivityDetailsFragment summary = new ActivityDetailsFragment();
        summary.setArguments(bundle);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, summary)
                .commit();
    }

    private void navigateToHome(){
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        for (int i = 0; i < 3; i++) {
            if (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.popBackStack();
            }
        }
    }
}

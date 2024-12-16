package com.example.smartposture.view.fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartposture.R;
import com.example.smartposture.data.adapter.StepsAdapter;
import com.example.smartposture.util.AdditionalSpaceBottom;
import com.example.smartposture.view.activity.MainActivity;
import com.example.smartposture.viewmodel.WorkoutViewModel;

public class WorkoutDetailFragment extends BaseFragment {

    private WorkoutViewModel workoutViewModel;
    private ScrollView contentScrollView;
    private TextView workoutName, workoutDescription, repGoal;
    private ImageView workoutImage;
    private RecyclerView stepsRecyclerView;
    private Button startButton;
    private ImageButton backButton;
    private LinearLayout workoutGoals;
    private int workoutId, activityWorkoutId, repetition;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workout_detail, container, false);

        if (getActivity() != null) {
            ((MainActivity) getActivity()).setBottomNavVisibility(View.GONE);
        }
        workoutId = requireArguments().getInt("workout_id", -1);
        activityWorkoutId = requireArguments().getInt("activity_workout_id", -1);
        repetition = requireArguments().getInt("repetition", -1);

        // Initialize views
        workoutGoals = view.findViewById(R.id.workoutGoals);
        repGoal = view.findViewById(R.id.repetitionGoal);
        contentScrollView = view.findViewById(R.id.workoutDetailScrollView);
        workoutName = view.findViewById(R.id.workoutDetailName);
        workoutDescription = view.findViewById(R.id.workoutDetailDescription);
        workoutImage = view.findViewById(R.id.workoutDetailImage);
        stepsRecyclerView = view.findViewById(R.id.stepsRecyclerView);
        startButton = view.findViewById(R.id.startButton);
        backButton = view.findViewById(R.id.backButton);

        // Set up the loading dialog
        Dialog loadingDialog = createFullScreenLoadingDialog();
        loadingDialog.show();

        // Set up RecyclerView
        int spaceInPixelsBottom = getResources().getDimensionPixelSize(R.dimen.activityWorkoutsBottom);
        stepsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        workoutViewModel = new ViewModelProvider(this).get(WorkoutViewModel.class);
        if (repetition != -1 && activityWorkoutId != -1){
            workoutGoals.setVisibility(View.VISIBLE);
            repGoal.setText(String.valueOf(repetition));
            stepsRecyclerView.setNestedScrollingEnabled(false);
            stepsRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        } else {
            workoutGoals.setVisibility(View.GONE);
        }

        backButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        startButton.setOnClickListener(v -> navigateToPoseDetector(workoutName.getText().toString().toLowerCase()));

        // Get the workout ID from arguments and load data

        if (workoutId != -1) {
            // Observing the LiveData from the ViewModel
            workoutViewModel.fetchWorkoutDetail(workoutId);

            workoutViewModel.getWorkoutDetail().observe(getViewLifecycleOwner(), response -> {
                if (response != null && response != null) {
                    // Successfully fetched workout data
                    workoutName.setText(response.getName());
                    workoutDescription.setText(response.getDescription());

                    // Set image resource dynamically
                    int imageResourceId = getDrawableResourceId(requireContext(), response.getPath());
                    workoutImage.setImageResource(imageResourceId != 0 ? imageResourceId : R.drawable.default_image);

                    // Set up RecyclerView adapter for steps
                    StepsAdapter stepsAdapter = new StepsAdapter(response.getSteps());
                    stepsRecyclerView.setAdapter(stepsAdapter);
                    stepsRecyclerView.addItemDecoration(new AdditionalSpaceBottom(spaceInPixelsBottom));

                    // Hide loading dialog and show content
                    loadingDialog.dismiss();
                } else {
                    // Handle error case if no data is found
                    workoutName.setText("Error fetching details.");
                    workoutDescription.setText("");
                    loadingDialog.dismiss();
                }
            });
        }

        return view;
    }
    private Dialog createFullScreenLoadingDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_loading);
        dialog.setCancelable(false);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        ImageView preloaderImage = dialog.findViewById(R.id.preloaderImage);
        Animation bounceAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.logo_bounce);
        preloaderImage.startAnimation(bounceAnimation);

        return dialog;
    }

    private int getDrawableResourceId(Context context, String fileName) {
        return context.getResources().getIdentifier(fileName, "drawable", context.getPackageName());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setBottomNavVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setBottomNavVisibility(View.VISIBLE);
        }
    }

    private void navigateToPoseDetector(String name) {
        Bundle bundle = new Bundle();
        bundle.putString("exer", name);
        bundle.putInt("room_id", requireArguments().getInt("room_id", -1));
        bundle.putInt("activity_id", requireArguments().getInt("activity_id", -1));
        bundle.putInt("activity_workout_id", activityWorkoutId);
        bundle.putInt("rep_goal", repetition);

        PoseDetectorFragment fragment = new PoseDetectorFragment();
        fragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack("WorkoutDetailFragment")
                .commit();
    }
}

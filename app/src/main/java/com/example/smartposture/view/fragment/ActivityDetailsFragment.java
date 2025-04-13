package com.example.smartposture.view.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartposture.R;
import com.example.smartposture.data.adapter.ActivityWorkoutAdapter;
import com.example.smartposture.data.model.ActivityDetails;
import com.example.smartposture.data.model.ActivityWorkout;
import com.example.smartposture.data.sharedpreference.SharedPreferenceManager;
import com.example.smartposture.util.AdditionalSpaceBottom;
import com.example.smartposture.util.AdditionalSpaceTop;
import com.example.smartposture.util.CustomGraph;
import com.example.smartposture.view.activity.MainActivity;
import com.example.smartposture.viewmodel.ActivityViewModel;

import java.util.ArrayList;
import java.util.List;

public class ActivityDetailsFragment extends BaseFragment implements ActivityWorkoutAdapter.OnWorkoutClickListener  {
    private ActivityViewModel activityViewModel;
    private TextView title, description, deadline;
    private Button submit;
    private ImageView backButton;
    private RecyclerView workoutsRecyclerView;
    private ActivityWorkoutAdapter workoutAdapter;
    private SharedPreferenceManager spManager;
    private boolean isDialogShowing = false;
    private int partialSquatCount, parallelSquatCount, deepSquatCount, repetitionSubmittedCount;
    private float totalWorkoutScore;
    private LinearLayout viewStatistics;
    private int userId;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity_details, container, false);

        Dialog loadingDialog = createFullScreenLoadingDialog();
        loadingDialog.show();

        int activityId = requireArguments().getInt("activity_id", -1);
        int roomId = requireArguments().getInt("room_id", -1);
        spManager = getSharedPreferenceManager();
        userId = spManager.getUserId();
        String userType = spManager.getUserType();

        Log.d("Test IDs: " ,""+ activityId +" " +userId);
        title = view.findViewById(R.id.activityName);
        description = view.findViewById(R.id.description);
        deadline = view.findViewById(R.id.deadline);
        backButton = view.findViewById(R.id.backButton);
        submit = view.findViewById(R.id.submitButton);
        workoutsRecyclerView = view.findViewById(R.id.activityWorkoutRecyclerView);
        viewStatistics = view.findViewById(R.id.viewActivityStatistics);

        ProgressBar loadingSpinner = view.findViewById(R.id.loadingSpinner);

        activityViewModel = new ViewModelProvider(this).get(ActivityViewModel.class);

        int spaceInPixelsBottom = getResources().getDimensionPixelSize(R.dimen.activityWorkoutsBottom);
        int spaceInPixelsTop = getResources().getDimensionPixelSize(R.dimen.activityWorkoutsTop);
        workoutsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        workoutAdapter = new ActivityWorkoutAdapter(new ArrayList<>(), this, activityId, roomId, this, userType);
        workoutsRecyclerView.setAdapter(workoutAdapter);
        workoutsRecyclerView.addItemDecoration(new AdditionalSpaceBottom(spaceInPixelsBottom));
        workoutsRecyclerView.addItemDecoration(new AdditionalSpaceTop(spaceInPixelsTop));

        if (userType.equals("trainee")){
            viewStatistics.setVisibility(View.GONE);
        }
        
        backButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        viewStatistics.setOnClickListener(v -> {
           navigateToStatistics(roomId, activityId);
        });

        activityViewModel.getActivityDetails().observe(getViewLifecycleOwner(), response -> {
            if (response != null && response.getActivity() != null) {
                updateUI(response.getActivity());
                loadingDialog.dismiss();
            } else {
                loadingDialog.dismiss();
            }
        });

        if(userType.equals("trainer")){
            submit.setVisibility(View.GONE);
        }

        submit.setOnClickListener(v -> {
            loadingSpinner.setVisibility(View.VISIBLE);
            submit.setEnabled(false);
            submit.setText("");

            activityViewModel.submitActivity(activityId, userId);
            activityViewModel.getSubmitActivityStatus().observe(getViewLifecycleOwner(), status -> {
                if ("Success".equals(status)) {
                    Toast.makeText(requireContext(), "Activity Submitted", Toast.LENGTH_SHORT).show();

                    requireActivity().getSupportFragmentManager().popBackStack();
                } else {
                    submit.setEnabled(true);
                    submit.setText("Submit Activity");
                    Toast.makeText(requireContext(), status, Toast.LENGTH_SHORT).show();
                }
            });
        });

        if (activityId != -1) {
            activityViewModel.fetchActivityDetails(activityId, userId);
        }
        return view;
    }

    private void updateUI(ActivityDetails activityDetails) {
        title.setText(activityDetails.getTitle());
        description.setText(activityDetails.getDescription());
        deadline.setText(String.format("%s %s", activityDetails.getEnd_date(), activityDetails.getEnd_time()));
        if (activityDetails.getStatus().equals("Submitted")){
            submit.setEnabled(false);
            submit.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.green));
        }

        List<ActivityWorkout> workouts = activityDetails.getWorkouts();
        workoutAdapter.updateWorkouts(workouts);
        for (ActivityWorkout workout : workouts) {
            if(workout.getStatus() == 0){
                submit.setEnabled(false);
                submit.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.green));
                return;
            }
        }
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
            ((MainActivity) getActivity()).setBottomNavVisibility(View.GONE);
        }
    }

    @Override
    public void viewScore(int activityWorkoutId, int repetition) {
        CustomGraph.showWorkoutDialog(
                getViewLifecycleOwner(),
                requireContext(),
                activityWorkoutId,
                repetition,
                userId,
                activityViewModel
        );
    }

    private void navigateToStatistics(int roomId, int activityId) {
        Bundle bundle = new Bundle();
        bundle.putInt("room_id", roomId);
        bundle.putInt("activity_id", activityId);

        ActivityStatisticsFragment summary = new ActivityStatisticsFragment();
        summary.setArguments(bundle);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, summary)
                .addToBackStack("ActivityDetailsFragment")
                .commit();
    }
}

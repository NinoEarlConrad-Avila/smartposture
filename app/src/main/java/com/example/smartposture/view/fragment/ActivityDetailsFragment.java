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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.example.smartposture.view.activity.MainActivity;
import com.example.smartposture.viewmodel.ActivityViewModel;

import java.util.ArrayList;
import java.util.List;

public class ActivityDetailsFragment extends BaseFragment {
    private ActivityViewModel activityViewModel;
    private TextView title, description, deadline;
    private ImageView backButton;
    private RecyclerView workoutsRecyclerView;
    private ActivityWorkoutAdapter workoutAdapter;
    private SharedPreferenceManager spManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity_details, container, false);

        Dialog loadingDialog = createFullScreenLoadingDialog();
        loadingDialog.show();

        int activityId = requireArguments().getInt("activity_id", -1);
        spManager = getSharedPreferenceManager();
        int userId = spManager.getUserId();
        Log.d("Test IDs: " ,""+ activityId +" " +userId);
        title = view.findViewById(R.id.activityName);
        description = view.findViewById(R.id.description);
        deadline = view.findViewById(R.id.deadline);
        backButton = view.findViewById(R.id.backButton);
        workoutsRecyclerView = view.findViewById(R.id.activityWorkoutRecyclerView);

        activityViewModel = new ViewModelProvider(this).get(ActivityViewModel.class);

        int spaceInPixelsBottom = getResources().getDimensionPixelSize(R.dimen.activityWorkoutsBottom);
        int spaceInPixelsTop = getResources().getDimensionPixelSize(R.dimen.activityWorkoutsTop);
        workoutsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        workoutAdapter = new ActivityWorkoutAdapter(new ArrayList<>(), this);
        workoutsRecyclerView.setAdapter(workoutAdapter);
        workoutsRecyclerView.addItemDecoration(new AdditionalSpaceBottom(spaceInPixelsBottom));
        workoutsRecyclerView.addItemDecoration(new AdditionalSpaceTop(spaceInPixelsTop));

        backButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        activityViewModel.getActivityDetails().observe(getViewLifecycleOwner(), response -> {
            if (response != null && response.getActivity() != null) {
                updateUI(response.getActivity());
                loadingDialog.dismiss();
            } else {
                loadingDialog.dismiss();
            }
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

        List<ActivityWorkout> workouts = activityDetails.getWorkouts();
        workoutAdapter.updateWorkouts(workouts);
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
}

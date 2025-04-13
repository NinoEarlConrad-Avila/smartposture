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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartposture.R;
import com.example.smartposture.data.adapter.SubmissionWorkoutsAdapter;
import com.example.smartposture.data.model.ActivityWorkout;
import com.example.smartposture.data.model.SubmissionDetails;
import com.example.smartposture.data.sharedpreference.SharedPreferenceManager;
import com.example.smartposture.util.AdditionalSpaceBottom;
import com.example.smartposture.util.AdditionalSpaceTop;
import com.example.smartposture.util.CustomGraph;
import com.example.smartposture.view.activity.MainActivity;
import com.example.smartposture.viewmodel.ActivityViewModel;

import java.util.ArrayList;
import java.util.List;

public class SubmissionDetailFragment extends BaseFragment implements SubmissionWorkoutsAdapter.OnViewClickListener{
    private ActivityViewModel activityViewModel;
    private TextView user,partial, parallel, deep, totalSquats, averageScore, scoreClassification;
    private ImageView backButton;
    private RecyclerView workoutsRecyclerView;
    private SubmissionWorkoutsAdapter workoutAdapter;
    private SharedPreferenceManager spManager;
    private boolean isDialogShowing = false;
    private int traineeId;
    private LinearLayout viewStatistics;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_submission, container, false);

        Dialog loadingDialog = createFullScreenLoadingDialog();
        loadingDialog.show();

        int roomId = requireArguments().getInt("room_id", -1);
        int activityId = requireArguments().getInt("activity_id", -1);
        traineeId = requireArguments().getInt("trainee_id", -1);
        spManager = getSharedPreferenceManager();
        int userId = spManager.getUserId();
        String userType = spManager.getUserType();

        Log.d("Test IDs: " ,""+ activityId +" " +userId);
        user = view.findViewById(R.id.submissionUser);
        partial = view.findViewById(R.id.partialSquatCount);
        parallel = view.findViewById(R.id.parallelSquatCount);
        deep = view.findViewById(R.id.deepSquatCount);
        totalSquats = view.findViewById(R.id.overAllSquatCount);
        averageScore = view.findViewById(R.id.averageScore);
        scoreClassification = view.findViewById(R.id.scoreClassification);
        workoutsRecyclerView = view.findViewById(R.id.activityWorkoutsRecyclerView);
        backButton = view.findViewById(R.id.backButton);

        activityViewModel = new ViewModelProvider(this).get(ActivityViewModel.class);

        int spaceInPixelsBottom = getResources().getDimensionPixelSize(R.dimen.activityWorkoutsBottom);
        int spaceInPixelsTop = getResources().getDimensionPixelSize(R.dimen.activityWorkoutsTop);
        workoutsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        workoutAdapter = new SubmissionWorkoutsAdapter(new ArrayList<>(), this,this, activityId, roomId, traineeId);
        workoutsRecyclerView.setAdapter(workoutAdapter);
        workoutsRecyclerView.addItemDecoration(new AdditionalSpaceBottom(spaceInPixelsBottom));
        workoutsRecyclerView.addItemDecoration(new AdditionalSpaceTop(spaceInPixelsTop));

        if (userType.equals("trainee")){
            viewStatistics.setVisibility(View.GONE);
        }

        backButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        activityViewModel.getSubmissionDetail().observe(getViewLifecycleOwner(), response -> {
            if (response != null && response.getSubmission() != null) {
                updateUI(response.getSubmission());
                loadingDialog.dismiss();
            } else {
                loadingDialog.dismiss();
            }
        });

        if (activityId != -1 && traineeId != -1) {
            activityViewModel.fetchSubmissionDetails(activityId, traineeId);
        }
        return view;
    }

    private void updateUI(SubmissionDetails submissionDetails) {
        user.setText(spManager.getUsername());
        partial.setText(String.valueOf(submissionDetails.getCount_025()));
        parallel.setText(String.valueOf(submissionDetails.getCount_050()));
        deep.setText(String.valueOf(submissionDetails.getCount_100()));
        totalSquats.setText(String.valueOf((submissionDetails.getTotal_repetitions())));
        averageScore.setText(String.valueOf(submissionDetails.getAvg_score()*100));

        if(submissionDetails.getAvg_score()*100 >= 75.0){
            scoreClassification.setText("VERY GOOD");
            scoreClassification.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green));
        }
        else if(submissionDetails.getAvg_score()*100 >= 50.0 && submissionDetails.getAvg_score()*100 <75.0){
            scoreClassification.setText("GOOD");
            scoreClassification.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.submitted_late));
        }
        else {
            scoreClassification.setText("BAD");
            scoreClassification.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.no_submission));
        }
        List<ActivityWorkout> workouts = submissionDetails.getWorkouts();
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
    public void viewScore(int activityWorkoutId, int repetition) {
        CustomGraph.showWorkoutDialog(
                getViewLifecycleOwner(),
                requireContext(),
                activityWorkoutId,
                repetition,
                traineeId,
                activityViewModel
        );
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

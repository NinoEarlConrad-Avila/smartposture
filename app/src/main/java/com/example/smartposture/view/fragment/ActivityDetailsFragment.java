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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartposture.R;
import com.example.smartposture.data.adapter.ActivityWorkoutAdapter;
import com.example.smartposture.data.adapter.RoomAdapter;
import com.example.smartposture.data.model.ActivityDetails;
import com.example.smartposture.data.model.ActivityWorkout;
import com.example.smartposture.data.sharedpreference.SharedPreferenceManager;
import com.example.smartposture.util.AdditionalSpaceBottom;
import com.example.smartposture.util.AdditionalSpaceTop;
import com.example.smartposture.view.activity.MainActivity;
import com.example.smartposture.viewmodel.ActivityViewModel;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

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
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity_details, container, false);

        Dialog loadingDialog = createFullScreenLoadingDialog();
        loadingDialog.show();

        int activityId = requireArguments().getInt("activity_id", -1);
        int roomId = requireArguments().getInt("room_id", -1);
        spManager = getSharedPreferenceManager();
        int userId = spManager.getUserId();
        String userType = spManager.getUserType();

        Log.d("Test IDs: " ,""+ activityId +" " +userId);
        title = view.findViewById(R.id.activityName);
        description = view.findViewById(R.id.description);
        deadline = view.findViewById(R.id.deadline);
        backButton = view.findViewById(R.id.backButton);
        submit = view.findViewById(R.id.submitButton);
        workoutsRecyclerView = view.findViewById(R.id.activityWorkoutRecyclerView);
        ProgressBar loadingSpinner = view.findViewById(R.id.loadingSpinner);

        activityViewModel = new ViewModelProvider(this).get(ActivityViewModel.class);

        int spaceInPixelsBottom = getResources().getDimensionPixelSize(R.dimen.activityWorkoutsBottom);
        int spaceInPixelsTop = getResources().getDimensionPixelSize(R.dimen.activityWorkoutsTop);
        workoutsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        workoutAdapter = new ActivityWorkoutAdapter(new ArrayList<>(), this, activityId, roomId, this, userType);
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

    private void setGraphViewData(View view, ArrayList<Float> floatList) {
        // Get the GraphView from the layout
        GraphView graphView = view.findViewById(R.id.idGraphView);

        if (floatList != null && !floatList.isEmpty()) {
            // Create counters for each squat type
            partialSquatCount = 0;
            parallelSquatCount = 0;
            deepSquatCount = 0;
            totalWorkoutScore = 0;
            repetitionSubmittedCount = floatList.size();
            // Classify the squat types based on the float values
            for (int i = 0; i < floatList.size(); i++) {
                Float value = floatList.get(i);
                if (value <= 0.25) {
                    partialSquatCount++;
                } else if (value <= 0.5) {
                    parallelSquatCount++;
                } else {
                    deepSquatCount++;
                }
                totalWorkoutScore += value;
            }

            // Prepare data for the BarGraph (X: Squat Types, Y: Count)
            DataPoint[] dataPoints = new DataPoint[3];
            dataPoints[0] = new DataPoint(0, partialSquatCount);
            dataPoints[1] = new DataPoint(1, parallelSquatCount);
            dataPoints[2] = new DataPoint(2, deepSquatCount);

            // Create a BarGraphSeries with the dataPoints
            BarGraphSeries<DataPoint> series = new BarGraphSeries<>(dataPoints);
            graphView.addSeries(series);

            // Customize the appearance of the series
            series.setColor(ContextCompat.getColor(getContext(), R.color.teal));
            series.setSpacing(20);  // Reduced spacing to avoid cutting off bars

            // Set the number of labels on the Y and X axis
            graphView.getGridLabelRenderer().setNumVerticalLabels(5);  // Adjust as necessary
            graphView.getGridLabelRenderer().setNumHorizontalLabels(3);  // 3 squat types

            // Set titles for the axes
            graphView.getGridLabelRenderer().setVerticalAxisTitle("Count");
            graphView.getGridLabelRenderer().setVerticalAxisTitleColor(ContextCompat.getColor(getContext(), R.color.teal));
            graphView.getGridLabelRenderer().setHorizontalAxisTitle("Squat Type");
            graphView.getGridLabelRenderer().setHorizontalAxisTitleColor(ContextCompat.getColor(getContext(), R.color.teal));

            // Use StaticLabelsFormatter to set custom X-axis labels
            StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graphView);
            staticLabelsFormatter.setHorizontalLabels(new String[] { "Partial\nSquat", "Parallel\nSquat", "Deep\nSquat" });
            graphView.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

            // Customize grid and label colors
            graphView.getGridLabelRenderer().setGridColor(ContextCompat.getColor(getContext(), R.color.teal));
            graphView.getGridLabelRenderer().setHorizontalLabelsColor(ContextCompat.getColor(getContext(), R.color.teal));
            graphView.getGridLabelRenderer().setVerticalLabelsColor(ContextCompat.getColor(getContext(), R.color.teal));

            // Enable scrolling and scaling
            graphView.getViewport().setScalable(true);  // Enables zooming and scrolling
            graphView.getViewport().setScrollable(true);  // Enables scrolling in both directions

            graphView.getViewport().setXAxisBoundsManual(true);
            graphView.getViewport().setMinX(0);
            graphView.getViewport().setMaxX(2);
        } else {
            Log.d("GraphView", "No data available in the floatList.");
        }
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
            showWorkoutDialog(activityWorkoutId, repetition);
    }

    private void showWorkoutDialog(int activityWorkoutId, int repetition) {
        Dialog workoutDialog = new Dialog(requireContext());
        workoutDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        workoutDialog.setContentView(R.layout.dialog_view_score);
        workoutDialog.setCancelable(true);

        if (workoutDialog.getWindow() != null) {
            workoutDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            workoutDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            workoutDialog.getWindow().setDimAmount(0.5f);
        }

        ImageView closeButton = workoutDialog.findViewById(R.id.close);
        closeButton.setOnClickListener(v -> {
            workoutDialog.dismiss();
        });

        RelativeLayout preloaderLayout = workoutDialog.findViewById(R.id.preloaderLayout);
        ImageView preloaderImage = workoutDialog.findViewById(R.id.preloaderImage);
        LinearLayout scoreDetails = workoutDialog.findViewById(R.id.scoreDetails);
        TextView requiredRepetition, repetitionSubmitted, partialSquat, parallelSquat, deepSquat, totalScore, scoreClassification;
        requiredRepetition = workoutDialog.findViewById(R.id.requiredRepetition);
        repetitionSubmitted = workoutDialog.findViewById(R.id.repetitionSubmitted);
        partialSquat = workoutDialog.findViewById(R.id.partialSquat);
        parallelSquat = workoutDialog.findViewById(R.id.parallelSquat);
        deepSquat = workoutDialog.findViewById(R.id.deepSquat);
        totalScore = workoutDialog.findViewById(R.id.totalScore);
        scoreClassification = workoutDialog.findViewById(R.id.scoreClassification);

        preloaderLayout.setVisibility(View.VISIBLE);
        preloaderImage.setVisibility(View.VISIBLE);
        scoreDetails.setVisibility(View.GONE);
        scoreClassification.setVisibility(View.GONE);

        activityViewModel.fetchWorkoutScores(activityWorkoutId, spManager.getUserId()).observe(getViewLifecycleOwner(), workoutScores -> {
            if (workoutScores != null) {
                setGraphViewData(workoutDialog.findViewById(R.id.idGraphView), workoutScores);
                requiredRepetition.setText(String.valueOf(repetition));
                repetitionSubmitted.setText(String.valueOf(repetitionSubmittedCount));
                partialSquat.setText(String.valueOf(partialSquatCount));
                parallelSquat.setText(String.valueOf(parallelSquatCount));
                deepSquat.setText(String.valueOf(deepSquatCount));
                totalScore.setText(String.valueOf(totalWorkoutScore));

                int calcScore = (int) ((totalWorkoutScore / repetitionSubmittedCount) * 100);

                if (calcScore >= 75){
                    scoreClassification.setText("VERY GOOD");
                    scoreClassification.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green));
                } else if (calcScore < 75 && calcScore > 50){
                    scoreClassification.setText("GOOD");
                    scoreClassification.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.submitted_late));
                } else if (calcScore <= 50){
                    scoreClassification.setText("BAD");
                    scoreClassification.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.no_submission));
                }
                preloaderLayout.setVisibility(View.GONE);
                preloaderImage.setVisibility(View.GONE);
                scoreDetails.setVisibility(View.VISIBLE);
                scoreClassification.setVisibility(View.VISIBLE);
            }
        });
        workoutDialog.show();
    }
}

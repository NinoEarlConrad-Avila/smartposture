package com.example.smartposture.view.fragment;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
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
import com.example.smartposture.viewmodel.ActivityViewModel;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.example.smartposture.R;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import java.util.ArrayList;

public class WorkoutSummaryFragment extends BaseFragment {
    private TextView repCount, totalScore;
    private Button submit;
    private ActivityViewModel activityViewModel;
    private SharedPreferenceManager spManager;
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

        activityViewModel = new ViewModelProvider(this).get(ActivityViewModel.class);
        submit.setOnClickListener(v -> {
            if (floatList != null && floatList.size() > 1) {
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
            // Loop through the list and log each element
            for (Float value : floatList) {
                Log.d("WorkoutSummaryFragment", "Value: " + value);
            }

            // Set rep count and total score
            repCount.setText(String.valueOf(floatList.size()-1));  // Display size of list
            totalScore.setText(String.valueOf(floatList.get(0))); // Display the first element in the list

            // Set graph view data (ensure the method is defined correctly)
            setGraphViewData(view, floatList);
        } else {
            Log.d("WorkoutSummaryFragment", "No data found in floatList.");
        }
        return view;
    }

    private void setGraphViewData(View view, ArrayList<Float> floatList) {
        // Get the GraphView from the layout
        GraphView graphView = view.findViewById(R.id.idGraphView);

        if (floatList != null && !floatList.isEmpty()) {
            // Create counters for each squat type
            int partialSquatCount = 0;
            int parallelSquatCount = 0;
            int deepSquatCount = 0;

            // Classify the squat types based on the float values
            for (int i = 1; i < floatList.size(); i++) {
                Float value = floatList.get(i);
                if (value <= 0.25) {
                    partialSquatCount++;
                } else if (value <= 0.5) {
                    parallelSquatCount++;
                } else {
                    deepSquatCount++;
                }
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

            // Set X-axis bounds based on the data
            graphView.getViewport().setXAxisBoundsManual(true);
            graphView.getViewport().setMinX(0); // Start from 0 on the X-axis
            graphView.getViewport().setMaxX(2); // Set max X value to 2 (since you have 3 categories)
        } else {
            Log.d("GraphView", "No data available in the floatList.");
        }
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

package com.example.smartposture.view.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.example.smartposture.R;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import java.util.ArrayList;

public class WorkoutSummaryFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workout_summary, container, false);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                    }
                });

        ArrayList<Float> floatList = (ArrayList<Float>) getArguments().getSerializable("floatList");

        if (floatList != null) {
            // Loop through the list and log each element
            for (Float value : floatList) {
                Log.d("WorkoutSummaryFragment", "Value: " + value);
            }
        } else {
            Log.d("WorkoutSummaryFragment", "No data found in floatList.");
        }

        setGraphViewData(view, floatList);
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
}

package com.example.smartposture.view;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.example.smartposture.R;

import java.util.ArrayList;
import java.util.Map;

public class WorkoutSummaryFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workout_summary, container, false);
        setGraphViewData(view);
        return view;
    }

    private void setGraphViewData(View view) {
        // Get the GraphView from the layout
        GraphView graphView =(GraphView) view.findViewById(R.id.idGraphView);

        // Create a new series with DataPoints for the graph
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(1, 0),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 1),
                new DataPoint(5, 8),
                new DataPoint(6, 10),
                new DataPoint(7, 1),
                new DataPoint(8, 2),
                new DataPoint(9, 2),
                new DataPoint(10, 5),

        });

        // Add the series to the graph
        graphView.addSeries(series);

        // Customize the graph appearance
        series.setColor(getResources().getColor(R.color.teal));
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(1);
        series.setThickness(3);
        graphView.getGridLabelRenderer().setVerticalAxisTitle("Y Axis");
        graphView.getGridLabelRenderer().setHorizontalAxisTitle("X Axis");

        // Enable scrolling and scaling
        graphView.getViewport().setScalable(true);
        graphView.getViewport().setScrollable(true);
        graphView.getGridLabelRenderer().setGridColor(Color.RED); // Set both horizontal and vertical grid color
        // Or individually set horizontal/vertical grid color
        graphView.getGridLabelRenderer().setHorizontalLabelsColor(Color.BLUE); // Horizontal grid color
        graphView.getGridLabelRenderer().setVerticalLabelsColor(Color.GREEN);   // Vertical grid color
        graphView.getGridLabelRenderer().setPadding(20);
        // Additional customizations
        graphView.getGridLabelRenderer().setHighlightZeroLines(true);
    }
}
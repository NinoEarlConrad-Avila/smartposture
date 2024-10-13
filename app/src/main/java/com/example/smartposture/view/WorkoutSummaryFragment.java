package com.example.smartposture.view;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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
        GraphView graphView = (GraphView) view.findViewById(R.id.idGraphView);

        // Create two series (lines)
        LineGraphSeries<DataPoint> series1 = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(0, 0),
                new DataPoint(1, 40),
                new DataPoint(2, 70),
                new DataPoint(3, 30),
                new DataPoint(4, 30),
                new DataPoint(5, 30),
        });

//        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>(new DataPoint[]{
//                new DataPoint(1, 5),
//                new DataPoint(2, 6),
//                new DataPoint(3, 7),
//        });

        // Add both series to the graph
        graphView.addSeries(series1);
//        graphView.addSeries(series2);

        // Customize the appearance of both series
        series1.setColor(ContextCompat.getColor(getContext(), R.color.teal));
        series1.setDrawDataPoints(true);
        series1.setDataPointsRadius(1);
        series1.setThickness(8);
        series1.setColor(Color.BLUE);


//        series2.setColor(ContextCompat.getColor(getContext(), R.color.teal));
//        series2.setDrawDataPoints(true);
//        series2.setDataPointsRadius(1);
//        series2.setThickness(3);

        // Get the CheckBoxes from the layout
//        CheckBox checkBox1 = view.findViewById(R.id.checkBox2);
//        CheckBox checkBox2 = view.findViewById(R.id.checkBox1);
//
//        checkBox1.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (isChecked) {
//
//                checkBox2.setChecked(false);
//
//                series1.setThickness(8);
//                series1.setColor(Color.RED);
//
//                series2.setThickness(3);
//                series2.setColor(ContextCompat.getColor(getContext(), R.color.teal));
//            } else {
//                if (!checkBox2.isChecked()) {
//                    series1.setThickness(3);
//                    series1.setColor(ContextCompat.getColor(getContext(), R.color.teal));
//                    series2.setThickness(3);
//                    series2.setColor(ContextCompat.getColor(getContext(), R.color.teal));
//                }
//            }
//            graphView.invalidate();
//        });
//
//        checkBox2.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (isChecked) {
//                checkBox1.setChecked(false);
//
//                series2.setThickness(8);
//                series2.setColor(Color.GREEN);
//
//                series1.setThickness(3);
//                series1.setColor(ContextCompat.getColor(getContext(), R.color.teal));
//            } else {
//                if (!checkBox1.isChecked()) {
//                    series1.setThickness(3);
//                    series1.setColor(ContextCompat.getColor(getContext(), R.color.teal));
//                    series2.setThickness(3);
//                    series2.setColor(Color.RED);
//                }
//            }
//            graphView.invalidate();
//        });

        graphView.getGridLabelRenderer().setNumVerticalLabels(5);   // Set number of labels on Y axis
        graphView.getGridLabelRenderer().setNumHorizontalLabels(6);
// Set manual bounds for the viewport (to control the min and max values on X and Y axes)
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMinY(0);   // Set minimum Y value
        graphView.getViewport().setMaxY(100);  // Set maximum Y value

        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(0);   // Set minimum X value
        graphView.getViewport().setMaxX(5);  // Set maximum X value

        graphView.getGridLabelRenderer().setVerticalAxisTitle("Angle");
        graphView.getGridLabelRenderer().setVerticalAxisTitleColor(ContextCompat.getColor(getContext(), R.color.teal));
        graphView.getGridLabelRenderer().setHorizontalAxisTitle("Repetition");
        graphView.getGridLabelRenderer().setHorizontalAxisTitleColor(ContextCompat.getColor(getContext(), R.color.teal));
        graphView.getGridLabelRenderer().setGridColor(ContextCompat.getColor(getContext(), R.color.teal));
        graphView.getGridLabelRenderer().setHorizontalLabelsColor(ContextCompat.getColor(getContext(), R.color.teal));
        graphView.getGridLabelRenderer().setVerticalLabelsColor(ContextCompat.getColor(getContext(), R.color.teal));
        graphView.getViewport().setScalable(true);
        graphView.getViewport().setScrollable(true);
    }

}
package com.example.smartposture.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.example.smartposture.R;
import com.example.smartposture.viewmodel.ActivityViewModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;

public class CustomGraph {

    private static int partialSquatCount = 0;
    private static int parallelSquatCount = 0;
    private static int deepSquatCount = 0;
    private static float totalWorkoutScore = 0;
    private static int repetitionSubmittedCount = 0;

    public static void showWorkoutDialog(
            LifecycleOwner lifecycleOwner,
            Context context,
            int activityWorkoutId,
            int repetition,
            int traineeId,
            ActivityViewModel activityViewModel
    ) {
        Dialog workoutDialog = new Dialog(context);
        workoutDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        workoutDialog.setContentView(R.layout.dialog_view_score);
        workoutDialog.setCancelable(true);

        if (workoutDialog.getWindow() != null) {
            workoutDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            workoutDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            workoutDialog.getWindow().setDimAmount(0.5f);
        }

        ImageView closeButton = workoutDialog.findViewById(R.id.close);
        closeButton.setOnClickListener(v -> workoutDialog.dismiss());

        RelativeLayout preloaderLayout = workoutDialog.findViewById(R.id.preloaderLayout);
        ImageView preloaderImage = workoutDialog.findViewById(R.id.preloaderImage);
        LinearLayout scoreDetails = workoutDialog.findViewById(R.id.scoreDetails);
        TextView requiredRepetition = workoutDialog.findViewById(R.id.requiredRepetition);
        TextView repetitionSubmitted = workoutDialog.findViewById(R.id.repetitionSubmitted);
        TextView partialSquat = workoutDialog.findViewById(R.id.partialSquat);
        TextView parallelSquat = workoutDialog.findViewById(R.id.parallelSquat);
        TextView deepSquat = workoutDialog.findViewById(R.id.deepSquat);
        TextView totalScore = workoutDialog.findViewById(R.id.totalScore);
        TextView scoreClassification = workoutDialog.findViewById(R.id.scoreClassification);

        preloaderLayout.setVisibility(View.VISIBLE);
        preloaderImage.setVisibility(View.VISIBLE);
        scoreDetails.setVisibility(View.GONE);
        scoreClassification.setVisibility(View.GONE);

        activityViewModel.fetchWorkoutScores(activityWorkoutId, traineeId).observe(lifecycleOwner, workoutScores -> {
            if (workoutScores != null) {
                setGraphViewData(workoutDialog.findViewById(R.id.viewScoreBarChart), workoutScores, context);

                requiredRepetition.setText(String.valueOf(repetition));
                repetitionSubmitted.setText(String.valueOf(repetitionSubmittedCount));
                partialSquat.setText(String.valueOf(partialSquatCount));
                parallelSquat.setText(String.valueOf(parallelSquatCount));
                deepSquat.setText(String.valueOf(deepSquatCount));
                totalScore.setText(String.valueOf(totalWorkoutScore));

                int calcScore = (int) ((totalWorkoutScore / repetitionSubmittedCount) * 100);

                if (calcScore >= 75){
                    scoreClassification.setText("VERY GOOD");
                    scoreClassification.setBackgroundColor(ContextCompat.getColor(context, R.color.green));
                } else if (calcScore < 75 && calcScore >= 50){
                    scoreClassification.setText("GOOD");
                    scoreClassification.setBackgroundColor(ContextCompat.getColor(context, R.color.submitted_late));
                } else {
                    scoreClassification.setText("BAD");
                    scoreClassification.setBackgroundColor(ContextCompat.getColor(context, R.color.no_submission));
                }

                preloaderLayout.setVisibility(View.GONE);
                preloaderImage.setVisibility(View.GONE);
                scoreDetails.setVisibility(View.VISIBLE);
                scoreClassification.setVisibility(View.VISIBLE);
            }
        });

        workoutDialog.show();
    }

    public static void setGraphViewData(View view, ArrayList<Float> floatList, Context context) {
        BarChart barChart = view.findViewById(R.id.viewScoreBarChart);

        if (floatList != null && !floatList.isEmpty()) {
            partialSquatCount = 0;
            parallelSquatCount = 0;
            deepSquatCount = 0;
            totalWorkoutScore = 0;
            repetitionSubmittedCount = floatList.size();

            for (Float value : floatList) {
                if (value <= 0.25) partialSquatCount++;
                else if (value <= 0.5) parallelSquatCount++;
                else deepSquatCount++;

                totalWorkoutScore += value;
            }

            ArrayList<BarEntry> entries = new ArrayList<>();
            entries.add(new BarEntry(0f, partialSquatCount));
            entries.add(new BarEntry(1f, parallelSquatCount));
            entries.add(new BarEntry(2f, deepSquatCount));

            BarDataSet barDataSet = new BarDataSet(entries, "");
            barDataSet.setColors(
                    ContextCompat.getColor(context, R.color.no_submission),
                    ContextCompat.getColor(context, R.color.submitted_late),
                    ContextCompat.getColor(context, R.color.green)
            );

            BarData barData = new BarData(barDataSet);
            barChart.setData(barData);
            barChart.setFitBars(true);

            XAxis xAxis = barChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"Partial", "Parallel", "Deep"}));

            YAxis yAxisLeft = barChart.getAxisLeft();
            yAxisLeft.setGranularity(1f);
            yAxisLeft.setAxisMinimum(0f);

            barChart.getAxisRight().setEnabled(false);

            barChart.setDoubleTapToZoomEnabled(false);
            barChart.setPinchZoom(false);
            barChart.setScaleEnabled(false);
            barChart.setClickable(false);

            Description description = new Description();
            description.setText("");
            barChart.setDescription(description);
            barChart.getLegend().setEnabled(false);

            barChart.invalidate();
        } else {
            Log.d("BarChart", "No data available in the floatList.");
        }
    }
}

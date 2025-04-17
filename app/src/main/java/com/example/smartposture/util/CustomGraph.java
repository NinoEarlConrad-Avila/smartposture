package com.example.smartposture.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
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
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;

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

            barDataSet.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return String.valueOf((int) value);
                }
            });

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
            barChart.animateY(1000);

            Description description = new Description();
            description.setText("");
            barChart.setDescription(description);
            barChart.getLegend().setEnabled(false);

            barChart.invalidate();
        } else {
            Log.d("BarChart", "No data available in the floatList.");
        }
    }
    public static void setBarMonthlyWorkouts(View view, Context context, List<Integer> dailyReps) {
        BarChart barChart = view.findViewById(R.id.barChart);

        List<BarEntry> entries = new ArrayList<>();
        String[] days = new String[dailyReps.size()];

        for (int i = 0; i < dailyReps.size(); i++) {
            entries.add(new BarEntry(i, dailyReps.get(i)));
            days[i] = String.valueOf(i + 1);
        }

        BarDataSet dataSet = new BarDataSet(entries, "Daily Workout Repetitions");
        int barColor = ContextCompat.getColor(context, R.color.green_med);
        dataSet.setColor(barColor);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);
        barChart.setFitBars(true);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(days));
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setLabelCount(days.length);
        xAxis.setLabelRotationAngle(90);

        barChart.getAxisLeft().setAxisMinimum(0f);
        barChart.getAxisRight().setAxisMinimum(0f);

        Legend legend = barChart.getLegend();
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);

        barChart.getDescription().setEnabled(false);
        barChart.animateY(1000);
        barChart.invalidate();
    }

    public static void setPieOverall(View view, Context context, float averageScore, int totalReps) {
        PieChart pieChart = view.findViewById(R.id.pieChartAveReps);

        float percentage = averageScore * 100f;
        int mainColor;

        if (percentage >= 75) mainColor = ContextCompat.getColor(context, R.color.green);
        else if (percentage >= 50) mainColor = ContextCompat.getColor(context, R.color.submitted_late);
        else mainColor = ContextCompat.getColor(context, R.color.no_submission);

        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(percentage, "Average Score"));
        entries.add(new PieEntry(100f - percentage, ""));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(
                mainColor,
                Color.parseColor("#EEEEEE")
        );
        dataSet.setSliceSpace(2f);
        dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setDrawValues(false);

        pieChart.setData(data);
        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.setDrawEntryLabels(false);
        pieChart.setHoleRadius(75f);
        pieChart.setTransparentCircleRadius(80f);

        String centerLabel = "Total Reps: " + totalReps + "\nAvg Score: " + String.format("%.2f", percentage) + "%";
        pieChart.setCenterText(centerLabel);
        pieChart.setCenterTextSize(9f);
        pieChart.setCenterTextColor(Color.DKGRAY);
        pieChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD);

        pieChart.animateY(1000, Easing.EaseInOutQuad);
        pieChart.invalidate();
    }

    public static void setPieScoresClassification(View view, Context context, int value1, int value2, int value3) {
        PieChart pieChart = view.findViewById(R.id.pieChartClassification);

        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(value1, "Partial"));
        entries.add(new PieEntry(value2, "Parallel"));
        entries.add(new PieEntry(value3, "Deep"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(
                ContextCompat.getColor(context, R.color.no_submission),
                ContextCompat.getColor(context, R.color.submitted_late),
                ContextCompat.getColor(context, R.color.green)
        );
        dataSet.setSliceSpace(2f);
        dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueTextColor(Color.BLACK);
        data.setValueTextSize(12f);

        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        pieChart.setData(data);
        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(true);

        pieChart.setDrawEntryLabels(false);

        pieChart.setHoleRadius(50f);
        pieChart.setTransparentCircleRadius(55f);
        pieChart.animateY(1000, Easing.EaseInOutQuad);
        pieChart.invalidate();
    }

    public static void setBarSubmissionStatus(View view, Context context, int submittedCount, int notSubmittedCount) {
        HorizontalBarChart barChart = view.findViewById(R.id.horizontalBarChart);

        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, notSubmittedCount));
        entries.add(new BarEntry(1f, submittedCount));

        BarDataSet dataSet = new BarDataSet(entries, "Submission Status");
        dataSet.setColors(
                ContextCompat.getColor(context, R.color.no_submission),
                ContextCompat.getColor(context, R.color.green)
        );
        dataSet.setValueTextSize(12f);

        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getBarLabel(BarEntry barEntry) {
                return String.valueOf((int) barEntry.getY());
            }
        });

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.4f);

        barChart.setData(data);

        final String[] labels = new String[]{"Not Submitted", "Submitted"};
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(12f);

        barChart.getAxisLeft().setAxisMinimum(0f);
        barChart.getAxisRight().setEnabled(false);

        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);

        barChart.getXAxis().setAxisMinimum(-0.5f);
        barChart.getXAxis().setAxisMaximum(entries.size() - 0.5f);

        barChart.animateY(1000);
        barChart.invalidate();
    }
}

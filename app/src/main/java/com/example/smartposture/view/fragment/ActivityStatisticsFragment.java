package com.example.smartposture.view.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartposture.R;
import com.example.smartposture.data.adapter.ActivityTraineeAdapter;
import com.example.smartposture.data.model.ActivityStatistics;
import com.example.smartposture.data.model.ActivityTrainee;
import com.example.smartposture.util.CustomGraph;
import com.example.smartposture.viewmodel.ActivityViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActivityStatisticsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ActivityTraineeAdapter traineeAdapter;
    private List<ActivityTrainee> traineeList = new ArrayList<>();
    private ActivityViewModel viewModel;
    private RelativeLayout noNotificationLayout, preloaderLayout;
    private ImageView preloaderImage;
    private ImageButton backButton;
    private int activityId, roomId;
    private Animation bounceAnimation;
    private Dialog loadingDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_activity_statistics, container, false);

        rootView.setVisibility(View.INVISIBLE);
        loadingDialog = createFullScreenLoadingDialog();
        loadingDialog.show();

        activityId = requireArguments().getInt("activity_id", -1);
        roomId = requireArguments().getInt("room_id", -1);
        // Initialize views
        recyclerView = rootView.findViewById(R.id.notificationRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        noNotificationLayout = rootView.findViewById(R.id.noNotification);
        preloaderLayout = rootView.findViewById(R.id.preloaderLayout);
        preloaderImage = rootView.findViewById(R.id.preloaderImage);
        backButton = rootView.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> getActivity().onBackPressed());

        bounceAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.logo_bounce);
        traineeAdapter = new ActivityTraineeAdapter(getContext(), traineeList, new ActivityTraineeAdapter.ViewSubmissionClickListener() {
            @Override
            public void onViewSubmissionClick(ActivityTrainee trainee) {
                navigateToActivityDetails(trainee.getTrainee_id(), trainee.getActivity_id(), roomId);
            }
        });
        recyclerView.setAdapter(traineeAdapter);

        viewModel = new ViewModelProvider(this).get(ActivityViewModel.class);

        observeActivityStatistics(rootView);

        fetchActivityStatisticsData();

        return rootView;
    }

    private void observeActivityStatistics(View view) {
        viewModel.getActivityStatistics().observe(getViewLifecycleOwner(), new Observer<ActivityStatistics>() {
            @Override
            public void onChanged(ActivityStatistics statistics) {
                if (statistics != null && !statistics.getTrainees().isEmpty()) {
                    noNotificationLayout.setVisibility(View.GONE);
                    preloaderLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                    Map<String, ActivityTrainee> traineesMap = statistics.getTrainees();
                    CustomGraph.setPieOverall(requireView(), requireContext(),statistics.getAvg_score(), statistics.getTotal_repetitions());
                    CustomGraph.setPieScoresClassification(requireView(), requireContext(), statistics.getCount_025(), statistics.getCount_050(), statistics.getCount_100());
                    CustomGraph.setBarSubmissionStatus(requireView(), requireContext(), statistics.getTotal_submitted(), statistics.getTotal_not_submitted());

                    traineeList.clear();
                    traineeList.addAll(traineesMap.values());
                    view.setVisibility(View.VISIBLE);
                    loadingDialog.dismiss();
                } else {
                    noNotificationLayout.setVisibility(View.VISIBLE);
                    preloaderLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                }
            }
        });
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

    private void fetchActivityStatisticsData() {
        // Display preloader while fetching data
        noNotificationLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        preloaderLayout.setVisibility(View.VISIBLE);
        preloaderImage.startAnimation(bounceAnimation);

        viewModel.fetchActivityStatistics(roomId, activityId);
    }

    private void navigateToActivityDetails(int traineeId, int activityId, int roomId) {
        Bundle bundle = new Bundle();
        bundle.putInt("trainee_id", traineeId);
        bundle.putInt("activity_id", activityId);
        bundle.putInt("room_id", roomId);

        SubmissionDetailFragment summary = new SubmissionDetailFragment();
        summary.setArguments(bundle);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, summary)
                .addToBackStack("ActivityStatisticsFragment")
                .commit();
    }
}

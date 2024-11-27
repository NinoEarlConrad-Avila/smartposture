package com.example.smartposture.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartposture.R;
import com.example.smartposture.data.adapter.StepsAdapter;
import com.example.smartposture.data.model.WorkoutDetail;
import com.example.smartposture.viewmodel.WorkoutDetailViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class WorkoutDetailFragment extends Fragment {

    private WorkoutDetailViewModel workoutDetailViewModel;
    private ScrollView contentScrollView;
    private TextView workoutName, workoutDescription;
    private ImageView workoutImage;
    private FrameLayout preloaderFrame;
    private RecyclerView stepsRecyclerView;
    private ImageView logoPreloader;
    private Button startButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workout_detail, container, false);

        if (getActivity() != null) {
            BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottom_navigation);
            if (bottomNav != null) {
                bottomNav.setVisibility(View.GONE);
            }
        }

        // Initialize views
        preloaderFrame = view.findViewById(R.id.preloaderFrame);
        contentScrollView = view.findViewById(R.id.workoutDetailScrollView);
        logoPreloader = view.findViewById(R.id.logoPreloader);

        workoutName = view.findViewById(R.id.workoutDetailName);
        workoutDescription = view.findViewById(R.id.workoutDetailDescription);
        workoutImage = view.findViewById(R.id.workoutDetailImage);
        stepsRecyclerView = view.findViewById(R.id.stepsRecyclerView);
        startButton = view.findViewById(R.id.startButton);

        // Initially hide content and show preloader
        preloaderFrame.setVisibility(View.VISIBLE);
        contentScrollView.setVisibility(View.GONE);
        startButton.setVisibility(View.GONE);

        // Start preloader animation (if any)
        logoPreloader.startAnimation(android.view.animation.AnimationUtils.loadAnimation(getContext(), R.anim.logo_bounce));

        workoutDetailViewModel = new ViewModelProvider(this).get(WorkoutDetailViewModel.class);

        // Set up RecyclerView
        stepsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        stepsRecyclerView.setNestedScrollingEnabled(false);
        stepsRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        // Get the workout ID from arguments and load data
        int workoutId = requireArguments().getInt("workout_id", -1);
        if (workoutId != -1) {
            // Observing the LiveData from the ViewModel
            workoutDetailViewModel.getWorkoutDetail(workoutId).observe(getViewLifecycleOwner(), response -> {
                if (response != null && response.getWorkouts() != null) {
                    // Successfully fetched workout data
                    WorkoutDetail workoutDetail = response.getWorkouts();
                    workoutName.setText(workoutDetail.getName());
                    workoutDescription.setText(workoutDetail.getDescription());

                    // Set image resource dynamically
                    int imageResourceId = getDrawableResourceId(requireContext(), workoutDetail.getPath());
                    workoutImage.setImageResource(imageResourceId != 0 ? imageResourceId : R.drawable.default_image);

                    // Set up RecyclerView adapter for steps
                    StepsAdapter stepsAdapter = new StepsAdapter(workoutDetail.getSteps());
                    stepsRecyclerView.setAdapter(stepsAdapter);
                    setRecyclerViewHeightBasedOnItems(stepsRecyclerView);

                    // Hide preloader and show content
                    preloaderFrame.setVisibility(View.GONE);
                    contentScrollView.setVisibility(View.VISIBLE);
                    startButton.setVisibility(View.VISIBLE);
                } else {
                    // Handle error case if no data is found
                    workoutName.setText("Error fetching details.");
                    workoutDescription.setText("");
                    preloaderFrame.setVisibility(View.GONE);
                    contentScrollView.setVisibility(View.VISIBLE);
                    startButton.setVisibility(View.VISIBLE);
                }
            });
        }

        return view;
    }

    private void setRecyclerViewHeightBasedOnItems(RecyclerView recyclerView) {
        RecyclerView.Adapter<RecyclerView.ViewHolder> adapter = recyclerView.getAdapter();
        if (adapter == null) {
            return;
        }

        int totalHeight = 0;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager == null) {
            return;
        }

        int recyclerViewPadding = recyclerView.getPaddingTop() + recyclerView.getPaddingBottom();
        totalHeight += recyclerViewPadding;

        for (int i = 0; i < adapter.getItemCount(); i++) {
            RecyclerView.ViewHolder holder = adapter.createViewHolder(recyclerView, adapter.getItemViewType(i));
            adapter.bindViewHolder(holder, i);

            View itemView = holder.itemView;
            itemView.measure(
                    View.MeasureSpec.makeMeasureSpec(recyclerView.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.UNSPECIFIED
            );

            int itemHeight = itemView.getMeasuredHeight();
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) itemView.getLayoutParams();
            int itemMarginTop = layoutParams.topMargin;
            int itemMarginBottom = layoutParams.bottomMargin;

            totalHeight += itemHeight + itemMarginTop + itemMarginBottom;
        }

        ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
        params.height = totalHeight + 350;
        recyclerView.setLayoutParams(params);
    }

    private int getDrawableResourceId(Context context, String fileName) {
        return context.getResources().getIdentifier(fileName, "drawable", context.getPackageName());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (getActivity() != null) {
            BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottom_navigation);
            if (bottomNav != null) {
                bottomNav.setVisibility(View.VISIBLE);
            }
        }
    }
}

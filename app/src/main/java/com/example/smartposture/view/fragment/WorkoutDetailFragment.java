package com.example.smartposture.view.fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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
import com.example.smartposture.view.activity.MainActivity;
import com.example.smartposture.viewmodel.WorkoutDetailViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class WorkoutDetailFragment extends Fragment {

    private WorkoutDetailViewModel workoutDetailViewModel;
    private ScrollView contentScrollView;
    private TextView workoutName, workoutDescription;
    private ImageView workoutImage;
    private RecyclerView stepsRecyclerView;
    private Button startButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workout_detail, container, false);

        if (getActivity() != null) {
            ((MainActivity) getActivity()).setBottomNavVisibility(View.GONE);
        }

        // Initialize views
        contentScrollView = view.findViewById(R.id.workoutDetailScrollView);
        workoutName = view.findViewById(R.id.workoutDetailName);
        workoutDescription = view.findViewById(R.id.workoutDetailDescription);
        workoutImage = view.findViewById(R.id.workoutDetailImage);
        stepsRecyclerView = view.findViewById(R.id.stepsRecyclerView);
        startButton = view.findViewById(R.id.startButton);

        // Set up the loading dialog
        Dialog loadingDialog = createFullScreenLoadingDialog();
        loadingDialog.show();

        workoutDetailViewModel = new ViewModelProvider(this).get(WorkoutDetailViewModel.class);

        // Set up RecyclerView
        stepsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        stepsRecyclerView.setNestedScrollingEnabled(false);
        stepsRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        startButton.setOnClickListener(v -> navigateToPoseDetector(workoutName.getText().toString().toLowerCase()));

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
                    stepsRecyclerView.setNestedScrollingEnabled(false);
                    setRecyclerViewHeightBasedOnItems(stepsRecyclerView);

                    // Hide loading dialog and show content
                    loadingDialog.dismiss();
                } else {
                    // Handle error case if no data is found
                    workoutName.setText("Error fetching details.");
                    workoutDescription.setText("");
                    loadingDialog.dismiss();
                }
            });
        }

        return view;
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
            ((MainActivity) getActivity()).setBottomNavVisibility(View.VISIBLE);
        }
    }

    private void navigateToPoseDetector(String name) {
        Bundle bundle = new Bundle();
        bundle.putString("exer", name);

        PoseDetectorFragment fragment = new PoseDetectorFragment();
        fragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }
}

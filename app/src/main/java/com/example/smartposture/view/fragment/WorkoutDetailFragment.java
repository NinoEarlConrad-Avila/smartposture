package com.example.smartposture.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartposture.R;
import com.example.smartposture.data.adapter.StepsAdapter;
import com.example.smartposture.data.api.ApiClient;
import com.example.smartposture.data.api.ApiService;
import com.example.smartposture.data.model.WorkoutDetail;
import com.example.smartposture.data.request.WorkoutDetailRequest;
import com.example.smartposture.data.response.WorkoutDetailResponse;
import com.example.smartposture.viewmodel.WorkoutDetailViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkoutDetailFragment extends Fragment {
    private WorkoutDetailViewModel workoutDetailViewModel;

    private TextView workoutName, workoutDescription;
    private ImageView workoutImage;
    RecyclerView stepsRecyclerView;

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

        workoutName = view.findViewById(R.id.workoutDetailName);
        workoutDescription = view.findViewById(R.id.workoutDetailDescription);
        workoutImage = view.findViewById(R.id.workoutDetailImage);
        stepsRecyclerView = view.findViewById(R.id.stepsRecyclerView);

        workoutDetailViewModel = new ViewModelProvider(this).get(WorkoutDetailViewModel.class);
        stepsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        int workoutId = requireArguments().getInt("workout_id", -1);
        if (workoutId != -1) {
            workoutDetailViewModel.getWorkoutDetail(workoutId).observe(getViewLifecycleOwner(), response -> {
                if (response != null && response.getWorkouts() != null) {
                    WorkoutDetail workoutDetail = response.getWorkouts();
                    workoutName.setText(workoutDetail.getName());
                    workoutDescription.setText(workoutDetail.getDescription());

                    int imageResourceId = getDrawableResourceId(requireContext(), workoutDetail.getPath());
                    workoutImage.setImageResource(imageResourceId != 0 ? imageResourceId : R.drawable.default_image);

                    // Pass dynamic steps to the adapter
                    StepsAdapter stepsAdapter = new StepsAdapter(workoutDetail.getSteps());
                    stepsRecyclerView.setAdapter(stepsAdapter);
                    setRecyclerViewHeightBasedOnItems(stepsRecyclerView);

                    stepsRecyclerView.setNestedScrollingEnabled(false);
                    stepsRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
                } else {
                    workoutName.setText("Error fetching details.");
                    workoutDescription.setText("");
                }
            });
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int workoutId = requireArguments().getInt("workout_id", -1);
        if (workoutId != -1) {
            fetchWorkoutDetails(workoutId);
        }
    }

    private void fetchWorkoutDetails(int workoutId) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        WorkoutDetailRequest request = new WorkoutDetailRequest(workoutId);

        apiService.getWorkoutDetail(request).enqueue(new Callback<WorkoutDetailResponse>() {
            @Override
            public void onResponse(@NonNull Call<WorkoutDetailResponse> call, @NonNull Response<WorkoutDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    displayWorkoutDetails(response.body().getWorkouts());
                }
            }

            @Override
            public void onFailure(@NonNull Call<WorkoutDetailResponse> call, @NonNull Throwable t) {
                Log.e("WorkoutDetail", "Failed to fetch workout details", t);
            }
        });
    }

    private void displayWorkoutDetails(WorkoutDetail detail) {
        TextView nameTextView = requireView().findViewById(R.id.workoutDetailName);
        TextView descriptionTextView = requireView().findViewById(R.id.workoutDetailDescription);
        RecyclerView stepsRecyclerView = requireView().findViewById(R.id.stepsRecyclerView);

        nameTextView.setText(detail.getName());
        descriptionTextView.setText(detail.getDescription());

        StepsAdapter adapter = new StepsAdapter(detail.getSteps());
        stepsRecyclerView.setAdapter(adapter);
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

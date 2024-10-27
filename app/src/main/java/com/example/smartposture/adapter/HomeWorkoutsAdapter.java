package com.example.smartposture.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.smartposture.R;
import com.example.smartposture.model.WorkoutModel;
import com.example.smartposture.view.fragment.WorkoutDetailsStartFragment;

import java.util.List;

public class HomeWorkoutsAdapter extends RecyclerView.Adapter<HomeWorkoutsAdapter.WorkoutViewHolder> {
    private List<WorkoutModel> workouts;

    public HomeWorkoutsAdapter(List<WorkoutModel> workouts) {
        this.workouts = workouts;
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_home_workout, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        WorkoutModel workout = workouts.get(position);
        holder.workoutName.setText(workout.getTitle());

        // Assuming `getCardPath()` returns the drawable resource name without the extension
        String cardPath = workout.getPath();
        int resourceId = holder.itemView.getContext().getResources().getIdentifier(cardPath, "drawable", holder.itemView.getContext().getPackageName());

        // Load the image using Glide
        if (resourceId != 0) {
            Glide.with(holder.itemView.getContext())
                    .load(resourceId)
                    .into(holder.workoutImg);
        } else {
            // Load a default image if the resource is not found
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.default_image)
                    .into(holder.workoutImg);
        }

        holder.itemView.setOnClickListener(v -> {
            FragmentActivity activity = (FragmentActivity) v.getContext();

            // Combine the workout steps into a single string
            List<String> steps = workout.getSteps();
            StringBuilder stepsBuilder = new StringBuilder();
            for (String step : steps) {
                stepsBuilder.append(step).append("\n");
            }
            String combinedSteps = stepsBuilder.toString();

            // Create the fragment with workout data including combined steps
            WorkoutDetailsStartFragment detailsFragment = WorkoutDetailsStartFragment.newInstance(
                    workout.getTitle(),
                    workout.getPath(),
                    workout.getId(),
                    workout.getDescription(),
                    combinedSteps // Pass the combined steps as the guide
            );

            // Replace the current fragment with WorkoutDetailsStartFragment
            FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, detailsFragment)
                    .addToBackStack("WorkoutDetailsStartFragment")
                    .commit();
        });
    }


    @Override
    public int getItemCount() {
        return workouts.size();
    }

    static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        TextView workoutName;
        ImageView workoutImg;

        WorkoutViewHolder(View itemView) {
            super(itemView);
            workoutName = itemView.findViewById(R.id.workoutName);
            workoutImg = itemView.findViewById(R.id.workoutImg);
        }
    }
}

package com.example.smartposture.data.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartposture.R;
import com.example.smartposture.data.model.ActivityWorkout;

import java.util.ArrayList;
import java.util.List;

public class ActivityWorkoutAdapter extends RecyclerView.Adapter<ActivityWorkoutAdapter.ActivityWorkoutViewHolder> {
    private List<ActivityWorkout> workouts;

    public ActivityWorkoutAdapter(List<ActivityWorkout> workouts){
        this.workouts = workouts;
    }

    @NonNull
    @Override
    public ActivityWorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity_workout, parent, false);
        return new ActivityWorkoutAdapter.ActivityWorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityWorkoutViewHolder holder, int position) {
        ActivityWorkout activity = workouts.get(position);
        String reps = "X" +activity.getRepetition();

        holder.name.setText(activity.getWorkout_name());
        holder.description.setText(activity.getWorkout_description());
        holder.repetition.setText(reps);

        int imageResourceId = getDrawableResourceId(holder.itemView.getContext(), activity.getWorkout_img_path());
        holder.img_path.setImageResource(imageResourceId != 0 ? imageResourceId : R.drawable.default_image);
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    static class ActivityWorkoutViewHolder extends RecyclerView.ViewHolder {
        TextView name, description, repetition;
        ImageView img_path;
        ImageButton startButton;

        public ActivityWorkoutViewHolder(@NonNull View itemView){
            super(itemView);
            name = itemView.findViewById(R.id.workoutName);
            description = itemView.findViewById(R.id.workoutDescription);
            repetition = itemView.findViewById(R.id.repetition);
            img_path = itemView.findViewById(R.id.workoutImage);
            startButton = itemView.findViewById(R.id.startWorkout);
        }
    }

    public void updateWorkouts(List<ActivityWorkout> newWorkouts) {
        if (newWorkouts == null) {
            newWorkouts = new ArrayList<>();
        }
        this.workouts.clear();
        this.workouts.addAll(newWorkouts);
        notifyDataSetChanged();
    }


    private int getDrawableResourceId(Context context, String fileName) {
        return context.getResources().getIdentifier(fileName, "drawable", context.getPackageName());
    }
}

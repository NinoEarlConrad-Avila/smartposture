package com.example.smartposture.data.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.smartposture.R;
import com.example.smartposture.data.model.Workout;

import java.util.List;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {
    private List<Workout> workouts;

    public WorkoutAdapter(List<Workout> workouts) {
        this.workouts = workouts;
    }

    public void setWorkouts(List<Workout> newWorkouts) {
        this.workouts = newWorkouts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workout_cards, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        Workout workout = workouts.get(position);
        holder.nameTextView.setText(workout.getName());
        holder.descriptionTextView.setText(workout.getDescription());
        holder.categoryTextView.setText(workout.getCategory());
        int imageResourceId = getDrawableResourceId(holder.itemView.getContext(), workout.getPath());
        if (imageResourceId != 0) {
            holder.imageView.setImageResource(imageResourceId);
        } else {
            holder.imageView.setImageResource(R.drawable.default_image);
        }
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, descriptionTextView, categoryTextView;
        ImageView imageView;

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.workoutName);
            descriptionTextView = itemView.findViewById(R.id.workoutDescription);
            imageView = itemView.findViewById(R.id.workoutImage);
            categoryTextView = itemView.findViewById(R.id.workoutCategory);
        }
    }

    private int getDrawableResourceId(Context context, String fileName) {
        return context.getResources().getIdentifier(fileName, "drawable", context.getPackageName());
    }
}


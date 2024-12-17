package com.example.smartposture.data.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartposture.R;
import com.example.smartposture.data.model.Workout;

import java.util.ArrayList;
import java.util.List;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {
    private List<Workout> workouts;
    private List<Workout> workoutsFull; // To keep the original list for filtering
    private final OnWorkoutClickListener listener;

    public interface OnWorkoutClickListener {
        void onWorkoutClick(int workoutId);
    }

    public WorkoutAdapter(List<Workout> workouts, OnWorkoutClickListener listener) {
        this.workouts = workouts;
        this.workoutsFull = new ArrayList<>(workouts); // Keep a copy of the original list
        this.listener = listener;
    }

    public void setWorkouts(List<Workout> newWorkouts) {
        this.workouts = newWorkouts;
        this.workoutsFull = new ArrayList<>(newWorkouts); // Update the full list as well
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workout_cards, parent, false);
        return new WorkoutViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        Workout workout = workouts.get(position);
        holder.itemView.setTag(workout.getId());
        holder.nameTextView.setText(workout.getName());
        holder.descriptionTextView.setText(workout.getDescription());
        holder.categoryTextView.setText(workout.getCategory());

        int imageResourceId = getDrawableResourceId(holder.itemView.getContext(), workout.getPath());
        holder.imageView.setImageResource(imageResourceId != 0 ? imageResourceId : R.drawable.default_image);
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    public void filter(String query) {
        // Clear the current list
        List<Workout> filteredList = new ArrayList<>();

        if (query.isEmpty()) {
            // If query is empty, show the full list
            filteredList.addAll(workoutsFull);
        } else {
            // Loop through the full list and add items that match the query
            for (Workout workout : workoutsFull) {
                if (workout.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(workout);
                }
            }
        }

        // Update the list and notify the adapter
        workouts.clear();
        workouts.addAll(filteredList);
        notifyDataSetChanged();
    }

    static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, descriptionTextView, categoryTextView;
        ImageView imageView;

        public WorkoutViewHolder(@NonNull View itemView, OnWorkoutClickListener listener) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.workoutName);
            descriptionTextView = itemView.findViewById(R.id.workoutDescription);
            imageView = itemView.findViewById(R.id.workoutImage);
            categoryTextView = itemView.findViewById(R.id.workoutCategory);

            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    int workoutId = (int) v.getTag();
                    String category = nameTextView.getText().toString(); // Get the category text

                    if ("squat".equalsIgnoreCase(category)) {
                        // Proceed if the category is "squat"
                        Log.d("Adapter", "Id: " + workoutId);
                        listener.onWorkoutClick(workoutId);
                    } else {
                        // Show a toast if the category is not "squat"
                        Toast.makeText(v.getContext(), "Feature not yet available. Coming Soon", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private int getDrawableResourceId(Context context, String fileName) {
        return context.getResources().getIdentifier(fileName, "drawable", context.getPackageName());
    }
}

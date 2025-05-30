package com.example.smartposture.data.adapter;

import android.content.Context;
import android.os.Bundle;
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
import com.example.smartposture.view.fragment.ActivityDetailsFragment;
import com.example.smartposture.view.fragment.WorkoutDetailFragment;

import java.util.ArrayList;
import java.util.List;

public class ActivityWorkoutAdapter extends RecyclerView.Adapter<ActivityWorkoutAdapter.ActivityWorkoutViewHolder> {
    private List<ActivityWorkout> workouts;
    private ActivityDetailsFragment context;
    private final OnWorkoutClickListener workoutClickListener;
    private int activityId, roomId;
    private String userType;
    public interface OnWorkoutClickListener {
        void viewScore(int activity_workout_id, int repetition, int workout_id);
    }
    public ActivityWorkoutAdapter(List<ActivityWorkout> workouts, ActivityDetailsFragment context, int activityId, int roomId,  OnWorkoutClickListener listener, String userType){
        this.workouts = workouts;
        this.context = context;
        this.activityId = activityId;
        this.roomId = roomId;
        this.workoutClickListener = listener;
        this.userType = userType;
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

        if (userType.equals("trainer")){
            holder.startButton.setEnabled(false);
        } else {
            if (activity.getStatus() == 1){
                holder.startButton.setVisibility(View.GONE);
                holder.doneWorkout.setVisibility(View.VISIBLE);
            } else {
                holder.startButton.setVisibility(View.VISIBLE);
                holder.doneWorkout.setVisibility(View.GONE);
            }
        }

        holder.startButton.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putInt("activity_id", activityId);
            bundle.putInt("room_id", roomId);
            bundle.putInt("activity_workout_id", activity.getActivity_workout_id());
            bundle.putInt("workout_id", activity.getWorkout_id());
            bundle.putInt("repetition", activity.getRepetition());
            bundle.putString("from", "Room");

            WorkoutDetailFragment fragment = new WorkoutDetailFragment();
            fragment.setArguments(bundle);

            context.getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack("ActivityDetailFragment")
                    .commit();
        });

        holder.doneWorkout.setOnClickListener(view -> {
            if (workoutClickListener != null) {
                workoutClickListener.viewScore(activity.getActivity_workout_id(), activity.getRepetition(), activity.getWorkout_id());
            }
        });
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    static class ActivityWorkoutViewHolder extends RecyclerView.ViewHolder {
        TextView name, description, repetition;
        ImageView img_path;
        ImageButton startButton, doneWorkout;

        public ActivityWorkoutViewHolder(@NonNull View itemView){
            super(itemView);
            name = itemView.findViewById(R.id.workoutName);
            description = itemView.findViewById(R.id.workoutDescription);
            repetition = itemView.findViewById(R.id.repetition);
            img_path = itemView.findViewById(R.id.workoutImage);
            startButton = itemView.findViewById(R.id.startWorkout);
            doneWorkout = itemView.findViewById(R.id.doneWorkout);
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

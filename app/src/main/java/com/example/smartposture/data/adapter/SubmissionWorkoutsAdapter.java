package com.example.smartposture.data.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartposture.R;
import com.example.smartposture.data.model.ActivityWorkout;
import com.example.smartposture.view.fragment.SubmissionDetailFragment;

import java.util.ArrayList;
import java.util.List;

public class SubmissionWorkoutsAdapter extends RecyclerView.Adapter<SubmissionWorkoutsAdapter.SubmissionWorkoutsViewHolder>{
    private List<ActivityWorkout> workouts;
    private SubmissionDetailFragment context;
    private int roomId, activityId, traineeId;
    private final OnViewClickListener viewClickListener;
    public interface OnViewClickListener {
        void viewScore(int activityWorkoutId, int repetition);
    }

    public SubmissionWorkoutsAdapter(List<ActivityWorkout> workouts, SubmissionDetailFragment context, OnViewClickListener viewClickListener, int activityId, int roomId, int traineeId) {
        this.workouts = workouts;
        this.context = context;
        this.viewClickListener = viewClickListener;
        this.activityId = activityId;
        this.roomId = roomId;
        this.traineeId = traineeId;
    }

    @NonNull
    @Override
    public SubmissionWorkoutsAdapter.SubmissionWorkoutsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_submission_workouts, parent, false);
        return new SubmissionWorkoutsAdapter.SubmissionWorkoutsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubmissionWorkoutsViewHolder holder, int position) {
        ActivityWorkout activity = workouts.get(position);
        String reps = "X" +activity.getRepetition();

        holder.name.setText(activity.getWorkout_name());
        holder.description.setText(activity.getWorkout_description());
        holder.repetition.setText(reps);

        int imageResourceId = getDrawableResourceId(holder.itemView.getContext(), activity.getWorkout_img_path());
        holder.img_path.setImageResource(imageResourceId != 0 ? imageResourceId : R.drawable.default_image);

        holder.viewScore.setOnClickListener(view -> {
            if (viewClickListener != null) {
                viewClickListener.viewScore(activity.getActivity_workout_id(), activity.getRepetition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    static class SubmissionWorkoutsViewHolder extends RecyclerView.ViewHolder {
        TextView name, description, repetition;
        ImageView img_path;
        Button viewScore;

        public SubmissionWorkoutsViewHolder(@NonNull View itemView){
            super(itemView);
            name = itemView.findViewById(R.id.workoutName);
            description = itemView.findViewById(R.id.workoutDescription);
            repetition = itemView.findViewById(R.id.repetition);
            img_path = itemView.findViewById(R.id.workoutImage);
            viewScore = itemView.findViewById(R.id.viewWorkoutSubmission);
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

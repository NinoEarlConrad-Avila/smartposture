package com.example.smartposture.data.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartposture.R;
import com.example.smartposture.data.model.ActivityTrainee;
import com.example.smartposture.data.model.Trainee;

import java.util.List;

public class ActivityTraineeAdapter extends RecyclerView.Adapter<ActivityTraineeAdapter.TraineeViewHolder> {

    private List<ActivityTrainee> traineeList;
    private ViewSubmissionClickListener onViewSubmissionClickListener;
    private Context context;
    // Constructor
    public ActivityTraineeAdapter(Context context, List<ActivityTrainee> traineeList, ViewSubmissionClickListener listener) {
        this.traineeList = traineeList;
        this.onViewSubmissionClickListener = listener;
        this.context = context;
    }

    @Override
    public TraineeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the item layout
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trainees, parent, false);
        return new TraineeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TraineeViewHolder holder, int position) {
        ActivityTrainee trainee = traineeList.get(position);

        // Bind the data to the views
        holder.usernameTextView.setText(trainee.getTrainee_username());

        Drawable background = ContextCompat.getDrawable(context, R.drawable.bg_activity_status);

        if (background != null) {
            if (trainee.getStatus().equals("Not attempted")) {
                background.setTint(ContextCompat.getColor(context, R.color.not_attempted));
            } else if (trainee.getStatus().equals("In Progress")) {
                background.setTint(ContextCompat.getColor(context, R.color.in_progress));
            } else if (trainee.getStatus().equals("Submitted")) {
                background.setTint(ContextCompat.getColor(context, R.color.submitted));
            } else if (trainee.getStatus().equals("Submitted Late")) {
                background.setTint(ContextCompat.getColor(context, R.color.submitted_late));
            } else if (trainee.getStatus().equals("No Submission")) {
                background.setTint(ContextCompat.getColor(context, R.color.no_submission));
            } else {
                background.setTint(ContextCompat.getColor(context, R.color.teal));
            }

            holder.status.setBackground(background);
            holder.status.setText(trainee.getStatus());
        }

        if(trainee.getStatus().equals("Submitted") || trainee.getStatus().equals("Submitted Late")){
            holder.viewSubmissionButton.setEnabled(true);
            holder.viewSubmissionButton.setBackgroundColor(ContextCompat.getColor(context, R.color.green_med));
        }
        else{
            holder.viewSubmissionButton.setEnabled(false);
            holder.viewSubmissionButton.setBackgroundColor(ContextCompat.getColor(context, R.color.light_green));
        }

        holder.viewSubmissionButton.setOnClickListener(v -> {
            if (onViewSubmissionClickListener != null) {
                onViewSubmissionClickListener.onViewSubmissionClick(trainee);
            }
        });
    }

    @Override
    public int getItemCount() {
        return traineeList != null ? traineeList.size() : 0;
    }

    // View holder class
    public class TraineeViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView, status;
        Button viewSubmissionButton;

        public TraineeViewHolder(View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.username);
            viewSubmissionButton = itemView.findViewById(R.id.viewWorkoutSubmission);
            status = itemView.findViewById(R.id.status);
        }
    }

    // Interface to handle start workout click event
    public interface ViewSubmissionClickListener {
        void onViewSubmissionClick(ActivityTrainee trainee);
    }
}

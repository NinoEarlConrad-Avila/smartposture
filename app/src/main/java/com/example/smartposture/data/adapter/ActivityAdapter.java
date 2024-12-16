package com.example.smartposture.data.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartposture.R;
import com.example.smartposture.data.model.Activity;
import com.example.smartposture.view.fragment.ActivityDetailsFragment;
import com.example.smartposture.view.fragment.RoomDetailFragment;

import java.util.ArrayList;
import java.util.List;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder>{
    private List<Activity> activities;
    private RoomDetailFragment fragment;
    private Context context;
    private String userType;
    private int roomId;
    public ActivityAdapter(Context context, List<Activity> activities, RoomDetailFragment fragment, String userType, int roomId){
        this.context = context;
        this.activities = activities;
        this.fragment = fragment;
        this.userType = userType;
        this.roomId = roomId;
    }

       @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity, parent, false);
        return new ActivityAdapter.ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        Activity activity = activities.get(position);

        if (userType.equals("trainer"))
            holder.activityStatus.setVisibility(View.GONE);

        holder.title.setText(activity.getTitle());
        holder.description.setText(activity.getDescription());
        holder.date.setText(activity.getEnd_date());
        holder.time.setText(activity.getEnd_time());

        Drawable background = ContextCompat.getDrawable(context, R.drawable.bg_workout_category);

        if(userType.equals("trainee")){
            if (background != null) {
                if (activity.getStatus().equals("Not attempted")) {
                    background.setTint(ContextCompat.getColor(context, R.color.not_attempted));
                } else if (activity.getStatus().equals("In Progress")) {
                    background.setTint(ContextCompat.getColor(context, R.color.in_progress));
                } else if (activity.getStatus().equals("Submitted")) {
                    background.setTint(ContextCompat.getColor(context, R.color.submitted));
                } else if (activity.getStatus().equals("Submitted Late")) {
                    background.setTint(ContextCompat.getColor(context, R.color.submitted_late));
                } else if (activity.getStatus().equals("No Submission")) {
                    background.setTint(ContextCompat.getColor(context, R.color.no_submission));
                } else {
                    background.setTint(ContextCompat.getColor(context, R.color.teal));
                }

                holder.status.setBackground(background);
                holder.status.setText(activity.getStatus());
            }
        }

        holder.viewButton.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putInt("activity_id", activity.getActivity_id());
            bundle.putInt("room_id", roomId);

            ActivityDetailsFragment fragment = new ActivityDetailsFragment();
            fragment.setArguments(bundle);

            this.fragment.getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack("RoomDetailFragment")
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    static class ActivityViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, date, time, status;
        LinearLayout activityStatus;
        Button viewButton;

        public ActivityViewHolder(@NonNull View itemView){
            super(itemView);
            activityStatus = itemView.findViewById(R.id.activityStatus);
            title = itemView.findViewById(R.id.activityTitle);
            description = itemView.findViewById(R.id.activityDescription);
            date = itemView.findViewById(R.id.activityEndDate);
            time = itemView.findViewById(R.id.activityEndTime);
            status = itemView.findViewById(R.id.status);
            viewButton = itemView.findViewById(R.id.viewActivityBtn);
        }
    }

    public void updateActivities(List<Activity> newActivities) {
        if (newActivities == null) {
            newActivities = new ArrayList<>();
        }
        this.activities.clear();
        this.activities.addAll(newActivities);
        notifyDataSetChanged();
    }
}

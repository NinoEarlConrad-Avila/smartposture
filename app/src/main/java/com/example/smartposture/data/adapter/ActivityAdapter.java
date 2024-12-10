package com.example.smartposture.data.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartposture.R;
import com.example.smartposture.data.model.Activity;

import java.util.List;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder>{
    private List<Activity> activities;

    public ActivityAdapter(List<Activity> activities){
        this.activities = activities;
    }

    public void updateActivity(List<Activity> newActivities) {
        this.activities = newActivities;
        notifyDataSetChanged();
        Log.d("ActivityAdapter", "Activities updated: " + newActivities.size());
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

        holder.title.setText(activity.getTitle());
        holder.description.setText(activity.getDescription());
        holder.date.setText(activity.getEnd_date());
        holder.time.setText(activity.getEnd_time());
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    static class ActivityViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, date, time;
        Button viewButton;

        public ActivityViewHolder(@NonNull View itemView){
            super(itemView);
            title = itemView.findViewById(R.id.activityTitle);
            description = itemView.findViewById(R.id.activityDescription);
            date = itemView.findViewById(R.id.activityEndDate);
            time = itemView.findViewById(R.id.activityEndTime);
            viewButton = itemView.findViewById(R.id.viewActivityBtn);
        }
    }
}

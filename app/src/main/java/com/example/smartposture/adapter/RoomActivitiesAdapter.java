package com.example.smartposture.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartposture.R;
import com.example.smartposture.model.ActivityModel;

import java.util.ArrayList;
import java.util.List;

public class RoomActivitiesAdapter extends RecyclerView.Adapter<RoomActivitiesAdapter.ActivityViewHolder> {
    private List<ActivityModel> activities = new ArrayList<>();

    public void setActivities(List<ActivityModel> activities) {
        this.activities = activities;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity, parent, false);
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        ActivityModel activity = activities.get(position);
        holder.activityName.setText(activity.getActivityname());
        holder.dueDate.setText("Due: " + activity.getEnddate() + " " + activity.getEndtime());
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    static class ActivityViewHolder extends RecyclerView.ViewHolder {
        TextView activityName, dueDate;

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            activityName = itemView.findViewById(R.id.activityName);
            dueDate = itemView.findViewById(R.id.dueDate);
        }
    }
}


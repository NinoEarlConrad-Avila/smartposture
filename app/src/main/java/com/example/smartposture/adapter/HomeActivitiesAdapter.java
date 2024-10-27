package com.example.smartposture.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.smartposture.R;
import com.example.smartposture.model.ActivityModel;

import java.util.ArrayList;
import java.util.List;

public class HomeActivitiesAdapter extends RecyclerView.Adapter<HomeActivitiesAdapter.ActivityViewHolder> {

    private List<ActivityModel> activityList = new ArrayList<>();;

    public HomeActivitiesAdapter() {

    }

    public void setActivities(List<ActivityModel> activities) {
        activityList.clear(); // Clear the old list
        activityList.addAll(activities); // Add the new activities
        notifyDataSetChanged(); // Notify the adapter about the data change
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_home_workout, parent, false);
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        ActivityModel activity = activityList.get(position);
        holder.activityName.setText(activity.getActivityname());

        int resourceId = holder.itemView.getContext().getResources().getIdentifier("activity", "drawable", holder.itemView.getContext().getPackageName());

        Glide.with(holder.itemView.getContext())
                .load(resourceId != 0 ? resourceId : R.drawable.default_image)
                .into(holder.activityImage);
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }

    public static class ActivityViewHolder extends RecyclerView.ViewHolder {
        TextView activityName;
        ImageView activityImage;

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            activityName = itemView.findViewById(R.id.workoutName);
            activityImage = itemView.findViewById(R.id.workoutImg);
        }
    }
}

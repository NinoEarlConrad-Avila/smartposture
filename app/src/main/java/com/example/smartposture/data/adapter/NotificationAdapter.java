package com.example.smartposture.data.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import androidx.annotation.NonNull;

import com.example.smartposture.R;
import com.example.smartposture.data.model.Notification;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private final List<Notification> notifications;

    public NotificationAdapter(List<Notification> notifications){
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationAdapter.NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notifications.get(position);

        holder.description.setText(notification.getDescription());
        holder.date.setText(notification.getDate());
        holder.time.setText(notification.getTime());
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView description, date, time;

        public NotificationViewHolder(@NonNull View itemView){
            super(itemView);
            description = itemView.findViewById(R.id.notificationDescription);
            date = itemView.findViewById(R.id.notificationDate);
            time = itemView.findViewById(R.id.notificationTime);
        }
    }
}

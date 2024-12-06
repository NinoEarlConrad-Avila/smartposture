package com.example.smartposture.data.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartposture.R;
import com.example.smartposture.data.model.Trainee;

import java.util.List;

public class RoomTraineesAdapter extends RecyclerView.Adapter<RoomTraineesAdapter.RoomTraineesViewHolder> {

    private final List<Trainee> roomTrainees;
    private final RoomTraineesAdapter.OnItemActionListener listener;
    private final boolean isForAvailableTrainees;
    private final Context context;

    public interface OnItemActionListener {
        void onAction(Trainee request);
    }

    public RoomTraineesAdapter(Context context, List<Trainee> roomTrainees, RoomTraineesAdapter.OnItemActionListener listener, boolean isForAvailableTrainees) {
        this.context = context;
        this.roomTrainees = roomTrainees;
        this.listener = listener;
        this.isForAvailableTrainees = isForAvailableTrainees;
    }

    @NonNull
    @Override
    public RoomTraineesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room_trainee, parent, false);
        return new RoomTraineesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomTraineesViewHolder holder, int position) {
        Trainee request = roomTrainees.get(position);
        holder.username.setText(request.getUsername());

        if (isForAvailableTrainees) {
            holder.actionButton.setText("Add");
            holder.actionButton.setBackgroundColor(ContextCompat.getColor(context, R.color.green_med));
            holder.actionButton.setOnClickListener(v -> listener.onAction(request));
        } else {
            holder.actionButton.setText("Remove");
            holder.actionButton.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
            holder.actionButton.setOnClickListener(v -> listener.onAction(request));
        }
    }

    @Override
    public int getItemCount() {
        return roomTrainees.size();
    }

    static class RoomTraineesViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        Button actionButton;

        public RoomTraineesViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            actionButton = itemView.findViewById(R.id.actionButton);
        }
    }
}

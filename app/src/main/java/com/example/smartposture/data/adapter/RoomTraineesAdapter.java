package com.example.smartposture.data.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartposture.R;
import com.example.smartposture.data.model.Trainee;

import java.util.List;

public class RoomTraineesAdapter extends RecyclerView.Adapter<RoomTraineesAdapter.RoomTraineesViewHolder>{

    private final List<Trainee> roomTrainees;
    private final RoomTraineesAdapter.OnItemActionListener listener;

    public interface OnItemActionListener {
        void addTrainee(Trainee request);
    }

    public RoomTraineesAdapter(List<Trainee> roomTrainees, RoomTraineesAdapter.OnItemActionListener listener) {
        this.roomTrainees = roomTrainees;
        this.listener = listener;
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

        holder.actionButton.setOnClickListener(v -> listener.addTrainee(request));
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

package com.example.smartposture.data.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartposture.R;
import com.example.smartposture.data.model.Room;

public class RoomAdapter extends ListAdapter<Room, RoomAdapter.RoomViewHolder> {

    private static String mode = "myRooms";

    public void setMode(String mode) {
        this.mode = mode;
        notifyDataSetChanged();
    }

    public RoomAdapter() {
        super(Room.DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_room, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = getItem(position);
        if (room != null) {
            holder.bind(room);
        }
    }

    static class RoomViewHolder extends RecyclerView.ViewHolder {
        private final TextView roomNameTextView;
        private final TextView roomCreatorTextView;
        private Button actionButton;
        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            roomNameTextView = itemView.findViewById(R.id.roomName);
            roomCreatorTextView = itemView.findViewById(R.id.roomCreator);
            actionButton = itemView.findViewById(R.id.roomActionButton);
        }

        public void bind(Room room) {
            roomNameTextView.setText(room.getRoom_name());
            roomCreatorTextView .setText(room.getCreator_username());

            if (mode.equals("myRooms")) {
                actionButton.setText("View Room");
            } else if (mode.equals("availableRooms")) {
                actionButton.setText("Request Join");
            }

            // Handle button click (optional)
            actionButton.setOnClickListener(v -> {
                if (mode.equals("myRooms")) {
                    // Handle "View Room" action
                } else if (mode.equals("availableRooms")) {
                    // Handle "Request Join" action
                }
            });
        }
    }
}

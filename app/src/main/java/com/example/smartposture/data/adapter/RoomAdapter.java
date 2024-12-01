package com.example.smartposture.data.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartposture.R;
import com.example.smartposture.data.model.Room;

import java.util.ArrayList;
import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {
    private static OnRoomClickListener onRoomClickListener;

    private static String mode = "myRooms";
    private List<Room> rooms;

    public interface OnRoomClickListener {
        void onRoomClick(int roomId, String mode);
    }

    public RoomAdapter(OnRoomClickListener onRoomClickListener) {
        this.onRoomClickListener = onRoomClickListener;
        rooms = new ArrayList<>();
    }

    public void setMode(String mode) {
        this.mode = mode;
        notifyDataSetChanged();
    }

    public void updateRooms(List<Room> newRooms) {
        this.rooms = newRooms;
        notifyDataSetChanged();
    }

    public Room getRoomById(int roomId) {
        for (Room room : rooms) {
            if (room.getRoom_id() == roomId) {
                return room;
            }
        }
        return null;
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
        Room room = rooms.get(position);
        if (room != null) {
            holder.bind(room);
            holder.itemView.setOnClickListener(v -> onRoomClickListener.onRoomClick(room.getRoom_id(), mode));
        }
    }

    @Override
    public int getItemCount() {
        return rooms != null ? rooms.size() : 0;
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
            roomCreatorTextView.setText(room.getCreator_username());

            if (mode.equals("myRooms")) {
                actionButton.setText("View Room");
            } else if (mode.equals("availableRooms")) {
                actionButton.setText("Request Join");
            }

            actionButton.setOnClickListener(v -> {
                if (mode.equals("myRooms")) {
                    if (onRoomClickListener != null) {
                        onRoomClickListener.onRoomClick(room.getRoom_id(), mode);
                    }
                } else if (mode.equals("availableRooms")) {
                    if (onRoomClickListener != null) {
                        onRoomClickListener.onRoomClick(room.getRoom_id(), mode);
                    }
                }
            });
        }
    }
}

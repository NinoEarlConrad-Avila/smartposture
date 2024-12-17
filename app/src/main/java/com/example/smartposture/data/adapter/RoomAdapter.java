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
import com.example.smartposture.data.model.Room;

import java.util.ArrayList;
import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {
    private static OnRoomClickListener onRoomClickListener;
    private static String mode = "myRooms";
    private List<Room> fullList;
    private List<Room> filteredList;
    private final Context context;

    public interface OnRoomClickListener {
        void onRoomClick(int roomId, String mode, Button actionButton);
        void onCodeClick(int roomId, String roomCode);
    }

    public RoomAdapter(OnRoomClickListener onRoomClickListener, Context context) {
        this.onRoomClickListener = onRoomClickListener;
        this.context = context;
        fullList = new ArrayList<>();
        filteredList = new ArrayList<>();
    }

    public void setMode(String mode) {
        RoomAdapter.mode = mode;
        notifyDataSetChanged();
    }

    public void updateRooms(List<Room> newRooms) {
        this.fullList = new ArrayList<>(newRooms);
        this.filteredList = new ArrayList<>(newRooms);
        notifyDataSetChanged();
    }

    public Room getRoomById(int roomId) {
        for (Room room : filteredList) {
            if (room.getRoom_id() == roomId) {
                return room;
            }
        }
        return null;
    }

    public List<Room> getRooms() {
        return filteredList;
    }

    public int getPosition(int roomId) {
        for (int i = 0; i < filteredList.size(); i++) {
            if (filteredList.get(i).getRoom_id() == roomId) {
                return i;
            }
        }
        return -1;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_room, parent, false);
        return new RoomViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = filteredList.get(position);
        if (room != null) {
            holder.bind(room);
        }
    }

    @Override
    public int getItemCount() {
        return filteredList != null ? filteredList.size() : 0;
    }

    public void filter(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(fullList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Room room : fullList) {
                if (room.getRoom_name().toLowerCase().contains(lowerCaseQuery)) {
                    filteredList.add(room);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class RoomViewHolder extends RecyclerView.ViewHolder {
        private final TextView roomNameTextView;
        private final TextView roomCreatorTextView;
        private Button actionButton, enterCode;
        private final Context context;

        public RoomViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            roomNameTextView = itemView.findViewById(R.id.roomName);
            roomCreatorTextView = itemView.findViewById(R.id.roomCreator);
            actionButton = itemView.findViewById(R.id.roomActionButton);
            enterCode = itemView.findViewById(R.id.enterRoomCode);
        }

        public void bind(Room room) {
            roomNameTextView.setText(room.getRoom_name());
            roomCreatorTextView.setText(room.getCreator_username());

            if (mode.equals("myRooms")) {
                actionButton.setText("View Room");
                actionButton.setBackgroundColor(ContextCompat.getColor(context, R.color.bg_dark_blue));
                enterCode.setVisibility(View.GONE);
            } else if (mode.equals("availableRooms")) {
                enterCode.setVisibility(View.VISIBLE);
                if (room.getRequest_status() == 0) {
                    actionButton.setText("Requested");
                    actionButton.setBackgroundColor(ContextCompat.getColor(context, R.color.bg_dark_blue));
                } else {
                    actionButton.setText("Request Join");
                    actionButton.setBackgroundColor(ContextCompat.getColor(context, R.color.green_med));
                }
            }

            actionButton.setOnClickListener(v -> {
                if (onRoomClickListener != null) {
                    onRoomClickListener.onRoomClick(room.getRoom_id(), mode, actionButton);
                }
            });
            enterCode.setOnClickListener(v -> {
                if (onRoomClickListener != null) {
                    onRoomClickListener.onCodeClick(room.getRoom_id(), room.getRoom_code());
                }
            });
        }
    }
}

package com.example.smartposture.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartposture.R;
import com.example.smartposture.model.RoomModel;

import java.util.ArrayList;
import java.util.List;

public class SelectRoomAdapter extends RecyclerView.Adapter<SelectRoomAdapter.RoomViewHolder> {

    private List<RoomModel> roomList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(RoomModel room);
    }

    public SelectRoomAdapter(List<RoomModel> roomList, OnItemClickListener listener) {
        this.roomList = roomList;
        this.listener = listener;
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
        RoomModel room = roomList.get(position);
        holder.bind(room, listener);
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        TextView roomNameTextView;
        TextView roomCreatorTextView;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            roomNameTextView = itemView.findViewById(R.id.roomName);
            roomCreatorTextView = itemView.findViewById(R.id.roomCreator);
        }

        public void bind(final RoomModel room, final OnItemClickListener listener) {
            roomNameTextView.setText(room.getRoomName());
            roomCreatorTextView.setText(room.getRoomCreator());
            itemView.setOnClickListener(v -> listener.onItemClick(room));
        }
    }
}


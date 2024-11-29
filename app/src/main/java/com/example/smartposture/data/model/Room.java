package com.example.smartposture.data.model;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

public class Room {
    private int room_id;
    private String room_name;
    private String room_code;
    private int room_creator;
    private String creator_username;

    public Room(int room_id, String room_name, String room_code, int room_creator, String creator_username) {
        this.room_id = room_id;
        this.room_name = room_name;
        this.room_code = room_code;
        this.room_creator = room_creator;
        this.creator_username = creator_username;
    }

    public int getRoom_id() {
        return room_id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public String getRoom_code() {
        return room_code;
    }

    public void setRoom_code(String room_code) {
        this.room_code = room_code;
    }

    public int getRoom_creator() {
        return room_creator;
    }

    public void setRoom_creator(int room_creator) {
        this.room_creator = room_creator;
    }

    public String getCreator_username() {
        return creator_username;
    }

    public void setCreator_username(String creator_username) {
        this.creator_username = creator_username;
    }

    public static final DiffUtil.ItemCallback<Room> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Room>() {
                @Override
                public boolean areItemsTheSame(@NonNull Room oldItem, @NonNull Room newItem) {
                    return oldItem.room_id == newItem.room_id;
                }

                @Override
                public boolean areContentsTheSame(@NonNull Room oldItem, @NonNull Room newItem) {
                    return oldItem.equals(newItem);
                }
            };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return room_id == room.room_id &&
                room_creator == room.room_creator &&
                room_name.equals(room.room_name) &&
                room_code.equals(room.room_code) &&
                creator_username.equals(room.creator_username);
    }
}

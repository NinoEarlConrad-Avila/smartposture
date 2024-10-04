package com.example.smartposture.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartposture.model.RoomModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SelectRoomViewModel extends ViewModel {

    private final MutableLiveData<List<RoomModel>> roomList = new MutableLiveData<>();
    private final DatabaseReference roomsRef = FirebaseDatabase.getInstance().getReference("rooms");

    public SelectRoomViewModel() {
        fetchRooms();
    }

    public LiveData<List<RoomModel>> getRooms() {
        return roomList;
    }

    private void fetchRooms() {
        roomsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<RoomModel> rooms = new ArrayList<>();
                for (DataSnapshot roomSnapshot : snapshot.getChildren()) {
                    RoomModel room = roomSnapshot.getValue(RoomModel.class);
                    rooms.add(room);
                }
                roomList.setValue(rooms);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}


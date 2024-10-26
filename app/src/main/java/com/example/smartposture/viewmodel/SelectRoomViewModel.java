package com.example.smartposture.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartposture.model.RoomModel;
import com.example.smartposture.model.UserModel;
import com.example.smartposture.view.activity.MainActivity;
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

    public LiveData<List<RoomModel>> getRooms() {
        return roomList;
    }

    public void fetchRooms(Context context) {
        UserModel user = MainActivity.getUserDetails(context);
        String userType = user.getUsertype();
        String userId = user.getUsername();

        if ("trainer".equals(userType)) {
            roomsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<RoomModel> rooms = new ArrayList<>();
                    for (DataSnapshot roomSnapshot : snapshot.getChildren()) {
                        RoomModel room = roomSnapshot.getValue(RoomModel.class);
                        if (room != null && room.getRoomCreator().equals(userId)) {
                            rooms.add(room);
                        }
                    }
                    roomList.setValue(rooms);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("SelectRoomViewModel", "Database error: " + error.getMessage());
                }
            });
        } else if ("trainee".equals(userType)) {
            DatabaseReference roomTraineesRef = FirebaseDatabase.getInstance().getReference("roomtrainees");

            roomTraineesRef.orderByChild("traineeid").equalTo(userId)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            List<RoomModel> rooms = new ArrayList<>();
                            for (DataSnapshot roomTraineeSnapshot : snapshot.getChildren()) {
                                Long roomId = roomTraineeSnapshot.child("roomid").getValue(Long.class);
                                String traineeId = roomTraineeSnapshot.child("traineeid").getValue(String.class);

                                Log.d("SelectRoomViewModel", "roomid: " + roomId + ", traineeid: " + traineeId);

                                if (roomId != null) {
                                    roomsRef.child(roomId.toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot roomSnapshot) {
                                            RoomModel room = roomSnapshot.getValue(RoomModel.class);
                                            if (room != null) {
                                                rooms.add(room);
                                                roomList.setValue(rooms);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {}
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("SelectRoomViewModel", "Database error: " + error.getMessage());
                        }
                    });
        }
    }
}



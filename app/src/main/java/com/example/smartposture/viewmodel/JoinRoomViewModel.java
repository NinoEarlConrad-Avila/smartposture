package com.example.smartposture.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.smartposture.model.JoinRoomRequestModel;
import com.example.smartposture.model.RoomModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class JoinRoomViewModel extends AndroidViewModel {

    private final DatabaseReference roomsRef;
    private final MutableLiveData<String> joinRoomStatus = new MutableLiveData<>();

    public JoinRoomViewModel(@NonNull Application application) {
        super(application);
        roomsRef = FirebaseDatabase.getInstance().getReference("rooms");
    }

    public LiveData<Long> getRoomIdByCode(String roomCode) {
        MutableLiveData<Long> roomIdLiveData = new MutableLiveData<>();
        roomsRef.orderByChild("roomCode").equalTo(roomCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        RoomModel room = snapshot.getValue(RoomModel.class);
                        if (room != null) {
                            roomIdLiveData.setValue(room.getRoomID());
                            return;
                        }
                    }
                } else {
                    roomIdLiveData.setValue(-1L);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                roomIdLiveData.setValue(-1L);
            }
        });
        return roomIdLiveData;
    }

    public LiveData<Boolean> hasUserRequestedToJoin(long roomId, String username) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        DatabaseReference joinRequestsRef = FirebaseDatabase.getInstance().getReference("joinroomrequest");

        joinRequestsRef.orderByChild("roomId").equalTo(roomId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean alreadyRequested = false;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            JoinRoomRequestModel request = snapshot.getValue(JoinRoomRequestModel.class);
                            if (request != null && request.getUsername().equals(username)) {
                                alreadyRequested = true;
                                break;
                            }
                        }
                        result.setValue(alreadyRequested);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        result.setValue(false);
                    }
                });
        return result;
    }

    public void sendJoinRequest(long roomId, String username) {
        DatabaseReference joinRequestsRef = FirebaseDatabase.getInstance().getReference("joinroomrequest");
        JoinRoomRequestModel joinRequest = new JoinRoomRequestModel(roomId, 0, username);

        joinRequestsRef.push().setValue(joinRequest)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        joinRoomStatus.setValue("Join request sent successfully");
                    } else {
                        joinRoomStatus.setValue("Failed to send join request");
                    }
                });
    }

    public LiveData<String> getJoinRoomStatus() {
        return joinRoomStatus;
    }
}

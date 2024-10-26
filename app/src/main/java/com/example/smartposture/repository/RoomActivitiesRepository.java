package com.example.smartposture.repository;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.smartposture.model.ActivityModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

//public class RoomActivitiesRepository {
//    private int roomId;
//
//    public interface ActivityCallback {
//        void onSuccess(List<ActivityModel> activityList);
//        void onError(String errorMessage);
//    }
//
//    public void setRoomId(int roomId) {
//        this.roomId = roomId;
//    }
//
//    // Fetch activities with active/inactive filter
//    public void getActivitiesByRoomWithFilter(boolean showActive, ActivityCallback callback) {
//        DatabaseReference activitiesRef = FirebaseDatabase.getInstance().getReference("activities");
//
//        // Query to get activities for the room and filter by active/inactive status
//        activitiesRef.orderByChild("roomid").equalTo(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                List<ActivityModel> activitiesList = new ArrayList<>();
//                for (DataSnapshot activitySnapshot : snapshot.getChildren()) {
//                    ActivityModel activity = activitySnapshot.getValue(ActivityModel.class);
//                    if (activity != null) {
//                        boolean isActive = activity.isActive(); // Check active status based on current time and status field
//                        if ((showActive && isActive) || (!showActive && !isActive)) {
//                            Log.d("RoomFragment", "Added Room Activities");
//                            activitiesList.add(activity);
//                        }
//                    }
//                }
//                callback.onSuccess(activitiesList);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                callback.onError(error.getMessage());
//            }
//        });
//    }
//}

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class RoomActivitiesRepository {
    private final DatabaseReference databaseReference;

    public RoomActivitiesRepository() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("activities");
    }

    public LiveData<List<ActivityModel>> getActivitiesForRoom(int roomId) {
        MutableLiveData<List<ActivityModel>> activitiesLiveData = new MutableLiveData<>();
        databaseReference.orderByChild("roomid").equalTo(roomId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<ActivityModel> activities = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    ActivityModel activity = data.getValue(ActivityModel.class);
                    if (activity != null) {
                        activities.add(activity);
                    }
                }
                activitiesLiveData.postValue(activities);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
        return activitiesLiveData;
    }
}

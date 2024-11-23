package com.example.smartposture.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartposture.model.ActivityModel;
import com.example.smartposture.model.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeActivitiesViewModel extends ViewModel {

    private final MutableLiveData<List<ActivityModel>> activities = new MutableLiveData<>();
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference activitiesRef = database.getReference("activities");
    private final DatabaseReference roomTraineesRef = database.getReference("roomtrainees");
    private final DatabaseReference roomsRef = database.getReference("rooms");

    public LiveData<List<ActivityModel>> getActivities() {
        return activities;
    }

    public void fetchUserActivities(UserModel currentUser) {
        if (currentUser == null) {
            Log.e("HomeActivitiesViewModel", "UserModel is null.");
            return;
        }

        List<ActivityModel> activeActivities = new ArrayList<>();
        String userType = currentUser.getUsertype();

        if ("trainer".equals(userType)) {
            fetchActivitiesForTrainer(currentUser.getUsername(), activeActivities);
        } else if ("trainee".equals(userType)) {
            fetchActivitiesForTrainee(currentUser.getUsername(), activeActivities);
        }
    }

    private void fetchActivitiesForTrainer(String trainerUsername, List<ActivityModel> activeActivities) {
        roomsRef.orderByChild("roomCreator").equalTo(trainerUsername)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        activeActivities.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            int roomId = snapshot.child("roomID").getValue(Integer.class);
                            fetchActivitiesForRoom(roomId, activeActivities);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("HomeActivitiesViewModel", "Failed to fetch rooms for trainer: " + error.getMessage());
                    }
                });
    }

    private void fetchActivitiesForTrainee(String traineeUsername, List<ActivityModel> activeActivities) {
        roomTraineesRef.orderByChild("traineeid").equalTo(traineeUsername)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        activeActivities.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            int roomId = snapshot.child("roomid").getValue(Integer.class);
                            fetchActivitiesForRoom(roomId, activeActivities);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("HomeActivitiesViewModel", "Failed to fetch trainee's rooms: " + error.getMessage());
                    }
                });
    }

    private void fetchActivitiesForRoom(int roomId, List<ActivityModel> activeActivities) {
        activitiesRef.orderByChild("roomid").equalTo(roomId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot activitySnapshot) {
                        for (DataSnapshot activity : activitySnapshot.getChildren()) {
                            ActivityModel model = activity.getValue(ActivityModel.class);

                            if (isActivityActive(model)) {
                                activeActivities.add(model);
                            }
                        }
                        activities.setValue(activeActivities);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("HomeActivitiesViewModel", "Failed to fetch activities for room: " + error.getMessage());
                    }
                });
    }

    private boolean isActivityActive(ActivityModel model) {
        if (model == null) return false;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String endDateTimeStr = model.getEnddate() + " " + model.getEndtime();

        try {
            Date endDateTime = dateFormat.parse(endDateTimeStr);
            return new Date().before(endDateTime);
        } catch (ParseException e) {
            Log.e("HomeActivitiesViewModel", "Date parsing error: " + e.getMessage());
            return false;
        }
    }
}

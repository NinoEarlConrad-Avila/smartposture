package com.example.smartposture.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.smartposture.model.ActivityModel;
import com.example.smartposture.model.UserModel;
import com.example.smartposture.view.activity.MainActivity;
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

public class HomeActivitiesViewModel extends AndroidViewModel {

    private final MutableLiveData<List<ActivityModel>> activities = new MutableLiveData<>();
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference activitiesRef = database.getReference("activities");
    private final DatabaseReference roomTraineesRef = database.getReference("roomtrainees");
    private final DatabaseReference roomsRef = database.getReference("rooms");

    public HomeActivitiesViewModel(@NonNull Application application) {
        super(application);
        // Fetch activities based on the current user's rooms
        fetchUserActivities(application);
    }

    public LiveData<List<ActivityModel>> getActivities() {
        return activities;
    }

    private void fetchUserActivities(Application application) {
        List<ActivityModel> activeActivities = new ArrayList<>();
        UserModel currentUser = MainActivity.getUserDetails(application);

        if (currentUser != null) {
            String userType = currentUser.getUsertype();

            if ("trainer".equals(userType)) {
                // Fetch activities for rooms created by the trainer
                fetchActivitiesForTrainer(currentUser.getUsername(), activeActivities);
            } else if ("trainee".equals(userType)) {
                // Fetch activities for rooms the trainee is a member of
                fetchActivitiesForTrainee(currentUser.getUsername(), activeActivities);
            }
        }
    }

    private void fetchActivitiesForTrainer(String trainerUsername, List<ActivityModel> activeActivities) {
        roomsRef.orderByChild("roomCreator").equalTo(trainerUsername).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                activeActivities.clear(); // Clear the list before adding new activities
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    int roomId = snapshot.child("roomID").getValue(Integer.class);

                    // Fetch activities for this room
                    fetchActivitiesForRoom(roomId, activeActivities);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Log.e("HomeActivitiesViewModel", "Failed to fetch rooms for trainer: " + error.getMessage());
            }
        });
    }

    private void fetchActivitiesForTrainee(String traineeUsername, List<ActivityModel> activeActivities) {
        roomTraineesRef.orderByChild("traineeid").equalTo(traineeUsername).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                activeActivities.clear(); // Clear the list before adding new activities
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    int roomId = snapshot.child("roomid").getValue(Integer.class);

                    // Fetch activities for this room
                    fetchActivitiesForRoom(roomId, activeActivities);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Log.e("HomeActivitiesViewModel", "Failed to fetch trainee's rooms: " + error.getMessage());
            }
        });
    }

    private void fetchActivitiesForRoom(int roomId, List<ActivityModel> activeActivities) {
        activitiesRef.orderByChild("roomid").equalTo(roomId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot activitySnapshot) {
                for (DataSnapshot activity : activitySnapshot.getChildren()) {
                    ActivityModel model = activity.getValue(ActivityModel.class);

                    if (isActivityActive(model)) {
                        activeActivities.add(model);
                    }
                }
                activities.setValue(activeActivities); // Update LiveData with the latest data
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Log.e("HomeActivitiesViewModel", "Failed to fetch activities for room: " + error.getMessage());
            }
        });
    }

    private boolean isActivityActive(ActivityModel model) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

        String endDateTimeStr = model.getEnddate() + " " + model.getEndtime();

        try {
            Date endDateTime = dateFormat.parse(endDateTimeStr);
            Date currentDateTime = new Date();

            return currentDateTime.before(endDateTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}

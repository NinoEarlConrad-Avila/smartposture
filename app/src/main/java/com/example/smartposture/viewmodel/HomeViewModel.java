package com.example.smartposture.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartposture.model.HomeModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HomeViewModel extends AndroidViewModel {
    private HomeModel homeModel;
    private MutableLiveData<Integer> pushupCountLiveData;
    private MutableLiveData<Integer> squatCountLiveData;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    public HomeViewModel(Application application) {
        super(application);
        homeModel = new HomeModel(application);

        pushupCountLiveData = new MutableLiveData<>();
        pushupCountLiveData.postValue(homeModel.getPushupCount());

        squatCountLiveData = new MutableLiveData<>();
        squatCountLiveData.postValue(homeModel.getSquatCount());
    }

    public LiveData<Integer> getPushupCount() {
        return pushupCountLiveData;
    }

    public LiveData<Integer> getSquatCount() {
        return squatCountLiveData;
    }

    public void incrementPushupCount() {
        int newCount = pushupCountLiveData.getValue() + 1;
        homeModel.setPushupCount(newCount);
        pushupCountLiveData.postValue(newCount);
    }

    public void incrementSquatCount() {
        int newCount = squatCountLiveData.getValue() + 1;
        homeModel.setSquatCount(newCount);
        squatCountLiveData.postValue(newCount);
    }

    public void updatePushupCountInFirebase(int newCount) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference statsRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(userID)
                .child("stats")
                .child(currentDate);

        statsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    statsRef.child("pushup").setValue(newCount)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d("Update success", "Pushup count updated successfully.");
                                } else {
                                    Log.e("Update error", "Failed to update pushup count: " + task.getException().getMessage());
                                }
                            });
                } else {
                    Map<String, Object> newStats = new HashMap<>();
                    newStats.put("pushup", newCount);

                    statsRef.setValue(newStats)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d("Create success", "New stats created successfully.");
                                } else {
                                    Log.e("Create error", "Failed to create new stats: " + task.getException().getMessage());
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Database error", "Error checking stats: " + databaseError.getMessage());
            }
        });
    }

    public void updateSquatCountInFirebase(int newCount) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference statsRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(userID)
                .child("stats")
                .child(currentDate);

        statsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    statsRef.child("squat").setValue(newCount)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d("Update success", "Squat count updated successfully.");
                                } else {
                                    Log.e("Update error", "Failed to update squat count: " + task.getException().getMessage());
                                }
                            });
                } else {
                    Map<String, Object> newStats = new HashMap<>();
                    newStats.put("squat", newCount);

                    statsRef.setValue(newStats)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d("Create success", "New stats created successfully.");
                                } else {
                                    Log.e("Create error", "Failed to create new stats: " + task.getException().getMessage());
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Database error", "Error checking stats: " + databaseError.getMessage());
            }
        });
    }

    public void fetchStats(String userID) {

        // Get the current date in yyyymmdd format
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());

        // Reference to the stats node under the current date
        DatabaseReference statsRef = mDatabase.child("users").child(userID).child("stats").child(currentDate);

        statsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int pushup;
                int squat;

                if (dataSnapshot.exists()) {
                    // Date exists, fetch the data
                    pushup = dataSnapshot.child("pushup").getValue(Integer.class);
                    squat = dataSnapshot.child("squat").getValue(Integer.class);
                } else {
                    // Date does not exist, initialize counts to 0
                    pushup = 0;
                    squat = 0;
                    initializeStats(userID, currentDate, pushup, squat);
                }

                pushupCountLiveData.setValue(pushup);
                squatCountLiveData.setValue(squat);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Database error", "Error checking stats: " + databaseError.getMessage());
            }
        });
    }

    private void initializeStats(String userID, String currentDate, int pushup, int squat) {
        DatabaseReference statsRef = mDatabase.child("users").child(userID).child("stats").child(currentDate);

        Map<String, Object> newStats = new HashMap<>();
        newStats.put("pushup", pushup);
        newStats.put("squat", squat);

        statsRef.setValue(newStats)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Initialize success", "New stats initialized successfully.");
                    } else {
                        Log.e("Initialize error", "Failed to initialize stats: " + task.getException().getMessage());
                    }
                });
    }
}

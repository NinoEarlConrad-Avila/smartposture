package com.example.smartposture.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartposture.model.HomeModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
        squatCountLiveData = new MutableLiveData<>();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            fetchStats(currentUser.getUid());
        }
    }

    public LiveData<Integer> getPushupCount() {
        return pushupCountLiveData;
    }

    public LiveData<Integer> getSquatCount() {
        return squatCountLiveData;
    }

    public void incrementPushupCount() {
        Integer currentCount = pushupCountLiveData.getValue();
        if (currentCount == null) {
            currentCount = 0;  // Default to 0 if it's null
        }
        int newCount = currentCount + 1;
        pushupCountLiveData.postValue(newCount);
//        updatePushupCountInFirebase(newCount);
//        int newCount = pushupCountLiveData.getValue() + 1;
//        pushupCountLiveData.postValue(newCount);
//        updatePushupCountInFirebase(newCount);
    }

    public void incrementSquatCount() {
        Integer currentCount = squatCountLiveData.getValue();
        if (currentCount == null) {
            currentCount = 0;  // Default to 0 if it's null
        }
        int newCount = currentCount + 1;
        squatCountLiveData.postValue(newCount);
//        updateSquatCountInFirebase(newCount);

//        int newCount = squatCountLiveData.getValue() + 1;
//        squatCountLiveData.postValue(newCount);
//        updateSquatCountInFirebase(newCount);
    }

    private void updatePushupCountInFirebase(int newCount) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference statsRef = mDatabase.child("users").child(userID).child("stats").child(currentDate);

        statsRef.child("pushup").setValue(newCount)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Update success", "Pushup count updated successfully.");
                    } else {
                        Log.e("Update error", "Failed to update pushup count: " + task.getException().getMessage());
                    }
                });
    }

    private void updateSquatCountInFirebase(int newCount) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference statsRef = mDatabase.child("users").child(userID).child("stats").child(currentDate);

        statsRef.child("squat").setValue(newCount)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Update success", "Squat count updated successfully.");
                    } else {
                        Log.e("Update error", "Failed to update squat count: " + task.getException().getMessage());
                    }
                });
    }

    public void fetchStats(String userID) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());

        DatabaseReference statsRef = mDatabase.child("users").child(userID).child("stats").child(currentDate);

        statsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int pushup = dataSnapshot.child("pushup").exists() ? dataSnapshot.child("pushup").getValue(Integer.class) : 0;
                int squat = dataSnapshot.child("squat").exists() ? dataSnapshot.child("squat").getValue(Integer.class) : 0;

                pushupCountLiveData.setValue(pushup);
                squatCountLiveData.setValue(squat);

                if (!dataSnapshot.exists()) {
                    initializeStats(userID, currentDate, pushup, squat);
                }
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

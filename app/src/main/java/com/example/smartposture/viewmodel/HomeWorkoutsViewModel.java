package com.example.smartposture.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartposture.model.WorkoutModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeWorkoutsViewModel extends ViewModel {
    private MutableLiveData<List<WorkoutModel>> workouts;
    private DatabaseReference databaseReference;

    public HomeWorkoutsViewModel() {
        databaseReference = FirebaseDatabase.getInstance().getReference("workouts");
    }

    public LiveData<List<WorkoutModel>> getWorkouts() {
        if (workouts == null) {
            workouts = new MutableLiveData<>();
            loadWorkouts();
        }
        return workouts;
    }

    private void loadWorkouts() {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<WorkoutModel> workoutList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    WorkoutModel workout = snapshot.getValue(WorkoutModel.class);
                    workoutList.add(workout);
                }
                workouts.setValue(workoutList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database errors here
                // You can also log the error or show an error message
            }
        });
    }
}

package com.example.smartposture.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartposture.data.model.Workout;
import com.example.smartposture.data.repository.WorkoutRepository;

import java.util.List;

public class WorkoutViewModel extends ViewModel {
    private final MutableLiveData<List<Workout>> workoutsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final WorkoutRepository workoutRepository;

    public WorkoutViewModel() {
        workoutRepository = new WorkoutRepository();
    }

    public LiveData<List<Workout>> getWorkoutsLiveData() {
        return workoutsLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void fetchWorkouts() {
        workoutRepository.fetchWorkouts(new WorkoutRepository.WorkoutCallback() {
            @Override
            public void onSuccess(List<Workout> workouts) {
                Log.d("WorkoutViewModel", "Success workout fetch" +workouts);
                workoutsLiveData.postValue(workouts);
            }

            @Override
            public void onFailure(String errorMessage) {
                errorLiveData.postValue(errorMessage);
            }
        });
    }
}


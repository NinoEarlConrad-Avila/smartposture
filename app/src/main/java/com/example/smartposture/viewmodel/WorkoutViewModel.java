package com.example.smartposture.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartposture.data.model.Workout;
import com.example.smartposture.data.repository.WorkoutRepository;

import java.util.ArrayList;
import java.util.List;

public class WorkoutViewModel extends ViewModel {
    private final MutableLiveData<List<Workout>> workoutsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingStateLiveData = new MutableLiveData<>();
    private final WorkoutRepository workoutRepository;

    public WorkoutViewModel() {
        workoutRepository = new WorkoutRepository();
    }

    public LiveData<List<Workout>> getWorkoutsLiveData() {
        return workoutsLiveData;
    }

    public LiveData<Boolean> getLoadingState() {
        return loadingStateLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void fetchWorkouts() {
        loadingStateLiveData.setValue(true);
        workoutRepository.fetchWorkouts(new WorkoutRepository.WorkoutCallback() {
            @Override
            public void onSuccess(List<Workout> workouts) {
                if (workouts != null) {
                    workoutsLiveData.postValue(workouts);
                } else {
                    workoutsLiveData.postValue(new ArrayList<>());
                }
                loadingStateLiveData.setValue(false);
            }

            @Override
            public void onFailure(String errorMessage) {
                errorLiveData.postValue(errorMessage);
                loadingStateLiveData.setValue(false);
            }
        });
    }
}

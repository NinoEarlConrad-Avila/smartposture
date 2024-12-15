package com.example.smartposture.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartposture.data.model.Workout;
import com.example.smartposture.data.model.WorkoutDetail;
import com.example.smartposture.data.repository.WorkoutRepository;
import com.example.smartposture.data.request.WorkoutDetailRequest;

import java.util.ArrayList;
import java.util.List;

public class WorkoutViewModel extends ViewModel {
    private final MutableLiveData<List<Workout>> workoutsLiveData = new MutableLiveData<>();
    private final MutableLiveData<WorkoutDetail> workoutDetailLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingStateLiveData = new MutableLiveData<>();
    private final WorkoutRepository workoutRepository;

    public WorkoutViewModel() {
        workoutRepository = new WorkoutRepository();
    }

    public LiveData<List<Workout>> getWorkoutsLiveData() {
        return workoutsLiveData;
    }
    public LiveData<WorkoutDetail> getWorkoutDetail() {
        return workoutDetailLiveData;
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

    public void fetchWorkoutDetail(int workoutId) {
        WorkoutDetailRequest request = new WorkoutDetailRequest(workoutId);
        loadingStateLiveData.setValue(true);
        workoutRepository.fetchWorkoutDetail(request).observeForever(response -> {
            if (response != null && response.getWorkouts() != null) {
                workoutDetailLiveData.setValue(response.getWorkouts());
                Log.d("Test ViewModel", "ViewModel returned");
            }
            loadingStateLiveData.setValue(false);
        });
    }
}

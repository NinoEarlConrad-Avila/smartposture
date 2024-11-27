package com.example.smartposture.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartposture.data.repository.WorkoutDetailRepository;
import com.example.smartposture.data.repository.WorkoutRepository;
import com.example.smartposture.data.response.WorkoutDetailResponse;

public class WorkoutDetailViewModel extends ViewModel {
    private final WorkoutDetailRepository workoutDetailRepository;

    public WorkoutDetailViewModel() {
        workoutDetailRepository = new WorkoutDetailRepository();
    }

    public LiveData<WorkoutDetailResponse> getWorkoutDetail(int workoutId) {
        return workoutDetailRepository.getWorkoutDetail(workoutId);
    }
}


package com.example.smartposture.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartposture.data.model.Activity;
import com.example.smartposture.data.repository.ActivityRepository;

import java.util.ArrayList;
import java.util.List;

public class ActivityViewModel extends ViewModel {
    private final ActivityRepository activityRepository;
    private final MutableLiveData<Boolean> loadingStateLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Activity>> activeActivitiesLiveData = new MutableLiveData<>();

    public ActivityViewModel(){
        activityRepository = new ActivityRepository();
    }
    public LiveData<Boolean> getLoadingState() {
        return loadingStateLiveData;
    }

    public LiveData<List<Activity>> getActiveActivities(){

        return activeActivitiesLiveData;
    }

    public void fetchActiveActivities(int roomId) {
        loadingStateLiveData.setValue(true);
        activityRepository.fetchActiveActivities(roomId).observeForever(response -> {
            if (response != null && response.getActivity() != null) {
                activeActivitiesLiveData.setValue(response.getActivity());
            } else {
                activeActivitiesLiveData.setValue(new ArrayList<>());
            }
            loadingStateLiveData.setValue(false);
        });
    }
}

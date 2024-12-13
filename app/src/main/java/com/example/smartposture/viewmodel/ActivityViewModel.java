package com.example.smartposture.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartposture.data.model.Activity;
import com.example.smartposture.data.model.ActivityDetails;
import com.example.smartposture.data.repository.ActivityRepository;
import com.example.smartposture.data.request.CreateActivityRequest;
import com.example.smartposture.data.response.ActivityDetailResponse;

import java.util.ArrayList;
import java.util.List;

public class ActivityViewModel extends ViewModel {
    private final ActivityRepository activityRepository;
    private final MutableLiveData<Boolean> loadingStateLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Activity>> activeActivitiesLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Activity>> inactiveActivitiesLiveData = new MutableLiveData<>();
    private final MutableLiveData<ActivityDetailResponse> activityDetailsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> createActivityStatus= new MutableLiveData<>();


    public ActivityViewModel(){
        activityRepository = new ActivityRepository();
    }
    public LiveData<Boolean> getLoadingState() {
        return loadingStateLiveData;
    }

    public LiveData<List<Activity>> getActiveActivities(){

        return activeActivitiesLiveData;
    }
    public LiveData<List<Activity>> getInactiveActivities(){

        return inactiveActivitiesLiveData;
    }
    public LiveData<ActivityDetailResponse> getActivityDetails(){

        return activityDetailsLiveData;
    }
    public LiveData<String> getCreateActivityStatus(){
        return createActivityStatus;
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

    public void fetchInactiveActivities(int roomId) {
        loadingStateLiveData.setValue(true);
        activityRepository.fetchInactiveActivities(roomId).observeForever(response -> {
            if (response != null && response.getActivity() != null) {
                inactiveActivitiesLiveData.setValue(response.getActivity());
            } else {
                inactiveActivitiesLiveData.setValue(new ArrayList<>());
            }
            loadingStateLiveData.setValue(false);
        });
    }

    public void createActivity(int room_id, String title, String description, String end_date, String end_time, int[] workouts, int[] repetitions){
        CreateActivityRequest request = new CreateActivityRequest(room_id, title, description, end_date, end_time, workouts, repetitions);
        loadingStateLiveData.setValue(true);
        activityRepository.addRoomActivity(request).observeForever( result -> {
            if ("Success".equals(result)) {
                createActivityStatus.setValue("Success");
            } else {
                createActivityStatus.setValue(result);
            }
        });
    }

    public void fetchActivityDetails(int activityId) {
        loadingStateLiveData.setValue(true);
        activityRepository.fetchActivityDetails(activityId).observeForever(response -> {
            if (response != null && response.getActivity() != null) {
                activityDetailsLiveData.setValue(response);
            }
            loadingStateLiveData.setValue(false);
        });
    }
}

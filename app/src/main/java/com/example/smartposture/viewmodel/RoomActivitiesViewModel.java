package com.example.smartposture.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartposture.model.ActivityModel;
import com.example.smartposture.repository.RoomActivitiesRepository;

import java.util.List;

//package com.example.smartposture.viewmodel;
//
//import android.app.Activity;
//
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.MutableLiveData;
//import androidx.lifecycle.ViewModel;
//
//import com.example.smartposture.model.ActivityModel;
//import com.example.smartposture.repository.RoomActivitiesRepository;
//
//import java.util.List;
//
//public class RoomActivitiesViewModel extends ViewModel {
//    private final MutableLiveData<List<ActivityModel>> activities = new MutableLiveData<>();
//    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();
//    private final MutableLiveData<String> error = new MutableLiveData<>();
//    private final MutableLiveData<Boolean> activeFilter = new MutableLiveData<>();  // Add this for active filter
//    private final RoomActivitiesRepository repository;
//
//    public RoomActivitiesViewModel() {
//        repository = new RoomActivitiesRepository();
//        activeFilter.setValue(true);  // Default to showing active activities
//    }
//
//    // LiveData getters
//    public LiveData<List<ActivityModel>> getActivities() {
//        return activities;
//    }
//
//    public LiveData<Boolean> getLoading() {
//        return loading;
//    }
//
//    public LiveData<String> getError() {
//        return error;
//    }
//
//    public LiveData<Boolean> getActiveFilter() {  // Getter for activeFilter
//        return activeFilter;
//    }
//
//    // Method to update the filter (active/inactive)
//    public void setActiveFilter(boolean isActive) {
//        activeFilter.setValue(isActive);
//        // Reload activities based on the filter
//        loadActivitiesForRoomWithFilter();
//    }
//
//    // Call this method whenever activities are loaded and filtered
//    private void loadActivitiesForRoomWithFilter() {
//        boolean showActive = activeFilter.getValue() != null && activeFilter.getValue();
//        loading.setValue(true);
//        repository.getActivitiesByRoomWithFilter(showActive, new RoomActivitiesRepository.ActivityCallback() {
//            @Override
//            public void onSuccess(List<ActivityModel> activityList) {
//                activities.setValue(activityList);
//
//                loading.setValue(false);
//            }
//
//            @Override
//            public void onError(String errorMessage) {
//                error.setValue(errorMessage);
//                loading.setValue(false);
//            }
//        });
//    }
//
//
//    // Original method to load activities, modified to include filter logic
//    public void loadActivitiesForRoom(int roomId) {
//        repository.setRoomId(roomId); // Assume repository can handle the roomId
//        loadActivitiesForRoomWithFilter();  // Loads activities considering the filter
//    }
//}
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import java.util.List;

public class RoomActivitiesViewModel extends ViewModel {
    private final RoomActivitiesRepository repository;
    private final MutableLiveData<Integer> roomIdLiveData = new MutableLiveData<>();
    private final LiveData<List<ActivityModel>> activitiesLiveData;

    public RoomActivitiesViewModel() {
        repository = new RoomActivitiesRepository();
        activitiesLiveData = Transformations.switchMap(roomIdLiveData, repository::getActivitiesForRoom);
    }

    public void setRoomId(int roomId) {
        roomIdLiveData.setValue(roomId);
    }

    public LiveData<List<ActivityModel>> getActivities() {
        return activitiesLiveData;
    }
}

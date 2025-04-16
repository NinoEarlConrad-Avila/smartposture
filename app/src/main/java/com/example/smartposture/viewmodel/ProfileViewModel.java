package com.example.smartposture.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartposture.data.model.Statistics;
import com.example.smartposture.data.repository.ProfileRepository;
import com.example.smartposture.data.request.MonthlyWorkoutsRequest;
import com.example.smartposture.data.request.UserIdRequest;

import java.util.ArrayList;

public class ProfileViewModel extends ViewModel {
    private final ProfileRepository profileRepository;
    private final MutableLiveData<Boolean> loadingStateLiveData = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<ArrayList<Integer>>> monthlyWorkouts = new MutableLiveData<>();
    private final MutableLiveData<Statistics> profileStatistics = new MutableLiveData<>();
    public ProfileViewModel(){
        profileRepository = new ProfileRepository();
    }

    public LiveData<ArrayList<ArrayList<Integer>>> getMonthlyWorkouts(){

        return monthlyWorkouts;
    }
    public LiveData<Statistics> getProfileStatistics(){

        return profileStatistics;
    }
    public void fetchMonthlyWorkout(int trainee_id, int year){
        MonthlyWorkoutsRequest request = new MonthlyWorkoutsRequest(trainee_id, year);
        loadingStateLiveData.setValue(true);

        profileRepository.fetchMonthlyWorkouts(request).observeForever(response -> {
            if (response != null){
                monthlyWorkouts.setValue(response);
            }
            loadingStateLiveData.setValue(false);
        });
    }

    public void fetchProfileStatistics(int user_id){
        UserIdRequest request = new UserIdRequest(user_id);
        loadingStateLiveData.setValue(true);

        profileRepository.fetchProfileStatistics(request).observeForever(response -> {
            if (response != null){
                profileStatistics.setValue(response.getStatistics());
            }
            loadingStateLiveData.setValue(false);
        });
    }
}

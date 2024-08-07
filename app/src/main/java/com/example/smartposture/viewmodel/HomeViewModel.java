package com.example.smartposture.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartposture.model.HomeModel;

public class HomeViewModel extends AndroidViewModel {
    private HomeModel homeModel;
    private MutableLiveData<Integer> pushupCountLiveData;
    private MutableLiveData<Integer> squatCountLiveData;
    public HomeViewModel(Application application) {
        super(application);
        homeModel = new HomeModel(application);

        pushupCountLiveData = new MutableLiveData<>();
        pushupCountLiveData.postValue(homeModel.getPushupCount());

        squatCountLiveData = new MutableLiveData<>();
        squatCountLiveData.postValue(homeModel.getSquatCount());
    }

    public LiveData<Integer> getPushupCount() {
        return pushupCountLiveData;
    }

    public LiveData<Integer> getSquatCount() {
        return squatCountLiveData;
    }

    public void incrementPushupCount() {
        int newCount = pushupCountLiveData.getValue() + 1;
        homeModel.setPushupCount(newCount);
        pushupCountLiveData.postValue(newCount);
    }

    public void incrementSquatCount() {
        int newCount = squatCountLiveData.getValue() + 1;
        homeModel.setSquatCount(newCount);
        squatCountLiveData.postValue(newCount);
    }
}

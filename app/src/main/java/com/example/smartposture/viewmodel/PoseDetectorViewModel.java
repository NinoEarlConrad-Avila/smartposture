package com.example.smartposture.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class PoseDetectorViewModel extends ViewModel {
    private MutableLiveData<Integer> repCount =  new MutableLiveData<>(0);

    public LiveData<Integer> getRepCount() {
        return repCount;
    }

    public void updateRepetition(ArrayList<Float> scores) {
        repCount.postValue(scores.size()-1);
    }
}

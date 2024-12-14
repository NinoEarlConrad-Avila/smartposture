package com.example.smartposture.viewmodel;

import android.app.Application;

import androidx.lifecycle.ViewModel;

import com.example.smartposture.data.model.PoseDetector;

public class PoseDetectorViewModel extends ViewModel {
    private PoseDetector poseDetector;

    public PoseDetectorViewModel(Application application) {
        super();
        poseDetector = new PoseDetector(application);
    }
}

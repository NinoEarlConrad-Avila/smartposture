package com.example.smartposture.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartposture.data.model.Activity;
import com.example.smartposture.data.model.ActivityStatistics;
import com.example.smartposture.data.model.ActivityTrainee;
import com.example.smartposture.data.repository.ActivityRepository;
import com.example.smartposture.data.request.ActivityIdRequest;
import com.example.smartposture.data.request.ActivityStatisticsRequest;
import com.example.smartposture.data.request.CreateActivityRequest;
import com.example.smartposture.data.request.SubmissionDetailsRequest;
import com.example.smartposture.data.request.TraineeActivityRequest;
import com.example.smartposture.data.request.TraineeScoreRequest;
import com.example.smartposture.data.request.WorkoutScoresRequest;
import com.example.smartposture.data.response.ActivityDetailResponse;
import com.example.smartposture.data.response.SubmissionDetailResponse;

import java.util.ArrayList;
import java.util.List;

public class ActivityViewModel extends ViewModel {
    private final ActivityRepository activityRepository;
    private final MutableLiveData<Boolean> loadingStateLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Activity>> activeActivitiesTrainer = new MutableLiveData<>();
    private final MutableLiveData<List<Activity>> activeActivitiesTrainee = new MutableLiveData<>();
    private final MutableLiveData<List<Activity>> inactiveActivitiesTrainer = new MutableLiveData<>();
    private final MutableLiveData<List<Activity>> inactiveActivitiesTrainee = new MutableLiveData<>();
    private final MutableLiveData<ActivityStatistics> activityStatistics = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<Float>> workoutScores = new MutableLiveData<>();
    private final MutableLiveData<ActivityDetailResponse> activityDetailsLiveData = new MutableLiveData<>();
    private final MutableLiveData<SubmissionDetailResponse> submissionDetailLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> createActivityStatus= new MutableLiveData<>();
    private final MutableLiveData<String> traineeScoreStatus= new MutableLiveData<>();
    private final MutableLiveData<String> submitActivityStatus= new MutableLiveData<>();


    public ActivityViewModel(){
        activityRepository = new ActivityRepository();
    }
    public LiveData<Boolean> getLoadingState() {
        return loadingStateLiveData;
    }

    public LiveData<List<Activity>> getActiveActivitiesTrainer(){

        return activeActivitiesTrainer;
    }
    public LiveData<List<Activity>> getActiveActivitiesTrainee(){

        return activeActivitiesTrainee;
    }
    public LiveData<List<Activity>> getInactiveActivitiesTrainer(){

        return inactiveActivitiesTrainer;
    }
    public LiveData<List<Activity>> getInactiveActivitiesTrainee(){

        return inactiveActivitiesTrainee;
    }
    public LiveData<ActivityDetailResponse> getActivityDetails(){

        return activityDetailsLiveData;
    }

    public LiveData<SubmissionDetailResponse> getSubmissionDetail(){

        return submissionDetailLiveData;
    }

    public LiveData<ArrayList<Float>> getWorkoutScores(){

        return workoutScores;
    }
    public LiveData<ActivityStatistics> getActivityStatistics(){

        return activityStatistics;
    }
    public LiveData<String> getCreateActivityStatus(){
        return createActivityStatus;
    }
    public LiveData<String> getTraineeScoreStatus(){
        return traineeScoreStatus;
    }
    public LiveData<String> getSubmitActivityStatus(){
        return submitActivityStatus;
    }

    public void fetchActiveActivitiesTrainer(int roomId) {
        loadingStateLiveData.setValue(true);
        activityRepository.fetchActiveActivitiesTrainer(roomId).observeForever(response -> {
            if (response != null && response.getActivity() != null) {
                activeActivitiesTrainer.setValue(response.getActivity());
            } else {
                activeActivitiesTrainer.setValue(new ArrayList<>());
            }
            loadingStateLiveData.setValue(false);
        });
    }

    public void fetchActiveActivitiesTrainee(int roomId, int userId) {
        TraineeActivityRequest request = new TraineeActivityRequest(roomId, userId);
        loadingStateLiveData.setValue(true);
        activityRepository.fetchActiveActivitiesTrainee(request).observeForever(response -> {
            if (response != null && response.getActivity() != null) {
                activeActivitiesTrainee.setValue(response.getActivity());
            } else {
                activeActivitiesTrainee.setValue(new ArrayList<>());
            }
            loadingStateLiveData.setValue(false);
        });
    }

    public void fetchInactiveActivitiesTrainer(int roomId) {
        loadingStateLiveData.setValue(true);
        activityRepository.fetchInactiveActivitiesTrainer(roomId).observeForever(response -> {
            if (response != null && response.getActivity() != null) {
                inactiveActivitiesTrainer.setValue(response.getActivity());
            } else {
                inactiveActivitiesTrainer.setValue(new ArrayList<>());
            }
            loadingStateLiveData.setValue(false);
        });
    }

    public void fetchInactiveActivitiesTrainee(int roomId, int userId) {
        TraineeActivityRequest request = new TraineeActivityRequest(roomId, userId);
        loadingStateLiveData.setValue(true);
        activityRepository.fetchInactiveActivitiesTrainee(request).observeForever(response -> {
            if (response != null && response.getActivity() != null) {
                inactiveActivitiesTrainee.setValue(response.getActivity());
            } else {
                inactiveActivitiesTrainee.setValue(new ArrayList<>());
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

    public void fetchActivityDetails(int activityId, int userId) {
        ActivityIdRequest request = new ActivityIdRequest(activityId, userId);

        loadingStateLiveData.setValue(true);
        activityRepository.fetchActivityDetails(request).observeForever(response -> {
            if (response != null && response.getActivity() != null) {
                activityDetailsLiveData.setValue(response);
                Log.d("Test ViewModel", "ViewModel returned");
            }else {
                Log.d("Test ViewModel", "ViewModel no return");
            }

            loadingStateLiveData.setValue(false);
        });
    }

    public void addTraineeScore(int act_wkt_id, int user_id, ArrayList<Float> scores){
        TraineeScoreRequest request = new TraineeScoreRequest(act_wkt_id, user_id, scores);
        loadingStateLiveData.setValue(true);
        activityRepository.addTraineeScore(request).observeForever( result -> {
            if ("Success".equals(result)) {
                traineeScoreStatus.setValue("Success");
            } else {
                traineeScoreStatus.setValue(result);
            }
        });
    }

    public void submitActivity(int activity_id, int user_id){
        ActivityIdRequest request = new ActivityIdRequest(activity_id, user_id);
        loadingStateLiveData.setValue(true);
        activityRepository.submitActivity(request).observeForever( result -> {
            if ("Success".equals(result)) {
                submitActivityStatus.setValue("Success");
            } else {
                submitActivityStatus.setValue(result);
            }
        });
    }

    public LiveData<ArrayList<Float>> fetchWorkoutScores(int activityWorkoutId, int userId) {
        WorkoutScoresRequest request = new WorkoutScoresRequest(activityWorkoutId, userId);
        MutableLiveData<ArrayList<Float>> scores = new MutableLiveData<>();
        loadingStateLiveData.setValue(true);
        activityRepository.fetchWorkoutScores(request).observeForever(response -> {
            if (response != null) {
                scores.setValue(response);
            }
            loadingStateLiveData.setValue(false);
        });
        return scores;
    }

    public void fetchActivityStatistics(int roomId, int activityId) {
        ActivityStatisticsRequest request = new ActivityStatisticsRequest(roomId, activityId);
        loadingStateLiveData.setValue(true);

        activityRepository.fetchActivityStatistics(request).observeForever(response -> {
            if (response != null && response.getTrainees() != null) {
                activityStatistics.setValue(response);
            }
            loadingStateLiveData.setValue(false);
        });
    }

    public void fetchSubmissionDetails(int activityId, int traineeId) {
        SubmissionDetailsRequest request = new SubmissionDetailsRequest(activityId, traineeId);

        loadingStateLiveData.setValue(true);
        activityRepository.fetchSubmissionDetails(request).observeForever(response -> {
            if (response != null && response.getSubmission() != null) {
                submissionDetailLiveData.setValue(response);
                Log.d("Test ViewModel", "ViewModel returned");
            }else {
                Log.d("Test ViewModel", "ViewModel no return");
            }

            loadingStateLiveData.setValue(false);
        });
    }
}

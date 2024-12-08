package com.example.smartposture.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartposture.data.model.Notification;
import com.example.smartposture.data.repository.NotificationRepository;

import java.util.ArrayList;
import java.util.List;

public class NotificationViewModel extends ViewModel {
    private final NotificationRepository notificationRepository;
    private final MutableLiveData<Boolean> loadingStateLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Notification>> userNotificationLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Notification>> roomNotificationLiveData = new MutableLiveData<>();
    public NotificationViewModel() {
        notificationRepository = new NotificationRepository();
    }

    public LiveData<Boolean> getLoadingState() {
        return loadingStateLiveData;
    }
    public LiveData<List<Notification>> getUserNotifications(){

        return userNotificationLiveData;
    }
    public LiveData<List<Notification>> getRoomNotifications(){

        return roomNotificationLiveData;
    }

    public void fetchUserNotification(int userId) {
        loadingStateLiveData.setValue(true);
        notificationRepository.fetchUserNotifications(userId).observeForever(response -> {
            if (response != null && response.getNotifications() != null) {
                userNotificationLiveData.setValue(response.getNotifications());
            } else {
                userNotificationLiveData.setValue(new ArrayList<>());
            }
            loadingStateLiveData.setValue(false);
        });
    }

    public void fetchRoomNotification(int roomId) {
        loadingStateLiveData.setValue(true);
        notificationRepository.fetchRoomNotifications(roomId).observeForever(response -> {
            if (response != null && response.getNotifications() != null) {
                roomNotificationLiveData.setValue(response.getNotifications());
            } else {
                roomNotificationLiveData.setValue(new ArrayList<>());
            }
            loadingStateLiveData.setValue(false);
        });
    }
}

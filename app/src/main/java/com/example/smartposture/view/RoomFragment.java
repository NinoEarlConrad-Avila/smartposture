package com.example.smartposture.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartposture.R;
import com.example.smartposture.adapter.RoomActivitiesAdapter;
import com.example.smartposture.model.ActivityModel;
import com.example.smartposture.viewmodel.RoomActivitiesViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RoomFragment extends Fragment {
    private RecyclerView recyclerView;
    private RoomActivitiesAdapter adapter;
    private RoomActivitiesViewModel activityViewModel;
    private Button activeBtn, inactiveBtn;
    private TextView emptyMessage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room, container, false);
        recyclerView = view.findViewById(R.id.recyclerRoomActivities);
        activeBtn = view.findViewById(R.id.activeBtn);
        inactiveBtn = view.findViewById(R.id.inactiveBtn);
        emptyMessage = view.findViewById(R.id.emptyMessage);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RoomActivitiesAdapter();
        recyclerView.setAdapter(adapter);

        activityViewModel = new ViewModelProvider(this).get(RoomActivitiesViewModel.class);
        long roomId = getArguments().getLong("roomid", -1);
        if (roomId != -1) {
            activityViewModel.setRoomId((int) roomId);
        }

        // Observe activities and set default view to active activities
        activityViewModel.getActivities().observe(getViewLifecycleOwner(), activities -> {
            showActiveActivities(activities);
            highlightButton(activeBtn, inactiveBtn);

            activeBtn.setOnClickListener(v -> {
                showActiveActivities(activities);
                highlightButton(activeBtn, inactiveBtn);
            });

            inactiveBtn.setOnClickListener(v -> {
                showInactiveActivities(activities);
                highlightButton(inactiveBtn, activeBtn);
            });
        });

        return view;
    }

    private void showActiveActivities(List<ActivityModel> activities) {
        long currentTime = System.currentTimeMillis();
        List<ActivityModel> activeActivities = new ArrayList<>();
        for (ActivityModel activity : activities) {
            long endTime = parseDateTime(activity.getEnddate(), activity.getEndtime());
            if (currentTime < endTime) {
                activeActivities.add(activity);
            }
        }
        if (activeActivities.isEmpty()) {
            emptyMessage.setText("No active activities");
            emptyMessage.setVisibility(View.VISIBLE);
        } else {
            emptyMessage.setVisibility(View.GONE);
        }

        adapter.setActivities(activeActivities);
    }

    private void showInactiveActivities(List<ActivityModel> activities) {
        long currentTime = System.currentTimeMillis();
        List<ActivityModel> inactiveActivities = new ArrayList<>();
        for (ActivityModel activity : activities) {
            long endTime = parseDateTime(activity.getEnddate(), activity.getEndtime());
            if (currentTime > endTime) {
                inactiveActivities.add(activity);
            }
        }
        if (inactiveActivities.isEmpty()) {
            emptyMessage.setText("No inactive activities");
            emptyMessage.setVisibility(View.VISIBLE);
        } else {
            emptyMessage.setVisibility(View.GONE);
        }

        adapter.setActivities(inactiveActivities);
    }

    private long parseDateTime(String date, String time) {
        String dateTimeString = date + " " + time;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

        try {
            Date parsedDate = dateFormat.parse(dateTimeString);
            if (parsedDate != null) {
                return parsedDate.getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }

    private void highlightButton(Button selectedBtn, Button otherBtn) {
        // Highlight the selected button and reset the other button
        selectedBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.teal));
        selectedBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.white));

        otherBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.light_green));
        otherBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
    }
}

package com.example.smartposture.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartposture.R;
import com.example.smartposture.data.adapter.RoomAdapter;
import com.example.smartposture.util.AdditionalSpace;
import com.example.smartposture.viewmodel.RoomViewModel;

public class SelectRoomFragment extends Fragment {
    private RoomViewModel viewModel;
    private RecyclerView recyclerViewRooms;
    private RoomAdapter adapter;
    private Button myRooms, availableRooms;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_room, container, false);

        recyclerViewRooms = view.findViewById(R.id.recyclerViewRooms);
        myRooms = view.findViewById(R.id.myRooms);
        availableRooms = view.findViewById(R.id.availableRooms);
        viewModel = new ViewModelProvider(this).get(RoomViewModel.class);

        setupRecyclerView();
        observeViewModel();


        highlightButton(myRooms, availableRooms);
        fetchMyRooms(viewModel);

        myRooms.setOnClickListener(v -> {
            highlightButton(myRooms, availableRooms);
            fetchMyRooms(viewModel);
        });

        availableRooms.setOnClickListener(v -> {
            highlightButton(availableRooms, myRooms);
            fetchAvailableRooms(viewModel);
        });

        return view;
    }

    private void setupRecyclerView() {
        int spaceInPixels = getResources().getDimensionPixelSize(R.dimen.recyclerViewSpacing);
        adapter = new RoomAdapter();
        recyclerViewRooms.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewRooms.setAdapter(adapter);
        recyclerViewRooms.addItemDecoration(new AdditionalSpace(spaceInPixels));
    }

    private void observeViewModel() {
        viewModel.getRoomsLiveData().observe(getViewLifecycleOwner(), rooms -> {
            if (rooms != null) {
                adapter.submitList(rooms);
            }
        });

        viewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchMyRooms(RoomViewModel roomViewModel) {
        int userId = getUserId();
        if (userId != -1) {
            roomViewModel.fetchRooms(userId);
        } else {
            Toast.makeText(requireContext(), "User ID not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchAvailableRooms(RoomViewModel roomViewModel) {
        roomViewModel.fetchRooms(0);
    }

    private int getUserId() {
        return 18;
    }

    private void highlightButton(Button selectedBtn, Button otherBtn) {
        selectedBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.teal));
        selectedBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.white));

        otherBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.light_green));
        otherBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
    }
}

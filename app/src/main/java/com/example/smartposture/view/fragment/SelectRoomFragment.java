package com.example.smartposture.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartposture.R;
import com.example.smartposture.data.adapter.RoomAdapter;
import com.example.smartposture.data.model.Room;
import com.example.smartposture.data.sharedpreference.SharedPreferenceManager;
import com.example.smartposture.util.AdditionalSpace;
import com.example.smartposture.viewmodel.RoomViewModel;

public class SelectRoomFragment extends BaseFragment implements RoomAdapter.OnRoomClickListener {
    private RoomViewModel viewModel;
    private RecyclerView recyclerViewRooms;
    private RoomAdapter adapter;
    private Button myRooms, availableRooms;
    private SharedPreferenceManager spManager;
    private TextView noRoomsText;
    private String userType;
    private LinearLayout layoutButtons, layoutTrainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_room, container, false);

        recyclerViewRooms = view.findViewById(R.id.recyclerViewRooms);
        myRooms = view.findViewById(R.id.myRooms);
        availableRooms = view.findViewById(R.id.availableRooms);
        noRoomsText = view.findViewById(R.id.noRoomsText);
        layoutButtons = view.findViewById(R.id.linearLayoutButtons);
        layoutTrainer = view.findViewById(R.id.linearLayoutTrainer);

        viewModel = new ViewModelProvider(this).get(RoomViewModel.class);

        spManager = getSharedPreferenceManager();
        userType = spManager.getUserType();

        setupRecyclerView();
        observeViewModel();

        if ("trainee".equals(userType)) {
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
        } else if ("trainer".equals(userType)) {
            layoutButtons.setVisibility(View.GONE);
            layoutTrainer.setVisibility(View.VISIBLE);
            fetchMyRooms(viewModel);
        }

        return view;
    }

    @Override
    public void onRoomClick(int roomId, String mode) {
        if (roomId != -1) {
            Room selectedRoom = adapter.getRoomById(roomId);
            if (selectedRoom != null) {
                viewModel.selectRoom(selectedRoom);
            }
        } else {
            Toast.makeText(requireContext(), "Invalid Room ID", Toast.LENGTH_SHORT).show();
        }
    }


    private void setupRecyclerView() {
        int spaceInPixels = getResources().getDimensionPixelSize(R.dimen.recyclerViewSpacing);
        adapter = new RoomAdapter(this);
        recyclerViewRooms.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewRooms.setAdapter(adapter);
        recyclerViewRooms.addItemDecoration(new AdditionalSpace(spaceInPixels));
    }

    private void observeViewModel() {
        viewModel.getRoomsLiveData().observe(getViewLifecycleOwner(), rooms -> {
            if (rooms != null && !rooms.isEmpty()) {
                adapter.updateRooms(rooms);
                recyclerViewRooms.setVisibility(View.VISIBLE);
                noRoomsText.setVisibility(View.GONE);
            } else {
                recyclerViewRooms.setVisibility(View.GONE);
                noRoomsText.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getSelectedRoomLiveData().observe(getViewLifecycleOwner(), room -> {
            if (room != null) {
                navigateToRoomDetails(room.getRoom_id());
                viewModel.selectRoom(null);
            }
        });

        viewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchMyRooms(RoomViewModel roomViewModel) {
        adapter.setMode("myRooms");
        int userId = getUserId();
        if (userId != -1) {
            if ("trainee".equals(userType)) {
                roomViewModel.fetchTraineeRooms(userId);
            } else if ("trainer".equals(userType)) {
                roomViewModel.fetchTrainerRooms(userId);
            }
        } else {
            Toast.makeText(requireContext(), "User ID not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchAvailableRooms(RoomViewModel roomViewModel) {
        adapter.setMode("availableRooms");
        int userId = getUserId();
        if (userId != -1) {
            roomViewModel.fetchTraineeAvailableRooms(userId);
        } else {
            Toast.makeText(requireContext(), "User ID not found", Toast.LENGTH_SHORT).show();
        }
    }

    private int getUserId() {
        return spManager.getUserId();
    }

    private void highlightButton(Button selectedBtn, Button otherBtn) {
        selectedBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.teal));
        selectedBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.white));

        otherBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.light_green));
        otherBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
    }

    private void navigateToRoomDetails(int roomId) {
        Bundle bundle = new Bundle();
        bundle.putInt("room_id", roomId);

        RoomDetailFragment fragment = new RoomDetailFragment();
        fragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack("RoomDetailFragment")
                .commit();
    }
}

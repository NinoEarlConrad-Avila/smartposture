package com.example.smartposture.view.fragment;

import android.os.Bundle;
import android.util.Log;
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
import com.example.smartposture.data.sharedpreference.SharedPreferenceManager;
import com.example.smartposture.util.AdditionalSpace;
import com.example.smartposture.viewmodel.RoomViewModel;

public class SelectRoomFragment extends BaseFragment {
    private RoomViewModel viewModel;
    private RecyclerView recyclerViewRooms;
    private RoomAdapter adapter;
    private Button myRooms, availableRooms;
    private SharedPreferenceManager spManager;
    private TextView noRoomsText;
    private String usertype;
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
        usertype = spManager.getUserType();
        Log.d("Type", "User Type: " +usertype);
        setupRecyclerView();
        observeViewModel();

        if (usertype.equals("trainee")) {
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
        } else if (usertype.equals("trainer")){
            layoutButtons.setVisibility(View.GONE);
            layoutTrainer.setVisibility(View.VISIBLE);
            fetchMyRooms(viewModel);
        }

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
            if (rooms != null && !rooms.isEmpty()) {
                adapter.submitList(rooms);
                recyclerViewRooms.setVisibility(View.VISIBLE);
                noRoomsText.setVisibility(View.GONE);
            } else {
                recyclerViewRooms.setVisibility(View.GONE);
                noRoomsText.setVisibility(View.VISIBLE);
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
            if (usertype.equals("trainee")) {
                roomViewModel.fetchTraineeRooms(userId);
            } else if(usertype.equals("trainer")){
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
        int id = spManager.getUserId();
        Log.d("USER ID: ", "user_id: " +id);
        return id;
    }

    private void highlightButton(Button selectedBtn, Button otherBtn) {
        selectedBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.teal));
        selectedBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.white));

        otherBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.light_green));
        otherBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
    }
}

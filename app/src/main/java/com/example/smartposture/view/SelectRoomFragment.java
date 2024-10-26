package com.example.smartposture.view;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.smartposture.R;
import com.example.smartposture.model.RoomModel;
import com.example.smartposture.model.UserModel;
import com.example.smartposture.viewmodel.SelectRoomViewModel;
import com.example.smartposture.adapter.SelectRoomAdapter;

public class SelectRoomFragment extends Fragment {

    private SelectRoomViewModel viewModel;
    private RecyclerView recyclerView;
    private SelectRoomAdapter adapter;
    private Button roomButton;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_room, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewRooms);
        roomButton = view.findViewById(R.id.joinRoom);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel = new ViewModelProvider(this).get(SelectRoomViewModel.class);
        UserModel user = MainActivity.getUserDetails(requireContext());

        if (user != null) {
            String userType = user.getUsertype();
            if ("trainer".equals(userType)) {
                roomButton.setText("Create Room");
                roomButton.setOnClickListener(v -> showCreateRoomDialog());
            } else if ("trainee".equals(userType)) {
                roomButton.setText("Join Room");
                roomButton.setOnClickListener(v -> showJoinRoomDialog());
            }
        }

        viewModel.fetchRooms(requireContext());

        viewModel.getRooms().observe(getViewLifecycleOwner(), rooms -> {
            adapter = new SelectRoomAdapter(rooms, room -> {
                RoomModel selectedRoom = new RoomModel(room.getRoomCode(), room.getRoomCreator(), room.getRoomID(), room.getRoomName());

                Bundle bundle = new Bundle();
                bundle.putSerializable("roomDetails", selectedRoom);
                bundle.putLong("roomid", room.getRoomID()); // Pass room ID here

                RoomFragment roomFragment = new RoomFragment();
                roomFragment.setArguments(bundle);

                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, roomFragment)
                        .addToBackStack(null)
                        .commit();
            });
            recyclerView.setAdapter(adapter);
        });


        return view;
    }
    private void showCreateRoomDialog() {
        CreateRoomDialog dialog = new CreateRoomDialog();
        dialog.show(getChildFragmentManager(), "CreateRoomDialog");
    }

    private void showJoinRoomDialog() {
        JoinRoomDialog dialog = new JoinRoomDialog();
        dialog.show(getChildFragmentManager(), "JoinRoomDialog");
    }
}


package com.example.smartposture.view.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.smartposture.R;
import com.example.smartposture.model.RoomModel;
import com.example.smartposture.model.UserModel;
import com.example.smartposture.view.activity.MainActivity;
import com.example.smartposture.view.dialog.CreateRoomDialog;
import com.example.smartposture.view.dialog.JoinRoomDialog;
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

        // Use the correct ViewModelProvider constructor for Fragment lifecycle:
        viewModel = new ViewModelProvider(this).get(SelectRoomViewModel.class);
        // Use parent activity's lifecycle
        UserModel user = MainActivity.getUserDetails(requireContext());

        if (user != null) {
            String userType = user.getUsertype();
            roomButton.setText("trainer".equals(userType) ? "Create Room" : "Join Room");
            roomButton.setOnClickListener(v -> {
                if ("trainer".equals(userType)) {
                    showCreateRoomDialog();
                } else {
                    showJoinRoomDialog(user.getUsername());
                }
            });
        }

        viewModel.fetchRooms(requireContext());

        viewModel.getRooms().observe(getViewLifecycleOwner(), rooms -> {
            adapter = new SelectRoomAdapter(rooms, room -> {

                Bundle bundle = new Bundle();
                bundle.putSerializable("roomDetails", room);
                bundle.putLong("roomid", room.getRoomID());

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


    private void showJoinRoomDialog(String username) {
        JoinRoomDialog dialog = new JoinRoomDialog();

        Bundle args = new Bundle();
        args.putString("username", username);
        dialog.setArguments(args);

        dialog.show(getChildFragmentManager(), "JoinRoomDialog");
    }
}


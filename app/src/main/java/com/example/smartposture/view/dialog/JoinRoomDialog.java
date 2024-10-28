package com.example.smartposture.view.dialog;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartposture.R;
import com.example.smartposture.viewmodel.JoinRoomViewModel;

public class JoinRoomDialog extends DialogFragment {
    private EditText roomCodeInput;
    private Button sendRequestButton;
    private JoinRoomViewModel joinRoomViewModel;
    private String username;
    private ImageView closeButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_join_room, container, false);
        roomCodeInput = view.findViewById(R.id.inputRoomCode);
        sendRequestButton = view.findViewById(R.id.btnSendRequest);
        closeButton = view.findViewById(R.id.close);
        joinRoomViewModel = new ViewModelProvider(this).get(JoinRoomViewModel.class);
        username = getArguments() != null ? getArguments().getString("username") : null;
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        closeButton.setOnClickListener(v -> dismiss());

        joinRoomViewModel.getJoinRoomStatus().observe(getViewLifecycleOwner(), status -> {
            Toast.makeText(getContext(), status, Toast.LENGTH_SHORT).show();
            if ("Join request sent successfully".equals(status)) {
                dismiss();
            } else {
                sendRequestButton.setEnabled(true);
            }
        });

        sendRequestButton.setOnClickListener(v -> {
            String roomCode = roomCodeInput.getText().toString().trim();
            Log.d("Join Room: ", roomCode + " " +username);
            if (!roomCode.isEmpty() && username != null) {
                sendRequestButton.setEnabled(false);
                joinRoomViewModel.getRoomIdByCode(roomCode).observe(getViewLifecycleOwner(), roomId -> {
                    if (roomId != null && roomId != -1) {
                        joinRoomViewModel.hasUserRequestedToJoin(roomId, username).observe(getViewLifecycleOwner(), alreadyRequested -> {
                            if (alreadyRequested) {
                                roomCodeInput.setError("You have already requested to join this room.");
                                sendRequestButton.setEnabled(true);
                            } else {
                                joinRoomViewModel.sendJoinRequest(roomId, username);
                            }
                        });
                    } else {
                        roomCodeInput.setError("Room code does not exist.");
                        sendRequestButton.setEnabled(true);
                    }
                });
            } else {
                roomCodeInput.setError("Room code cannot be empty");
            }
        });
    }
}

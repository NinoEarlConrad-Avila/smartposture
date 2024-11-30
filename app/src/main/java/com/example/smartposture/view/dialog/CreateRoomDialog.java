package com.example.smartposture.view.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartposture.R;
import com.example.smartposture.model.RoomModel;
import com.example.smartposture.view.activity.MainActivity;
import com.example.smartposture.viewmodel.SelectRoomViewModel;

import java.util.Random;

public class CreateRoomDialog extends DialogFragment {

    private EditText inputRoomName;
    private Button btnSendRequest;
    private ImageView close;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setContentView(R.layout.dialog_create_room); // Ensure you have this layout

        inputRoomName = dialog.findViewById(R.id.input_room_name);
        btnSendRequest = dialog.findViewById(R.id.btnSendRequest);
        close = dialog.findViewById(R.id.close);

        close.setOnClickListener(v -> dismiss());

        btnSendRequest.setOnClickListener(v -> {
            String roomName = inputRoomName.getText().toString();
            String roomCode = generateRoomCode();
            // Assuming you have access to ViewModel here
            SelectRoomViewModel viewModel = new ViewModelProvider(requireActivity()).get(SelectRoomViewModel.class);
//            String roomCreator = MainActivity.getUserDetails(requireContext()).getUsername();
//            RoomModel newRoom = new RoomModel(roomCode, roomCreator, null, roomName);
//            viewModel.addRoom(newRoom); // Add this method to ViewModel
            dismiss();
        });

        return dialog;
    }
    private String generateRoomCode() {
        StringBuilder roomCode = new StringBuilder();

        // Generate 2 random uppercase letters
        for (int i = 0; i < 2; i++) {
            char letter = (char) ('A' + new Random().nextInt(26)); // 'A' to 'Z'
            roomCode.append(letter);
        }

        // Generate 4 random digits
        for (int i = 0; i < 4; i++) {
            int digit = new Random().nextInt(10); // 0 to 9
            roomCode.append(digit);
        }

        return roomCode.toString();
    }

}

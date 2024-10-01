package com.example.smartposture.view;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.example.smartposture.R;

public class JoinRoomDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate custom layout for modal
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_join_room, null);

        // Find views in the layout
        ImageView closeIcon = view.findViewById(R.id.close);
        EditText roomCodeInput = view.findViewById(R.id.modal_input);
        Button sendRequestButton = view.findViewById(R.id.btnSendRequest);

        // Close button listener to dismiss the dialog
        closeIcon.setOnClickListener(v -> dismiss());

        // Send request button listener
        sendRequestButton.setOnClickListener(v -> {
            String roomCode = roomCodeInput.getText().toString().trim();
            // Add logic to handle the room code
            if (!roomCode.isEmpty()) {
                // Example: Send the room code to the parent activity or fragment
                // or handle room join request here
                // For example: joinRoom(roomCode);
                dismiss(); // Optionally dismiss the dialog after sending the request
            } else {
                roomCodeInput.setError("Room code cannot be empty");
            }
        });

        // Configure the builder
        builder.setView(view)
                .setPositiveButton(null, null) // Remove default buttons
                .setNegativeButton(null, null);

        return builder.create();
    }
}

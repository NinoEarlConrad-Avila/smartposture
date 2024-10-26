package com.example.smartposture.view.dialog;

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

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_join_room, null);

        ImageView closeIcon = view.findViewById(R.id.close);
        EditText roomCodeInput = view.findViewById(R.id.modal_input);
        Button sendRequestButton = view.findViewById(R.id.btnSendRequest);

        closeIcon.setOnClickListener(v -> dismiss());

        sendRequestButton.setOnClickListener(v -> {
            String roomCode = roomCodeInput.getText().toString().trim();

            if (!roomCode.isEmpty()) {

                dismiss();
            } else {
                roomCodeInput.setError("Room code cannot be empty");
            }
        });

        builder.setView(view)
                .setPositiveButton(null, null) // Remove default buttons
                .setNegativeButton(null, null);

        return builder.create();
    }
}

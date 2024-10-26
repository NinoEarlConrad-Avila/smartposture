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
public class CreateRoomDialog extends DialogFragment{
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_create_room, null);

        ImageView closeIcon = view.findViewById(R.id.close);
        EditText roomNameInput = view.findViewById(R.id.input_room_name);
        EditText roomNameDescription = view.findViewById(R.id.input_room_description);
        Button sendRequestButton = view.findViewById(R.id.btnSendRequest);

        closeIcon.setOnClickListener(v -> dismiss());

        sendRequestButton.setOnClickListener(v -> {
            String roomName = roomNameInput.getText().toString().trim();
            String roomDescription = roomNameDescription.getText().toString().trim();

            if (!roomName.isEmpty() & !roomDescription.isEmpty()) {

                dismiss();
            } else {
                if(roomName.isEmpty()) {
                    roomNameInput.setError("Room name cannot be empty");
                }
                if(roomDescription.isEmpty()){
                    roomNameDescription.setError("Room description cannot be empty");
                }
            }
        });

        builder.setView(view)
                .setPositiveButton(null, null)
                .setNegativeButton(null, null);

        return builder.create();
    }
}

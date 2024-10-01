package com.example.smartposture.view;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.smartposture.R;

public class RoomFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room, container, false);

        Button joinRoom = view.findViewById(R.id.joinRoom);
        LinearLayout room1 = view.findViewById(R.id.room1);
        LinearLayout room2 = view.findViewById(R.id.room2);
        LinearLayout room3 = view.findViewById(R.id.room3);

        joinRoom.setOnClickListener(v -> {
            JoinRoomDialog modalDialog = new JoinRoomDialog();

            modalDialog.show(requireActivity().getSupportFragmentManager(), "JoinRoomModal");
        });
        return view;
    }
}

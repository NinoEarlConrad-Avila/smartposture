package com.example.smartposture.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartposture.R;
import com.example.smartposture.model.RoomModel;

public class RoomFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room, container, false);

        TextView roomCodeTextView = view.findViewById(R.id.roomCodeTextView);
        TextView roomCreatorTextView = view.findViewById(R.id.roomCreatorTextView);
        TextView roomIdTextView = view.findViewById(R.id.roomIDTextView);
        TextView roomNameTextView = view.findViewById(R.id.roomNameTextView);

        Bundle args = getArguments();
        if (args != null) {
            RoomModel roomDetails = (RoomModel) args.getSerializable("roomDetails");

            if (roomDetails != null) {
                roomCodeTextView.setText(roomDetails.getRoomCode());
                roomCreatorTextView.setText(roomDetails.getRoomCreator());
                roomIdTextView.setText(String.valueOf(roomDetails.getRoomID()));
                roomNameTextView.setText(roomDetails.getRoomName());
            }
        }

        return view;
    }
}

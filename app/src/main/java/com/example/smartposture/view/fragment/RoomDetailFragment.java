package com.example.smartposture.view.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartposture.R;
import com.example.smartposture.data.adapter.JoinRequestAdapter;
import com.example.smartposture.data.model.JoinRequest;
import com.example.smartposture.data.model.Room;
import com.example.smartposture.viewmodel.RoomDetailViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class RoomDetailFragment extends Fragment {
    private RoomDetailViewModel roomViewModel;
    private LinearLayout viewJoinRequest;
    private TextView roomName, roomCreator, roomCode;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room, container, false);

        if (getActivity() != null) {
            BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottom_navigation);
            if (bottomNav != null) {
                bottomNav.setVisibility(View.GONE);
            }
        }

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
            new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    requireActivity().getSupportFragmentManager().popBackStack();
                }
            });

        roomName = view.findViewById(R.id.roomNameTextView);
        roomCreator = view.findViewById(R.id.roomCreatorTextView);
        roomCode = view.findViewById(R.id.roomCode);
        viewJoinRequest = view.findViewById(R.id.viewJoinRequest);

        view.setVisibility(View.INVISIBLE);
        Dialog loadingDialog = createFullScreenLoadingDialog();
        loadingDialog.show();

        roomViewModel = new ViewModelProvider(this).get(RoomDetailViewModel.class);

        int roomId = requireArguments().getInt("room_id", -1);

        viewJoinRequest.setOnClickListener(v -> {
            showJoinRequestDialog(roomId);
        });

        roomViewModel.fetchRoomDetails(roomId).observe(getViewLifecycleOwner(), response -> {
            Room room = response.getRoom();
                roomName.setText(room.getRoom_name());
                roomCreator.setText(room.getCreator_username());
                roomCode.setText(room.getRoom_code());

                view.setVisibility(View.VISIBLE);
                loadingDialog.dismiss();
            }
        );
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() != null) {
            BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottom_navigation);
            if (bottomNav != null) {
                bottomNav.setVisibility(View.VISIBLE);
            }
        }
    }

    private Dialog createFullScreenLoadingDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_loading);
        dialog.setCancelable(false);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        ImageView preloaderImage = dialog.findViewById(R.id.preloaderImage);
        Animation bounceAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.logo_bounce);
        preloaderImage.startAnimation(bounceAnimation);

        return dialog;
    }

    private void showJoinRequestDialog(int roomId) {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_join_request);
        dialog.setCancelable(true);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setDimAmount(0.5f);
        }
        ImageView closeButton = dialog.findViewById(R.id.close);
        closeButton.setOnClickListener(v -> dialog.dismiss());

        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        RoomDetailViewModel viewModel = new ViewModelProvider(this).get(RoomDetailViewModel.class);

        viewModel.fetchJoinRequests(roomId).observe(getViewLifecycleOwner(), response -> {
            if (response != null && response.getRequests() != null) {
                JoinRequestAdapter adapter = new JoinRequestAdapter(response.getRequests(), new JoinRequestAdapter.OnItemActionListener() {
                    @Override
                    public void onAccept(JoinRequest request) {
                        // Handle accept logic here
                    }

                    @Override
                    public void onReject(JoinRequest request) {
                        // Handle reject logic here
                    }
                });

                recyclerView.setAdapter(adapter);
            }
        });

        dialog.show();
    }
}

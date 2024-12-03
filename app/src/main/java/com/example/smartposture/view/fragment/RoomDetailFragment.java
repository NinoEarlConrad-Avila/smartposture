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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
        Dialog dialog = createJoinRequestDialog();

        RelativeLayout layout = dialog.findViewById(R.id.preloaderLayout);
        RelativeLayout noRequest = dialog.findViewById(R.id.noJoinRequest);
        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerView);
        ImageView preloaderImage = dialog.findViewById(R.id.preloaderImage);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        layout.setVisibility(View.VISIBLE);
        preloaderImage.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        noRequest.setVisibility(View.GONE);

        Animation animation = AnimationUtils.loadAnimation(requireContext(), R.anim.logo_bounce);
        preloaderImage.startAnimation(animation);

        RoomDetailViewModel viewModel = new ViewModelProvider(this).get(RoomDetailViewModel.class);

        viewModel.fetchJoinRequests(roomId).observe(getViewLifecycleOwner(), joinRequests -> {
            if (joinRequests == null || joinRequests.isEmpty()) {
                noRequest.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                recyclerView.setAdapter(new JoinRequestAdapter(joinRequests, new JoinRequestAdapter.OnItemActionListener() {
                    @Override
                    public void onAccept(JoinRequest request) {
                        showConfirmationDialog("Accept Request", "Are you sure you want to accept this request?",
                                () -> viewModel.acceptJoinRequest(roomId, request.getUser_id()));
                    }

                    @Override
                    public void onReject(JoinRequest request) {
                        showConfirmationDialog("Reject Request", "Are you sure you want to reject this request?",
                                () -> viewModel.rejectJoinRequest(roomId, request.getUser_id()));
                    }
                }));
                recyclerView.setVisibility(View.VISIBLE);
                noRequest.setVisibility(View.GONE);
            }
        });

        viewModel.getLoadingState().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                recyclerView.setVisibility(View.GONE);
                noRequest.setVisibility(View.GONE);
                layout.setVisibility(View.VISIBLE);
                preloaderImage.setVisibility(View.VISIBLE);
                preloaderImage.startAnimation(animation);
            } else {
                layout.setVisibility(View.GONE);
                preloaderImage.clearAnimation();
                preloaderImage.setVisibility(View.GONE);
            }
        });

        dialog.show();
    }

    private Dialog createJoinRequestDialog() {
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

        return dialog;
    }

    private void showConfirmationDialog(String title, String message, Runnable onConfirm) {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dialog_confirmation);
        dialog.setCancelable(true);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setDimAmount(0.5f);
        }

        TextView titleTextView = dialog.findViewById(R.id.alertTitle);
        TextView messageTextView = dialog.findViewById(R.id.alertMessage);
        titleTextView.setText(title);
        messageTextView.setText(message);

        Button btnConfirm = dialog.findViewById(R.id.btnConfirm);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);

        btnConfirm.setOnClickListener(v -> {
            dialog.dismiss();
            onConfirm.run();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}

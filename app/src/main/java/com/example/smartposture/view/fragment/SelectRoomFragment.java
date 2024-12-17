package com.example.smartposture.view.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartposture.R;
import com.example.smartposture.data.adapter.RoomAdapter;
import com.example.smartposture.data.model.Room;
import com.example.smartposture.data.request.CreateRoomRequest;
import com.example.smartposture.data.sharedpreference.SharedPreferenceManager;
import com.example.smartposture.util.AdditionalSpaceBottom;
import com.example.smartposture.view.activity.MainActivity;
import com.example.smartposture.viewmodel.RoomViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SelectRoomFragment extends BaseFragment implements RoomAdapter.OnRoomClickListener {
    private SharedPreferenceManager spManager;
    private RoomViewModel viewModel;
    private RoomAdapter adapter;
    private RecyclerView recyclerViewRooms;
    private RelativeLayout layoutNoRooms, layoutPreLoader;
    private LinearLayout layoutButtons, layoutTrainer;
    private Button myRoomsButton, availableRoomsButton;
    private String userType;
    private int userId;
    private ImageView preloaderImage;
    private EditText searchRoom;
    private Animation animation;
    private List<Room> allRooms = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_room, container, false);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
            new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                }
            });

        layoutButtons = view.findViewById(R.id.linearLayoutButtons);
        layoutTrainer = view.findViewById(R.id.linearLayoutTrainer);
        layoutPreLoader = view.findViewById(R.id.preloaderLayout);
        layoutNoRooms = view.findViewById(R.id.noRooms);

        searchRoom = view.findViewById(R.id.searchRoom);
        myRoomsButton = view.findViewById(R.id.myRooms);
        availableRoomsButton = view.findViewById(R.id.availableRooms);

        preloaderImage = view.findViewById(R.id.preloaderImage);
        recyclerViewRooms = view.findViewById(R.id.recyclerViewRooms);
        animation = AnimationUtils.loadAnimation(requireContext(), R.anim.logo_bounce);

        viewModel = new ViewModelProvider(this).get(RoomViewModel.class);
        spManager = getSharedPreferenceManager();
        userType = spManager.getUserType();
        userId = spManager.getUserId();

        setupRecyclerView();
        observeViewModel();

        if ("trainee".equals(userType)) {
            highlightButton(myRoomsButton, availableRoomsButton);
            fetchMyRooms(viewModel);

            myRoomsButton.setOnClickListener(v -> {
                highlightButton(myRoomsButton, availableRoomsButton);
                fetchMyRooms(viewModel);
            });

            availableRoomsButton.setOnClickListener(v -> {
                highlightButton(availableRoomsButton, myRoomsButton);
                fetchAvailableRooms(viewModel);
            });
        } else if ("trainer".equals(userType)) {
            layoutButtons.setVisibility(View.GONE);
            layoutTrainer.setVisibility(View.VISIBLE);

            Button createRoomButton = view.findViewById(R.id.createRoom);
            createRoomButton.setOnClickListener(v -> showBottomDialog());

            fetchMyRooms(viewModel);
        }
        searchRoom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String query = charSequence.toString().toLowerCase();

                adapter.filter(query);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        return view;
    }

    @Override
    public void onRoomClick(int roomId, String mode, Button actionButton) {
        if (roomId != -1) {
            Room selectedRoom = adapter.getRoomById(roomId);
            if (selectedRoom != null) {
                if ("myRooms".equals(mode)) {
                    viewModel.selectRoom(selectedRoom);
                } else if ("availableRooms".equals(mode)) {
                    if (selectedRoom.getRequest_status() == 0) {
                        showConfirmationDialog(
                                "Cancel Join Request",
                                "Are you sure you want to cancel your join request for this room?",
                                () -> cancelJoinRequest(selectedRoom, actionButton)
                        );
                    } else {
                        showConfirmationDialog(
                                "Confirm Join Request",
                                "Are you sure you want to request to join this room?",
                                () -> submitJoinRequest(selectedRoom, actionButton)
                        );
                    }
                }
            }
        } else {
            Toast.makeText(requireContext(), "Invalid Room ID", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCodeClick(int roomId, String roomCode) {
        if (roomId != -1) {
            Room selectedRoom = adapter.getRoomById(roomId);
            if (selectedRoom != null) {
                enterRoomCodeDialog(
                    roomId,
                    roomCode,
                    () -> {
                        Toast.makeText(requireContext(), "Room Code Verified! Joining Room...", Toast.LENGTH_SHORT).show();
                    }
                );
            }
        } else {
            Toast.makeText(requireContext(), "Invalid Room ID", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupRecyclerView() {
        int spaceInPixels = getResources().getDimensionPixelSize(R.dimen.recyclerViewSpacing);
        adapter = new RoomAdapter(this, getContext());
        recyclerViewRooms.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewRooms.setAdapter(adapter);
        recyclerViewRooms.addItemDecoration(new AdditionalSpaceBottom(spaceInPixels));
    }

    private void observeViewModel() {
        viewModel.getRoomsLiveData().observe(getViewLifecycleOwner(), rooms -> {
            if (rooms != null && !rooms.isEmpty()) {
                adapter.updateRooms(rooms);
                recyclerViewRooms.setVisibility(View.VISIBLE);
                layoutNoRooms.setVisibility(View.GONE);
            } else {
                recyclerViewRooms.setVisibility(View.GONE);
                layoutNoRooms.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getLoadingState().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                recyclerViewRooms.setVisibility(View.GONE);
                layoutNoRooms.setVisibility(View.GONE);
                layoutPreLoader.setVisibility(View.VISIBLE);
                preloaderImage.setVisibility(View.VISIBLE);
                preloaderImage.startAnimation(animation);
            } else {
                layoutPreLoader.setVisibility(View.GONE);
                preloaderImage.clearAnimation();
                preloaderImage.setVisibility(View.GONE);
            }
        });

        viewModel.getJoinRequestStatus().observe(getViewLifecycleOwner(), response -> {
            if ("Success".equals(response)) {
                Toast.makeText(requireContext(), "Join request sent", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Failed to send join request: " + response, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getCancelRequestStatus().observe(getViewLifecycleOwner(), response -> {
            if ("Success".equals(response)) {
                Toast.makeText(requireContext(), "Join request canceled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Failed to cancel join request: " + response, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getSelectedRoomLiveData().observe(getViewLifecycleOwner(), room -> {
            if (room != null) {
                navigateToRoomDetails(room.getRoom_id());
                viewModel.selectRoom(null);
            }
        });

        viewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchMyRooms(RoomViewModel roomViewModel) {
        adapter.setMode("myRooms");
        int userId = getUserId();
        if (userId != -1) {
            if ("trainee".equals(userType)) {
                roomViewModel.fetchTraineeRooms(userId);
            } else if ("trainer".equals(userType)) {
                roomViewModel.fetchTrainerRooms(userId);
            }
        } else {
            Toast.makeText(requireContext(), "User ID not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchAvailableRooms(RoomViewModel roomViewModel) {
        adapter.setMode("availableRooms");
        int userId = getUserId();
        if (userId != -1) {
            roomViewModel.fetchTraineeAvailableRooms(userId);
        } else {
            Toast.makeText(requireContext(), "User ID not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void submitJoinRequest(Room selectedRoom, Button actionButton) {
        int roomId = selectedRoom.getRoom_id();
        int userId = getUserId();
        String username = spManager.getUsername();

        if (userId != -1) {
            viewModel.requestJoinRoom(userId, username, roomId);

            actionButton.setText("Requested");
            actionButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.bg_dark_blue));

            selectedRoom.setRequest_status(0);
            adapter.notifyItemChanged(adapter.getPosition(selectedRoom.getRoom_id()));
        } else {
            Toast.makeText(requireContext(), "User not logged in. Failed to send join request.", Toast.LENGTH_SHORT).show();
        }
    }

    private void cancelJoinRequest(Room selectedRoom, Button actionButton) {
        int roomId = selectedRoom.getRoom_id();
        int userId = getUserId();

        if (userId != -1) {
            viewModel.cancelJoinRequest(roomId, userId);

            actionButton.setText("Request Join");
            actionButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green_med));

            selectedRoom.setRequest_status(1);
            adapter.notifyItemChanged(adapter.getPosition(selectedRoom.getRoom_id()));
        } else {
            Toast.makeText(requireContext(), "User not logged in. Failed to cancel join request.", Toast.LENGTH_SHORT).show();
        }
    }

    private int getUserId() {
        return spManager.getUserId();
    }

    private void highlightButton(Button selectedBtn, Button otherBtn) {
        selectedBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.teal));
        selectedBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.white));

        otherBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.light_green));
        otherBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
    }

    private void showBottomDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_create_room, null);
        dialog.setContentView(dialogView);

        Button createRoomButton = dialogView.findViewById(R.id.btnSendRequest);
        EditText roomNameEditText = dialogView.findViewById(R.id.input_room_name);

        Observer<String> statusObserver = new Observer<String>() {
            @Override
            public void onChanged(String status) {
                if (status != null) {
                    if (status.equals("Use another room name")) {
                        roomNameEditText.setError(status);
                    } else if (status.equals("Room successfully created")) {
                        dialog.dismiss();
                        Toast.makeText(requireContext(), "Room created successfully!", Toast.LENGTH_SHORT).show();
                        viewModel.fetchTrainerRooms(getUserId());
                    } else {
                        Log.d("Error", "An error occurred: " + status);
                    }
                    viewModel.updateRoomCreationStatus(null);
                }
            }
        };

        viewModel.getRoomCreationStatus().observe(getViewLifecycleOwner(), statusObserver);

        createRoomButton.setOnClickListener(v -> {
            String roomName = roomNameEditText.getText().toString();
            String roomCode = generateRoomCode();
            int creatorId = getUserId();
            String creatorUsername = spManager.getUsername();

            if (!roomName.isEmpty()) {
                CreateRoomRequest request = new CreateRoomRequest(roomName, roomCode, creatorId, creatorUsername);
                viewModel.createRoom(request);
            } else {
                roomNameEditText.setError("Room name cannot be empty.");
            }
        });

        dialog.setOnDismissListener(dialogInterface -> {
            viewModel.getRoomCreationStatus().removeObserver(statusObserver);
        });

        dialog.show();
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

    private void enterRoomCodeDialog(int roomId, String expectedRoomCode, Runnable onConfirm) {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dialog_enter_code);
        dialog.setCancelable(true);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setDimAmount(0.5f);
        }

        EditText enteredRoomCode = dialog.findViewById(R.id.inputRoomCode);
        Button joinRoom = dialog.findViewById(R.id.sendRequest);

        joinRoom.setOnClickListener(v -> {
            String enteredCode = enteredRoomCode.getText().toString().trim();
            if (enteredCode.equals(expectedRoomCode)) {
                viewModel.joinRoomCode(roomId, userId);
                navigateToRoomDetails(roomId);
                Toast.makeText(requireContext(), "Successfully joined the room!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                enteredRoomCode.setError("Incorrect room code");
            }
        });

        dialog.show();
    }

    private static String generateRoomCode() {
        Random random = new Random();

        char firstLetter = (char) ('A' + random.nextInt(26));
        char secondLetter = (char) ('A' + random.nextInt(26));

        int digits = random.nextInt(10000);
        String formattedDigits = String.format("%04d", digits);

        return "" + firstLetter + secondLetter + formattedDigits;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setBottomNavVisibility(View.VISIBLE);
        }
    }

    private void navigateToRoomDetails(int roomId) {
        Bundle bundle = new Bundle();
        bundle.putInt("room_id", roomId);

        RoomDetailFragment fragment = new RoomDetailFragment();
        fragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack("SelectRoomFragment")
                .commit();
    }
}

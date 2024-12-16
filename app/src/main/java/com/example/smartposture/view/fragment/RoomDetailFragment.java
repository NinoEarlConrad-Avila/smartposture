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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartposture.R;
import com.example.smartposture.data.adapter.ActivityAdapter;
import com.example.smartposture.data.adapter.JoinRequestAdapter;
import com.example.smartposture.data.adapter.RoomTraineesAdapter;
import com.example.smartposture.data.model.JoinRequest;
import com.example.smartposture.data.model.Room;
import com.example.smartposture.data.model.Trainee;
import com.example.smartposture.data.sharedpreference.SharedPreferenceManager;
import com.example.smartposture.util.AdditionalSpaceBottom;
import com.example.smartposture.view.activity.MainActivity;
import com.example.smartposture.viewmodel.ActivityViewModel;
import com.example.smartposture.viewmodel.RoomDetailViewModel;

public class RoomDetailFragment extends BaseFragment {
    private RoomDetailViewModel roomViewModel;
    private ActivityViewModel activityViewModel;
    private ActivityAdapter activityAdapter;
    private LinearLayout viewJoinRequest, viewRoomTrainees, addActivity;
    private RelativeLayout layout, noRequest, noActivities, preloaderActivityLayout;
    private RecyclerView recyclerView, recyclerRoomActivities;
    private ImageView preloaderImage, notification, preloaderImageActivity;
    private ImageButton backButton;
    private TextView roomName, roomCreator, roomCode, noActivityText;
    private Button activeActivitiesBtn, inactiveActivitiesBtn;
    private Animation animation;
    private String dialogType, userType;
    private SharedPreferenceManager spManager;
    private int roomId;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room, container, false);

        view.setVisibility(View.INVISIBLE);
        Dialog loadingDialog = createFullScreenLoadingDialog();
        loadingDialog.show();

        View optionsTrainer = view.findViewById(R.id.optionsTrainer);
        spManager = getSharedPreferenceManager();
        userType = spManager.getUserType();
        int userId = spManager.getUserId();

        roomName = view.findViewById(R.id.roomNameTextView);
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
            new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    requireActivity().getSupportFragmentManager().popBackStack();
                }
            });

        recyclerRoomActivities = view.findViewById(R.id.recyclerRoomActivities);
        activeActivitiesBtn = view.findViewById(R.id.activeActivityBtn);
        inactiveActivitiesBtn = view.findViewById(R.id.inactiveActivityBtn);
        noActivities = view.findViewById(R.id.noActivities);
        noActivityText = view.findViewById(R.id.noActText);
        preloaderActivityLayout = view.findViewById(R.id.preloaderActivityLayout);
        preloaderImageActivity = view.findViewById(R.id.preloaderImageActivity);
        addActivity = view.findViewById(R.id.addActivity);
        animation = AnimationUtils.loadAnimation(requireContext(), R.anim.logo_bounce);

        roomCreator = view.findViewById(R.id.roomCreatorTextView);
        roomCode = view.findViewById(R.id.roomCode);
        viewJoinRequest = view.findViewById(R.id.viewJoinRequest);
        viewRoomTrainees = view.findViewById(R.id.viewRoomTrainees);
        notification = view.findViewById(R.id.notification);
        backButton = view.findViewById(R.id.backButton);

        if ("trainee".equalsIgnoreCase(userType)) {
            optionsTrainer.setVisibility(View.GONE);
        }
        int spaceInPixels = getResources().getDimensionPixelSize(R.dimen.roomActivities);
        recyclerRoomActivities.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerRoomActivities.addItemDecoration(new AdditionalSpaceBottom(spaceInPixels));

        roomViewModel = new ViewModelProvider(this).get(RoomDetailViewModel.class);
        activityViewModel = new ViewModelProvider(this).get(ActivityViewModel.class);

        roomId = requireArguments().getInt("room_id", -1);

        addActivity.setOnClickListener(v -> {
            navigateToCreateActivity(roomId);
        });

        notification.setOnClickListener(v -> {
            navigateToNotification(roomId);
        });

        highlightButton(activeActivitiesBtn, inactiveActivitiesBtn);
        if (userType.equals("trainee")){
            fetchActiveActivitiesTrainee(roomId, userId);
        } else if (userType.equals("trainer")){
            fetchActiveActivitiesTrainer(roomId);
        }

        backButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        activeActivitiesBtn.setOnClickListener(v -> {
            highlightButton(activeActivitiesBtn, inactiveActivitiesBtn);
            noActivityText.setText("No Active Activities");
            if (userType.equals("trainee")){
                fetchActiveActivitiesTrainee(roomId, userId);
            } else if (userType.equals("trainer")){
                fetchActiveActivitiesTrainer(roomId);
            }
        });

        inactiveActivitiesBtn.setOnClickListener(v -> {
            highlightButton(inactiveActivitiesBtn, activeActivitiesBtn);
            noActivityText.setText("No Inactive Activities");
            if (userType.equals("trainee")){
                fetchInactiveActivitiesTrainee(roomId, userId);
            } else if (userType.equals("trainer")){
                fetchInactiveActivitiesTrainer(roomId);
            }
        });

        viewJoinRequest.setOnClickListener(v -> {
            dialogType = "joinRequest";
            showJoinRequestDialog(roomId);
        });

        viewRoomTrainees.setOnClickListener(v -> {
            dialogType = "roomTrainees";
            showRoomTraineesDialog(roomId);
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
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setBottomNavVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setBottomNavVisibility(View.GONE);
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
        Dialog dialog = createDialog();

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

    private void showRoomTraineesDialog(int roomId) {
        Dialog dialog = createDialog();

        layout = dialog.findViewById(R.id.preloaderLayout);
        noRequest = dialog.findViewById(R.id.noJoinRequest);
        recyclerView = dialog.findViewById(R.id.recyclerView);
        preloaderImage = dialog.findViewById(R.id.preloaderImage);
        Button viewTrainees = dialog.findViewById(R.id.trainees);
        Button addTrainees = dialog.findViewById(R.id.addTrainees);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        layout.setVisibility(View.VISIBLE);
        preloaderImage.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        noRequest.setVisibility(View.GONE);

        animation = AnimationUtils.loadAnimation(requireContext(), R.anim.logo_bounce);
        preloaderImage.startAnimation(animation);

        roomViewModel = new ViewModelProvider(this).get(RoomDetailViewModel.class);

        highlightButton(viewTrainees, addTrainees);
        fetchRoomTrainees(roomId);

        viewTrainees.setOnClickListener(v -> {
            highlightButton(viewTrainees, addTrainees);
            fetchRoomTrainees(roomId);
        });

        addTrainees.setOnClickListener(v -> {
            highlightButton(addTrainees, viewTrainees);
            fetchAvailableTrainees(roomId);
        });

        loadingStateObserver();

        dialog.show();
    }

    private Dialog createDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (dialogType.equals("joinRequest")){
            dialog.setContentView(R.layout.dialog_join_request);
        } else if (dialogType.equals("roomTrainees")){
            dialog.setContentView(R.layout.dialog_room_trainees);
        }
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

    private void fetchRoomTrainees(int roomId) {
        roomViewModel.fetchRoomTrainees(roomId).observe(getViewLifecycleOwner(), roomTrainees -> {
            if (roomTrainees == null || roomTrainees.isEmpty()) {
                noRequest.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                recyclerView.setAdapter(new RoomTraineesAdapter(getContext(), roomTrainees, new RoomTraineesAdapter.OnItemActionListener() {
                    @Override
                    public void onAction(Trainee request) {
                        showConfirmationDialog("Remove Trainee", "Are you sure you want to remove this trainee?",
                                () -> roomViewModel.removeTrainee(roomId, request.getId()));
                    }
                }, false));
                recyclerView.setVisibility(View.VISIBLE);
                noRequest.setVisibility(View.GONE);
            }
        });

        loadingStateObserver();
    }

    private void fetchAvailableTrainees(int roomId) {
        roomViewModel.fetchAvailableTrainees(roomId).observe(getViewLifecycleOwner(), availableTrainees -> {
            if (availableTrainees == null || availableTrainees.isEmpty()) {
                noRequest.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                recyclerView.setAdapter(new RoomTraineesAdapter(getContext(), availableTrainees, new RoomTraineesAdapter.OnItemActionListener() {
                    @Override
                    public void onAction(Trainee request) {
                        showConfirmationDialog("Add Trainee", "Are you sure you want to add this trainee?",
                                () -> roomViewModel.addTrainee(roomId, request.getId()));
                    }
                }, true));
                recyclerView.setVisibility(View.VISIBLE);
                noRequest.setVisibility(View.GONE);
            }
        });

        loadingStateObserver();
    }

    private void fetchActiveActivitiesTrainer(int roomId) {
        activityViewModel.fetchActiveActivitiesTrainer(roomId);

        activityViewModel.getActiveActivitiesTrainer().observe(getViewLifecycleOwner(), activities -> {
            if (activities != null && !activities.isEmpty()) {
                activityAdapter = new ActivityAdapter(getContext(), activities, this, userType, roomId);
                recyclerRoomActivities.setAdapter(activityAdapter);

                recyclerRoomActivities.setVisibility(View.VISIBLE);
                noActivities.setVisibility(View.GONE);
            } else {
                recyclerRoomActivities.setVisibility(View.GONE);
                noActivities.setVisibility(View.VISIBLE);
            }
        });
        loadingStateActivitiesObserver();
    }

    private void fetchActiveActivitiesTrainee(int roomId, int userId) {
        activityViewModel.fetchActiveActivitiesTrainee(roomId, userId);

        activityViewModel.getActiveActivitiesTrainee().observe(getViewLifecycleOwner(), activities -> {
            if (activities != null && !activities.isEmpty()) {
                activityAdapter = new ActivityAdapter(getContext(), activities, this, userType, roomId);
                recyclerRoomActivities.setAdapter(activityAdapter);

                recyclerRoomActivities.setVisibility(View.VISIBLE);
                noActivities.setVisibility(View.GONE);
            } else {
                recyclerRoomActivities.setVisibility(View.GONE);
                noActivities.setVisibility(View.VISIBLE);
            }
        });
        loadingStateActivitiesObserver();
    }

    private void fetchInactiveActivitiesTrainer(int roomId) {
        activityViewModel.fetchInactiveActivitiesTrainer(roomId);

        activityViewModel.getInactiveActivitiesTrainer().observe(getViewLifecycleOwner(), activities -> {
            if (activities != null && !activities.isEmpty()) {
                activityAdapter = new ActivityAdapter(getContext(), activities, this, userType, roomId);
                recyclerRoomActivities.setAdapter(activityAdapter);

                recyclerRoomActivities.setVisibility(View.VISIBLE);
                noActivities.setVisibility(View.GONE);
            } else {
                recyclerRoomActivities.setVisibility(View.GONE);
                noActivities.setVisibility(View.VISIBLE);
            }
        });
        loadingStateActivitiesObserver();
    }

    private void fetchInactiveActivitiesTrainee(int roomId, int userId) {
        activityViewModel.fetchInactiveActivitiesTrainee(roomId, userId);

        activityViewModel.getInactiveActivitiesTrainee().observe(getViewLifecycleOwner(), activities -> {
            if (activities != null && !activities.isEmpty()) {
                activityAdapter = new ActivityAdapter(getContext(), activities, this, userType, roomId);
                recyclerRoomActivities.setAdapter(activityAdapter);

                recyclerRoomActivities.setVisibility(View.VISIBLE);
                noActivities.setVisibility(View.GONE);
            } else {
                recyclerRoomActivities.setVisibility(View.GONE);
                noActivities.setVisibility(View.VISIBLE);
            }
        });
        loadingStateActivitiesObserver();
    }

    private void loadingStateActivitiesObserver(){
        activityViewModel.getLoadingState().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                recyclerRoomActivities.setVisibility(View.GONE);
                noActivities.setVisibility(View.GONE);
                preloaderActivityLayout.setVisibility(View.VISIBLE);
                preloaderImageActivity.setVisibility(View.VISIBLE);
                preloaderImageActivity.startAnimation(animation);
            } else {
                preloaderActivityLayout.setVisibility(View.GONE);
                preloaderImageActivity.clearAnimation();
                preloaderImageActivity.setVisibility(View.GONE);
            }
        });
    }

    private void loadingStateObserver(){
        roomViewModel.getLoadingState().observe(getViewLifecycleOwner(), isLoading -> {
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
    }

    private void highlightButton(Button selectedBtn, Button otherBtn) {
        selectedBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.teal));
        selectedBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.white));

        otherBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.light_green));
        otherBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
    }

    private void navigateToCreateActivity(int roomId) {
        Bundle bundle = new Bundle();
        bundle.putInt("room_id", roomId);

        CreateActivityFragment fragment = new CreateActivityFragment();
        fragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack("RoomDetailFragment")
                .commit();
    }

    private void navigateToNotification(int roomId) {
        Bundle bundle = new Bundle();
        bundle.putString("notification_type", "room");
        bundle.putInt("room_id", roomId);

        NotificationFragment fragment = new NotificationFragment();
        fragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack("RoomDetailFragment")
                .commit();
    }
}

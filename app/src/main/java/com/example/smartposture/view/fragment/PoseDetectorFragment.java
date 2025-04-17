package com.example.smartposture.view.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartposture.R;
import com.example.smartposture.data.sharedpreference.SharedPreferenceManager;
import com.example.smartposture.posedetector.GraphicOverlay;
import com.example.smartposture.posedetector.classification.PoseClassifierProcessor;
import com.example.smartposture.posedetector.classification.PoseCorrection;
import com.example.smartposture.view.activity.MainActivity;
import com.example.smartposture.viewmodel.ActivityViewModel;
import com.example.smartposture.viewmodel.PoseDetectorViewModel;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseDetection;
import com.google.mlkit.vision.pose.PoseDetector;
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions;
import com.google.common.util.concurrent.ListenableFuture;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PoseDetectorFragment extends BaseFragment {
    private static final String TAG = "PoseDetectorFragment";
    private static final int PERMISSION_REQUESTS = 1;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private PreviewView previewView;
    private PoseCorrection poseCorrection;
    private GraphicOverlay graphicOverlay;
    private PoseDetector poseDetector;
    private PoseClassifierProcessor poseClassifierProcessor;
    private HandlerThread handlerThread;
    private Handler handler;
    private final boolean runClassification = true;
    private PoseDetectorViewModel homeViewModel;
    private boolean isBackCamera = false;
    private boolean isInitialCam = true;
    private ImageView switchCameraBtn;
    private ProcessCameraProvider cameraProvider;
    private String type;
    private boolean guidanceStatus;
    private SharedPreferenceManager spManager;
    private PoseDetectorViewModel poseDetectorViewModel;
    private TextView goalRepetition, currentRepetition;
    private int activityWorkoutId, goal, current;
    private List<String> classificationResult;
    private LinearLayout goalLayout;
    private Button done;
    @OptIn(markerClass = ExperimentalGetImage.class)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pose_detector, container, false);

        if (getActivity() != null) {
            ((MainActivity) getActivity()).setBottomNavVisibility(View.GONE);
        }

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                    }
                });
        ImageButton returnBtn = view.findViewById(R.id.returnBtn);
        goalLayout = view.findViewById(R.id.goalRepetitionLayout);
        previewView = view.findViewById(R.id.previewView);
        graphicOverlay = view.findViewById(R.id.graphicOverlay);
        done = view.findViewById(R.id.done);
        Button goHome = view.findViewById(R.id.goHome);
        goalRepetition = view.findViewById(R.id.goalRepCount);
        currentRepetition = view.findViewById(R.id.currentRepCount);

        activityWorkoutId = requireArguments().getInt("activity_workout_id", -1);
        goal = requireArguments().getInt("rep_goal", -1);

        if (activityWorkoutId != -1 && goal != -1){
            goalRepetition.setText(String.valueOf(goal));
        }else {
            goalLayout.setVisibility(View.GONE);
        }

        spManager = SharedPreferenceManager.getInstance(requireContext());
        guidanceStatus = spManager.getGuidanceStatus();

        switchCameraBtn = view.findViewById(R.id.switchCam);
//        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        PoseDetectorOptions options = new PoseDetectorOptions.Builder()
                .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
                .build();
        poseDetector = PoseDetection.getClient(options);
        poseDetectorViewModel = new ViewModelProvider(this).get(PoseDetectorViewModel.class);

        poseDetectorViewModel.getRepCount().observe(getViewLifecycleOwner(), repCount -> {
            currentRepetition.setText(String.valueOf(repCount));

            if (repCount >= goal){
                done.setEnabled(true);
                done.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.teal));
            }else{
                done.setEnabled(false);
                done.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.light_green));
            }
        });

        poseCorrection = new PoseCorrection(requireContext(),"pose/pose_landmarks2.csv");

        handlerThread = new HandlerThread("PoseClassifierProcessorThread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());

        Bundle args = getArguments();
        type = args != null ? args.getString("exer") : "squat";
        poseClassifierProcessor = new PoseClassifierProcessor(getContext(), this, true, homeViewModel, type);


        goHome.setOnClickListener(v -> {
            navigateToHome();
        });
        done.setOnClickListener(v -> {
            ArrayList<Float> floatList = poseClassifierProcessor.getScores();

            if (floatList == null || floatList.isEmpty()) {
                // Show dialog for no available data
                showCustomDialog(
                        "No Data Available",
                        "No scores were recorded. Please try the exercise again.",
                        null, // No action needed on confirm
                        null // No action needed for cancel (since button is hidden)
                );
            } else {
                // Show confirmation dialog to proceed
                showCustomDialog(
                        "Confirm Action",
                        "Scores are available. Do you want to proceed?",
                        () -> {
                            // Action on "Yes": Navigate to the WorkoutSummaryFragment
                            navigateToSummary(floatList);
                        },
                        () -> {
                            resetCamera();
                            resetPoseClassifierProcessor();
                        }
                );
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBarsInsets.left, systemBarsInsets.top, systemBarsInsets.right, systemBarsInsets.bottom);
            return insets;
        });

        switchCameraBtn.setOnClickListener(v -> {
            isBackCamera = !isBackCamera; // Toggle camera
            isInitialCam = false;
            startCamera(); // Restart camera with the new lens facing
        });

        if (!allPermissionsGranted()) {
            getRuntimePermissions();
        } else {
            startCamera();
        }

        return view;
    }

    private void startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error starting camera", e);
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
//        if (!isInitialCam){
            cameraProvider.unbindAll();
//        }

        // Create a Preview use case
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        // Set the default to back camera
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(isBackCamera ? CameraSelector.LENS_FACING_BACK : CameraSelector.LENS_FACING_FRONT)
                .build();

        // Create an ImageAnalysis use case
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

// Set custom frame rate (e.g., 15 FPS)
        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(requireContext()), new ImageAnalysis.Analyzer() {
            private long lastAnalyzedTimestamp = 0L;
            private static final long ANALYSIS_INTERVAL_MS = 1000L / 24; // 15 FPS

            @Override
            public void analyze(@NonNull ImageProxy imageProxy) {
                long currentTimestamp = System.currentTimeMillis();

                if (currentTimestamp - lastAnalyzedTimestamp >= ANALYSIS_INTERVAL_MS) {
                    ByteBuffer byteBuffer = imageProxy.getImage().getPlanes()[0].getBuffer();
                    byteBuffer.rewind();
                    Bitmap bitmap = Bitmap.createBitmap(imageProxy.getWidth(), imageProxy.getHeight(), Bitmap.Config.ARGB_8888);
                    bitmap.copyPixelsFromBuffer(byteBuffer);

                    if (bitmap != null) {
                        InputImage image = InputImage.fromBitmap(bitmap, imageProxy.getImageInfo().getRotationDegrees());
                        Task<Pose> result = poseDetector.process(image)
                                .addOnSuccessListener(pose -> {
                                    classificationResult = new ArrayList<>();
                                    if (poseClassifierProcessor != null && runClassification) {
                                        handler.post(() -> {
                                            classificationResult.addAll(poseClassifierProcessor.getPoseResult(pose));
                                            if(guidanceStatus){
                                                poseCorrection.generateGuidance(pose);
                                            }
                                            requireActivity().runOnUiThread(() -> {
                                                // Adjust based on back camera flip
                                                graphicOverlay.setImageSourceInfo(imageProxy.getHeight(), imageProxy.getWidth(), !isBackCamera);
                                                graphicOverlay.clear();
                                                graphicOverlay.add(new Display(graphicOverlay, pose, true, true, true, classificationResult));
                                            });
                                        });
                                    }
                                })
                                .addOnFailureListener(e -> Log.e(TAG, "Pose detection failed", e));
                    }

                    // Update the timestamp after processing the current frame
                    lastAnalyzedTimestamp = currentTimestamp;
                }

                // Always close the imageProxy to prevent memory leaks
                imageProxy.close();
            }
        });

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, imageAnalysis, preview);
    }

    private boolean allPermissionsGranted() {
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(requireContext(), permission)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isPermissionGranted(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void getRuntimePermissions() {
        List<String> allNeededPermissions = new ArrayList<>();
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(requireContext(), permission)) {
                allNeededPermissions.add(permission);
            }
        }

        if (!allNeededPermissions.isEmpty()) {
            requestPermissions(allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
        }
    }

    private String[] getRequiredPermissions() {
        try {
            PackageInfo info = requireContext().getPackageManager().getPackageInfo(requireContext().getPackageName(), PackageManager.GET_PERMISSIONS);
            return info.requestedPermissions != null ? info.requestedPermissions : new String[0];
        } catch (Exception e) {
            Log.e(TAG, "Error getting required permissions", e);
            return new String[0];
        }
    }

    private void showCustomDialog(String title, String message, Runnable onConfirm, Runnable onCancel) {
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
            if (onConfirm != null) {
                onConfirm.run();
            }
        });

        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
            if (onCancel != null) {
                onCancel.run();
            }
        });

        dialog.show();
    }


    private void resetCamera() {
        // Logic to reset the camera
        poseClassifierProcessor.resetScores();
        poseClassifierProcessor.resetRepCount();
        currentRepetition.setText(String.valueOf(0));
        done.setEnabled(false);
        done.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.light_green));
        startCamera();
        Toast.makeText(requireContext(), "Camera reset.", Toast.LENGTH_SHORT).show();
    }

    private void resetPoseClassifierProcessor() {
        // Reinitialize the PoseClassifierProcessor
        poseClassifierProcessor = new PoseClassifierProcessor(getContext(), this,true, homeViewModel, type);
        Toast.makeText(requireContext(), "Pose Classifier Processor has been reset", Toast.LENGTH_SHORT).show();
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

    private void navigateToHome() {
        Bundle bundle = new Bundle();

        HomeFragment fragment = new HomeFragment();

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack("RoomDetailFragment")
                .commit();
    }
    private void navigateToSummary(ArrayList<Float> floatList) {
        Bundle bundle = new Bundle();
        bundle.putInt("room_id", requireArguments().getInt("room_id", -1));
        bundle.putInt("activity_id", requireArguments().getInt("activity_id", -1));
        bundle.putInt("rep_goal", requireArguments().getInt("rep_goal", -1));
        bundle.putInt("activity_workout_id", activityWorkoutId);
        bundle.putSerializable("floatList", floatList);

        WorkoutSummaryFragment summary = new WorkoutSummaryFragment();
        summary.setArguments(bundle);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, summary)
                .addToBackStack("WorkoutSummary")
                .commit();
    }
    public void showBadPosturePopup(String alert) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Posture Alert");
        builder.setMessage(alert);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
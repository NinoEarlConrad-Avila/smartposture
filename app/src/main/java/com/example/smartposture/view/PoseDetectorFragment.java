package com.example.smartposture.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartposture.R;
import com.example.smartposture.posedetector.GraphicOverlay;
import com.example.smartposture.posedetector.classification.PoseClassifierProcessor;
import com.example.smartposture.viewmodel.HomeViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

public class PoseDetectorFragment extends Fragment {
    private static final String TAG = "PoseDetectorFragment";
    private static final int PERMISSION_REQUESTS = 1;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private PreviewView previewView;
    private GraphicOverlay graphicOverlay;
    private PoseDetector poseDetector;
    private PoseClassifierProcessor poseClassifierProcessor;
    private HandlerThread handlerThread;
    private Handler handler;
    private final boolean runClassification = true;
    private HomeViewModel homeViewModel;

    @OptIn(markerClass = ExperimentalGetImage.class)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pose_detector, container, false);

        ImageButton returnBtn = view.findViewById(R.id.returnBtn);
        previewView = view.findViewById(R.id.previewView);
        graphicOverlay = view.findViewById(R.id.graphicOverlay);
        Button done = view.findViewById(R.id.done);

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        PoseDetectorOptions options = new PoseDetectorOptions.Builder()
                .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
                .build();
        poseDetector = PoseDetection.getClient(options);

        handlerThread = new HandlerThread("PoseClassifierProcessorThread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());

        Bundle args = getArguments();
        String type = args != null ? args.getString("exer") : "squat";
        poseClassifierProcessor = new PoseClassifierProcessor(getContext(), true, homeViewModel, type);

        returnBtn.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        done.setOnClickListener(v -> {
            WorkoutSummaryFragment summary = new WorkoutSummaryFragment();

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, summary)
                    .addToBackStack("WorkoutSummary")
                    .commit();
        });

        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBarsInsets.left, systemBarsInsets.top, systemBarsInsets.right, systemBarsInsets.bottom);
            return insets;
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
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error starting camera", e);
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        boolean isBackCamera = true; // or false for front camera


        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(isBackCamera ? CameraSelector.LENS_FACING_BACK : CameraSelector.LENS_FACING_FRONT)
                .build();

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
                                    List<String> classificationResult = new ArrayList<>();
                                    if (poseClassifierProcessor != null && runClassification) {
                                        handler.post(() -> {
                                            classificationResult.addAll(poseClassifierProcessor.getPoseResult(pose));
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (poseClassifierProcessor != null) {
            poseClassifierProcessor.cleanup();
        }
        if (handlerThread != null) {
            handlerThread.quitSafely();
        }
        BottomNavigationView bottomNavigation = requireActivity().findViewById(R.id.bottom_navigation);
        bottomNavigation.setVisibility(View.VISIBLE);
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
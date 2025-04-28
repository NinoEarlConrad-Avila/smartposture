package com.example.smartposture.posedetector.classification;

import static com.example.smartposture.posedetector.classification.Utils.subtract;

import android.content.Context;
import android.util.Log;

import com.google.mlkit.vision.common.PointF3D;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PoseCorrection {
    private final List<List<PointF3D>> dataset;
    private final float threshold = 30;
    private final PoseAudio pa;
    private static String POSE_SAMPLES_FILE;

    private final PoseNormalizer pn = new PoseNormalizer();
//    private final PoseEmbedding pe = new PoseEmbedding();

    public PoseCorrection(Context context, String type) {
        if(type != null && type.trim().equals("push up")){
            POSE_SAMPLES_FILE = "pose/squats.csv";
        }else if(type != null && type.trim().equals("squat")){
            POSE_SAMPLES_FILE = "pose/squats_cor.csv";
        } else if(type != null && type.trim().equals("wall sit")){
            POSE_SAMPLES_FILE = "pose/wall_sit_cor.csv";
        } else if(type != null && type.trim().equals("lunge")){
            POSE_SAMPLES_FILE = "pose/lunge_cor.csv";
        } else if(type != null && type.trim().equals("single leg squat")){
            POSE_SAMPLES_FILE = "pose/wall_sit_cor.csv";
        }
        dataset = loadDatasetFromAssets(context, POSE_SAMPLES_FILE);
        pa = new PoseAudio(context);
    }

    private static final List<String> BODY_POINTS = Arrays.asList(
            "Nose", "Left Eye Inner", "Left Eye", "Left Eye Outer",
            "Right Eye Inner", "Right Eye", "Right Eye Outer", "Left Ear",
            "Right Ear", "Left Mouth", "Right Mouth", "Left Shoulder",
            "Right Shoulder", "Left Elbow", "Right Elbow", "Left Wrist",
            "Right Wrist", "Left Pinky", "Right Pinky", "Left_Index",
            "Right Index", "Left Thumb", "Right thumb", "Left Hip",
            "Right Hip", "Left Knee", "Right Knee", "Left Ankle",
            "Right Ankle", "Left Heel", "Right Heel", "Left Foot Index",
            "Right Foot Index"
    );

    // Load the dataset from a file into a List of Lists of PointF3D
    private List<List<PointF3D>> loadDatasetFromAssets(Context context, String fileName) {
        List<List<PointF3D>> dataset = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(context.getAssets().open(fileName)))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                List<PointF3D> pose = new ArrayList<>();
                for (int i = 0; i < values.length; i += 3) {
                    float x = Float.parseFloat(values[i]);
                    float y = Float.parseFloat(values[i + 1]);
                    float z = Float.parseFloat(values[i + 2]);
                    pose.add(PointF3D.from(x, y, z));
                }
                dataset.add(pn.normalizePose(pose));
            }
        } catch (IOException e) {
            Log.e("PoseCorrection", "Error loading dataset from assets", e);
        }
        return dataset;
    }

    // Convert Pose object to a list of PointF3D for processing
    private List<PointF3D> poseToList(Pose pose) {
        List<PointF3D> poseList = new ArrayList<>();
        for (PoseLandmark landmark : pose.getAllPoseLandmarks()) {
            PointF3D position = landmark.getPosition3D();
            poseList.add(position);
        }
        return poseList;
    }

    // Find the nearest neighbor and return the difference
    public List<PointF3D> findCorrection(List<PointF3D> pose) {
        List<PointF3D> nearestNeighbor = null;
        float minDistance = Float.MAX_VALUE;

        for (List<PointF3D> sample : dataset) {
            float distance = calculatePoseDistance(pose, sample);
            if (distance < minDistance) {
                minDistance = distance;
                nearestNeighbor = sample;
            }
        }
        Log.d("NearestNeighbor", nearestNeighbor.toString());
        // Handle cases where no nearest neighbor is found
        if (nearestNeighbor == null) {
            Log.e("PoseCorrection", "No nearest neighbor found in the dataset.");
            return null; // Or return an empty correction
        }else {
            StringBuilder nearestNeighborLog = new StringBuilder();
            for (int i = 0; i < nearestNeighbor.size(); i++) {
                PointF3D point = nearestNeighbor.get(i);

                // Format each coordinate (X, Y, Z) as a float with 6 decimal places
                nearestNeighborLog.append(String.format("%.6f", point.getX())).append(", ");
                nearestNeighborLog.append(String.format("%.6f", point.getY())).append(", ");
                nearestNeighborLog.append(String.format("%.6f", point.getZ()));

                if (i < nearestNeighbor.size() - 1) {
                    nearestNeighborLog.append(", "); // Add a comma for all but the last point
                }
            }
            Log.d("Correction", nearestNeighborLog.toString());
        }
        return calculatePoseDifference(nearestNeighbor, pose);
    }

    // Calculate the Euclidean distance between two poses
    private float calculatePoseDistance(List<PointF3D> pose1, List<PointF3D> pose2) {
        float sum = 0;
        for (int i = 0; i < pose1.size(); i++) {
            PointF3D p1 = pose1.get(i);
            PointF3D p2 = pose2.get(i);
            float diffX = p1.getX() - p2.getX();
            float diffY = p1.getY() - p2.getY();
            float diffZ = p1.getZ() - p2.getZ();
            sum += diffX * diffX + diffY * diffY + diffZ * diffZ;
        }
        return (float) Math.sqrt(sum);
    }

    // Calculate the difference between the nearest neighbor and the current pose
    private List<PointF3D> calculatePoseDifference(List<PointF3D> nearestNeighbor, List<PointF3D> pose) {
        if (nearestNeighbor == null || pose == null) {
            throw new IllegalArgumentException("Nearest neighbor or pose cannot be null.");
        }

        List<PointF3D> difference = new ArrayList<>();
        for (int i = 0; i < pose.size(); i++) {
            PointF3D p1 = pose.get(i);
            PointF3D p2 = nearestNeighbor.get(i);
            PointF3D sub = subtract(p2, p1);
            difference.add(PointF3D.from(p1.getX() - p2.getX(), p1.getY() - p2.getY(), p1.getZ() - p2.getZ()));
        }
        return difference;
    }

    // Generate directional guidance for each landmark based on the Pose object
    public void generateGuidance(Pose pose) {
        if (pose.getAllPoseLandmarks() == null || pose.getAllPoseLandmarks().size() != 33) {
            Log.e("PoseCorrection", "Pose list is invalid or does not contain enough data." + pose.getAllPoseLandmarks().size());
            return;
        }else {
            StringBuilder allLandmarks = new StringBuilder();
            for (int i = 0; i < pose.getAllPoseLandmarks().size(); i++) {
                PoseLandmark landmark = pose.getAllPoseLandmarks().get(i);
                PointF3D position = landmark.getPosition3D();

                // Format each coordinate (X, Y, Z) as a float or double with 6 decimal places
                allLandmarks.append(String.format("%.6f", position.getX())).append(", ");
                allLandmarks.append(String.format("%.6f", position.getY())).append(", ");
                allLandmarks.append(String.format("%.6f", position.getZ()));

                if (i < pose.getAllPoseLandmarks().size() - 1) {
                    allLandmarks.append(","); // Add a comma for all but the last element
                }
            }
            Log.d("PoseLandmark", allLandmarks.toString());
        }

        List<PointF3D> poseList = poseToList(pose);
        // Normalize the pose using PoseEmbedding
        List<PointF3D> normalizedPose = pn.normalizePose(poseList);
        // Log the normalizedPose details
        if (normalizedPose == null || normalizedPose.isEmpty()) {
            Log.e("PoseEmbedding", "Normalized pose is empty or null.");
        } else {
            StringBuilder normalizedPoseLog = new StringBuilder();
            for (int i = 0; i < normalizedPose.size(); i++) {
                PointF3D point = normalizedPose.get(i);

                // Format each coordinate (X, Y, Z) as a float with 6 decimal places
                normalizedPoseLog.append(String.format("%.6f", point.getX())).append(", ");
                normalizedPoseLog.append(String.format("%.6f", point.getY())).append(", ");
                normalizedPoseLog.append(String.format("%.6f", point.getZ()));

                if (i < normalizedPose.size() - 1) {
                    normalizedPoseLog.append(", "); // Add a comma for all but the last point
                }
            }
            Log.d("NormalizedPose", normalizedPoseLog.toString());
        }
        List<PointF3D> correction = findCorrection(normalizedPose);

        List<String> bodyPointsToConsider = Arrays.asList(
                "Left Knee", "Right Knee",
                "Left Hip", "Right Hip",
                "Left Hand", "Right Hand",
                "Left Elbow", "Right Elbow",
                "Left Shoulder", "Right Shoulder"
        );

        // Indices of the relevant landmarks in the pose list
        int[] relevantIndices = {25, 26, 23, 24, 15, 16, 13, 14, 11, 12};

        // Building guidance output based on the filtered landmarks
        List<String> guidance = new ArrayList<>();

        // Generate corrections for each relevant body point
        for (int i = 0; i < relevantIndices.length; i++) {
            String bodyPoint = bodyPointsToConsider.get(i);

            // Get the precomputed corrections (dx, dy, dz) from the correction array
            PointF3D delta = correction.get(i);

            float dx = delta.getX();
            float dy = delta.getY();
            float dz = delta.getZ();

            // Skip small corrections below the threshold
            if (Math.abs(dx) < threshold && Math.abs(dy) < threshold && Math.abs(dz) < threshold) continue;

            // Build the correction string
            StringBuilder correctionMessage = new StringBuilder("Move ").append(bodyPoint);
            if (Math.abs(dx) >= threshold) correctionMessage.append(" ").append(dx > 0 ? "Right" : "Left");
            if (Math.abs(dy) >= threshold) correctionMessage.append(" ").append(dy > 0 ? "Up" : "Down");
            if (Math.abs(dz) >= threshold) correctionMessage.append(" ").append(dz > 0 ? "Forward" : "Backward");

            // Add the guidance message to the list
            guidance.add(correctionMessage.toString().trim());

            // Output the guidance as audio
            pa.speak(correctionMessage.toString());
        }

//        return guidance;
    }
}

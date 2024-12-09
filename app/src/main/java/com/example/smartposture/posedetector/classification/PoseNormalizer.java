package com.example.smartposture.posedetector.classification;

import com.google.mlkit.vision.common.PointF3D;

import java.util.ArrayList;
import java.util.List;

public class PoseNormalizer {

    private static final float TARGET_SIZE = 300.0f; // Target diagonal size for scaling

    // Method to calculate the center point from specific landmarks
    private PointF3D calculateCenterPoint(List<PointF3D> landmarks) {
        int[] indices = {12, 11, 24, 23}; // Indices for shoulders and hips
        float centerX = 0, centerY = 0, centerZ = 0;

        for (int idx : indices) {
            PointF3D point = landmarks.get(idx);
            centerX += point.getX();
            centerY += point.getY();
            centerZ += point.getZ();
        }

        int count = indices.length;
        return PointF3D.from(centerX / count, centerY / count, centerZ / count);
    }

    // Method to normalize landmarks by setting the center point to (0, 0, 0)
    private List<PointF3D> translateToCenter(List<PointF3D> landmarks, PointF3D center) {
        List<PointF3D> translatedLandmarks = new ArrayList<>();
        for (PointF3D point : landmarks) {
            float x = point.getX() - center.getX();
            float y = point.getY() - center.getY();
            float z = point.getZ() - center.getZ();
            translatedLandmarks.add(PointF3D.from(x, y, z));
        }
        return translatedLandmarks;
    }

    // Method to calculate the bounding box size (diagonal length)
    private float calculateBoundingBoxSize(List<PointF3D> landmarks) {
        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, minZ = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE, maxY = Float.MIN_VALUE, maxZ = Float.MIN_VALUE;

        for (PointF3D point : landmarks) {
            minX = Math.min(minX, point.getX());
            minY = Math.min(minY, point.getY());
            minZ = Math.min(minZ, point.getZ());
            maxX = Math.max(maxX, point.getX());
            maxY = Math.max(maxY, point.getY());
            maxZ = Math.max(maxZ, point.getZ());
        }

        float dx = maxX - minX;
        float dy = maxY - minY;
        float dz = maxZ - minZ;

        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    // Method to scale the landmarks to a uniform size
    private List<PointF3D> scaleLandmarks(List<PointF3D> landmarks, float scalingFactor) {
        List<PointF3D> scaledLandmarks = new ArrayList<>();
        for (PointF3D point : landmarks) {
            float x = point.getX() * scalingFactor;
            float y = point.getY() * scalingFactor;
            float z = point.getZ() * scalingFactor;
            scaledLandmarks.add(PointF3D.from(x, y, z));
        }
        return scaledLandmarks;
    }

    // Method to calculate the normalized direction vector from toes to head
    private PointF3D calculateDirectionVectorForYAxis(PointF3D head, PointF3D toes) {
        float dx = head.getX() - toes.getX();
        float dy = head.getY() - toes.getY();
        float dz = head.getZ() - toes.getZ();

        float magnitude = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);

        return PointF3D.from(dx / magnitude, dy / magnitude, dz / magnitude);
    }

    // Method to calculate the rotation angle to align the direction vector with the positive y-axis
    private float calculateRotationAngleForYAxis(PointF3D directionVector) {
        float targetX = 0.0f, targetY = 1.0f; // Align with positive y-axis
        return (float) Math.toDegrees(
                Math.atan2(directionVector.getY(), directionVector.getX()) - Math.atan2(targetY, targetX)
        );
    }

    // Method to rotate all points to align with the positive y-axis
    private List<PointF3D> rotateToAlignWithYAxis(List<PointF3D> landmarks, float angle) {
        double radians = Math.toRadians(angle);
        List<PointF3D> rotatedLandmarks = new ArrayList<>();

        for (PointF3D point : landmarks) {
            float x = point.getX();
            float y = point.getY();

            float xRotated = (float) (x * Math.cos(radians) - y * Math.sin(radians));
            float yRotated = (float) (x * Math.sin(radians) + y * Math.cos(radians));

            rotatedLandmarks.add(PointF3D.from(xRotated, yRotated, point.getZ()));
        }

        return rotatedLandmarks;
    }

    // Main method to normalize the 3D pose landmarks
    public List<PointF3D> normalizePose(List<PointF3D> landmarks) {
        // Step 1: Calculate the center point
        PointF3D centerPoint = calculateCenterPoint(landmarks);

        // Step 2: Translate all landmarks to center point (center becomes {0, 0, 0})
        List<PointF3D> translatedLandmarks = translateToCenter(landmarks, centerPoint);

        // Step 3: Calculate the bounding box size
        float currentSize = calculateBoundingBoxSize(translatedLandmarks);

        // Step 4: Scale landmarks to the target size
        float scalingFactor = TARGET_SIZE / currentSize;
        List<PointF3D> scaledLandmarks = scaleLandmarks(translatedLandmarks, scalingFactor);

        // Step 5: Determine the direction vector from toes to head
        PointF3D directionVector = calculateDirectionVectorForYAxis(scaledLandmarks.get(0), scaledLandmarks.get(landmarks.size() - 1));

        // Step 6: Calculate rotation angle to align the direction with the positive y-axis
        float rotationAngle = calculateRotationAngleForYAxis(directionVector);

        // Step 7: Rotate all landmarks to align the body with the positive y-axis
        return rotateToAlignWithYAxis(scaledLandmarks, rotationAngle);
    }
}

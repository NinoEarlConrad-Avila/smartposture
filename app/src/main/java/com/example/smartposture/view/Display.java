package com.example.smartposture.view;

import static java.lang.Math.max;
import static java.lang.Math.min;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import com.example.smartposture.posedetector.GraphicOverlay;
import com.example.smartposture.posedetector.GraphicOverlay.Graphic;
import com.google.mlkit.vision.common.PointF3D;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

import java.util.List;
import java.util.Locale;

public class Display extends Graphic {
    private static final float DOT_RADIUS = 8.0f;
    private static final float IN_FRAME_LIKELIHOOD_TEXT_SIZE = 30.0f;
    private static final float STROKE_WIDTH = 10.0f;
    private static final float POSE_CLASSIFICATION_TEXT_SIZE = 60.0f;
    private final boolean showInFrameLikelihood;
    private Pose pose;
    private final boolean visualizeZ;
    private final boolean rescaleZForVisualization;
    private float zMin = Float.MAX_VALUE;
    private float zMax = Float.MIN_VALUE;
    private final List<String> poseClassification;
    private final Paint classificationTextPaint;
//    private final Paint leftPaint;
//    private final Paint rightPaint;
    private final Paint whitePaint;
    private final Paint alertPaint;
    private final Paint linePaint;


    public Display(GraphicOverlay overlay,
                   Pose pose,
                   boolean showInFrameLikelihood,
                   boolean visualizeZ,
                   boolean rescaleZForVisualization,
                   List<String> poseClassification) {
        super(overlay);
        this.pose = pose;
        this.showInFrameLikelihood = showInFrameLikelihood;
        this.visualizeZ = visualizeZ;
        this.rescaleZForVisualization = rescaleZForVisualization;
        this.poseClassification = poseClassification;
        classificationTextPaint = new Paint();
        classificationTextPaint.setColor(Color.RED);
        classificationTextPaint.setTextSize(POSE_CLASSIFICATION_TEXT_SIZE);
        classificationTextPaint.setShadowLayer(5.0f, 0f, 0f, Color.BLACK);


        linePaint = new Paint();
        linePaint.setStrokeWidth(STROKE_WIDTH);
        linePaint.setColor(Color.WHITE);
        whitePaint = new Paint();
        whitePaint.setStrokeWidth(STROKE_WIDTH);
        whitePaint.setColor(Color.WHITE);
        whitePaint.setTextSize(50.0f);
        alertPaint = new Paint();
        alertPaint.setStrokeWidth(STROKE_WIDTH);
        alertPaint.setColor(Color.RED);
        alertPaint.setTextSize(50.0f);
//        leftPaint = new Paint();
//        leftPaint.setStrokeWidth(STROKE_WIDTH);
//        leftPaint.setColor(Color.GREEN);
//        rightPaint = new Paint();
//        rightPaint.setStrokeWidth(STROKE_WIDTH);
//        rightPaint.setColor(Color.YELLOW);
    }

    public void setPose(Pose pose) {
        this.pose = pose;
    }

    @Override
    public void draw(Canvas canvas){
        if (pose == null) {
            return;
        }

        List<PoseLandmark> landmarks = pose.getAllPoseLandmarks();
        if (landmarks.isEmpty()) {
            return;
        }

        for (PoseLandmark landmark : landmarks) {
            drawPoint(canvas, landmark, whitePaint);
            if (visualizeZ && rescaleZForVisualization) {
                zMin = min(zMin, landmark.getPosition3D().getZ());
                zMax = max(zMax, landmark.getPosition3D().getZ());
            }
        }

        // Get necessary landmarks for knee angle calculation
        PoseLandmark rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP);
        PoseLandmark rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE);
        PoseLandmark rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE);

        PoseLandmark leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP);
        PoseLandmark leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE);
        PoseLandmark leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE);

//        if (rightHip != null && rightKnee != null && rightAnkle != null && leftHip != null && leftKnee != null && leftAnkle != null) {
//            float[] rightHipCoords = {rightHip.getPosition3D().getX(), rightHip.getPosition3D().getY(), rightHip.getPosition3D().getZ()};
//            float[] rightKneeCoords = {rightKnee.getPosition3D().getX(), rightKnee.getPosition3D().getY(), rightKnee.getPosition3D().getZ()};
//            float[] rightAnkleCoords = {rightAnkle.getPosition3D().getX(), rightAnkle.getPosition3D().getY(), rightAnkle.getPosition3D().getZ()};
//
//            float rightKneeAngle = calculateKneeAngle3D(rightHipCoords, rightKneeCoords, rightAnkleCoords);
//
//            // Left foot angle
//            float[] leftHipCoords = {leftHip.getPosition3D().getX(), leftHip.getPosition3D().getY(), leftHip.getPosition3D().getZ()};
//            float[] leftKneeCoords = {leftKnee.getPosition3D().getX(), leftKnee.getPosition3D().getY(), leftKnee.getPosition3D().getZ()};
//            float[] leftAnkleCoords = {leftAnkle.getPosition3D().getX(), leftAnkle.getPosition3D().getY(), leftAnkle.getPosition3D().getZ()};
//
//            float leftKneeAngle = calculateKneeAngle3D(leftHipCoords, leftKneeCoords, leftAnkleCoords);
//
//            // Calculate mean angle
//            float kneeAngle = (rightKneeAngle + leftKneeAngle) / 2;
//            // Display right knee angle next to the right knee
//            float midX = (translateX(rightKnee.getPosition().x) + translateX(leftKnee.getPosition().x)) / 2;
//            float midY = (translateY(rightKnee.getPosition().y) + translateY(leftKnee.getPosition().y)) / 2;

//            canvas.drawText(String.format(Locale.US, "%.1f°", kneeAngle),
//                    midX + 20,
//                    midY, whitePaint);
//        }

        // Draw pose classification text.
        float classificationX = POSE_CLASSIFICATION_TEXT_SIZE * 0.5f;
        for (int i = 0; i < poseClassification.size(); i++) {
            float classificationY =
                    (canvas.getHeight()
                            - POSE_CLASSIFICATION_TEXT_SIZE * 1.5f * (poseClassification.size() - i));
            canvas.drawText(
                    poseClassification.get(i), classificationX, classificationY, classificationTextPaint);
        }

        PoseLandmark nose = pose.getPoseLandmark(PoseLandmark.NOSE);
        PoseLandmark lefyEyeInner = pose.getPoseLandmark(PoseLandmark.LEFT_EYE_INNER);
        PoseLandmark lefyEye = pose.getPoseLandmark(PoseLandmark.LEFT_EYE);
        PoseLandmark leftEyeOuter = pose.getPoseLandmark(PoseLandmark.LEFT_EYE_OUTER);
        PoseLandmark rightEyeInner = pose.getPoseLandmark(PoseLandmark.RIGHT_EYE_INNER);
        PoseLandmark rightEye = pose.getPoseLandmark(PoseLandmark.RIGHT_EYE);
        PoseLandmark rightEyeOuter = pose.getPoseLandmark(PoseLandmark.RIGHT_EYE_OUTER);
        PoseLandmark leftEar = pose.getPoseLandmark(PoseLandmark.LEFT_EAR);
        PoseLandmark rightEar = pose.getPoseLandmark(PoseLandmark.RIGHT_EAR);
        PoseLandmark leftMouth = pose.getPoseLandmark(PoseLandmark.LEFT_MOUTH);
        PoseLandmark rightMouth = pose.getPoseLandmark(PoseLandmark.RIGHT_MOUTH);

        PoseLandmark leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER);
        PoseLandmark rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER);
        PoseLandmark leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW);
        PoseLandmark rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW);
        PoseLandmark leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST);
        PoseLandmark rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST);
//        PoseLandmark leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP);
//        PoseLandmark rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP);
//        PoseLandmark leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE);
//        PoseLandmark rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE);
//        PoseLandmark leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE);
//        PoseLandmark rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE);

        PoseLandmark leftPinky = pose.getPoseLandmark(PoseLandmark.LEFT_PINKY);
        PoseLandmark rightPinky = pose.getPoseLandmark(PoseLandmark.RIGHT_PINKY);
        PoseLandmark leftIndex = pose.getPoseLandmark(PoseLandmark.LEFT_INDEX);
        PoseLandmark rightIndex = pose.getPoseLandmark(PoseLandmark.RIGHT_INDEX);
        PoseLandmark leftThumb = pose.getPoseLandmark(PoseLandmark.LEFT_THUMB);
        PoseLandmark rightThumb = pose.getPoseLandmark(PoseLandmark.RIGHT_THUMB);
        PoseLandmark leftHeel = pose.getPoseLandmark(PoseLandmark.LEFT_HEEL);
        PoseLandmark rightHeel = pose.getPoseLandmark(PoseLandmark.RIGHT_HEEL);
        PoseLandmark leftFootIndex = pose.getPoseLandmark(PoseLandmark.LEFT_FOOT_INDEX);
        PoseLandmark rightFootIndex = pose.getPoseLandmark(PoseLandmark.RIGHT_FOOT_INDEX);

        // Face
        drawLine(canvas, nose, lefyEyeInner, linePaint);
        drawLine(canvas, lefyEyeInner, lefyEye, linePaint);
        drawLine(canvas, lefyEye, leftEyeOuter, linePaint);
        drawLine(canvas, leftEyeOuter, leftEar, linePaint);
        drawLine(canvas, nose, rightEyeInner, linePaint);
        drawLine(canvas, rightEyeInner, rightEye, linePaint);
        drawLine(canvas, rightEye, rightEyeOuter, linePaint);
        drawLine(canvas, rightEyeOuter, rightEar, linePaint);
        drawLine(canvas, leftMouth, rightMouth, linePaint);
        drawLine(canvas, leftShoulder, rightShoulder, linePaint);
        drawLine(canvas, leftHip, rightHip, linePaint);

        // Left body
        drawLine(canvas, leftShoulder, leftElbow, linePaint);
        drawLine(canvas, leftElbow, leftWrist, linePaint);
        drawLine(canvas, leftShoulder, leftHip, linePaint);
        drawLine(canvas, leftHip, leftKnee, linePaint);
        drawLine(canvas, leftKnee, leftAnkle, linePaint);
        drawLine(canvas, leftWrist, leftThumb, linePaint);
        drawLine(canvas, leftWrist, leftPinky, linePaint);
        drawLine(canvas, leftWrist, leftIndex, linePaint);
        drawLine(canvas, leftIndex, leftPinky, linePaint);
        drawLine(canvas, leftAnkle, leftHeel, linePaint);
        drawLine(canvas, leftHeel, leftFootIndex, linePaint);

        // Right body
        drawLine(canvas, rightShoulder, rightElbow, linePaint);
        drawLine(canvas, rightElbow, rightWrist, linePaint);
        drawLine(canvas, rightShoulder, rightHip, linePaint);
        drawLine(canvas, rightHip, rightKnee, linePaint);
        drawLine(canvas, rightKnee, rightAnkle, linePaint);
        drawLine(canvas, rightWrist, rightThumb, linePaint);
        drawLine(canvas, rightWrist, rightPinky, linePaint);
        drawLine(canvas, rightWrist, rightIndex, linePaint);
        drawLine(canvas, rightIndex, rightPinky, linePaint);
        drawLine(canvas, rightAnkle, rightHeel, linePaint);
        drawLine(canvas, rightHeel, rightFootIndex, linePaint);

//        if (rightShoulder != null && rightElbow != null && rightWrist != null) {
//            // Calculate right knee angle
//            float rightElbowAngle = calculateKneeAngle3D(
//                    new float[]{rightShoulder.getPosition3D().getX(), rightShoulder.getPosition3D().getY(), rightShoulder.getPosition3D().getZ()},
//                    new float[]{rightElbow.getPosition3D().getX(), rightElbow.getPosition3D().getY(), rightElbow.getPosition3D().getZ()},
//                    new float[]{rightWrist.getPosition3D().getX(), rightWrist.getPosition3D().getY(), rightWrist.getPosition3D().getZ()});

            // Display right knee angle next to the right knee
//            canvas.drawText(String.format(Locale.US, "%.1f°", rightElbowAngle),
//                    translateX(rightElbow.getPosition().x) + 20,
//                    translateY(rightElbow.getPosition().y), whitePaint);
//        }

//        if (leftShoulder != null && leftElbow != null && leftWrist != null) {
//            // Calculate left knee angle
//            float leftElbowAngle = calculateKneeAngle3D(
//                    new float[]{leftShoulder.getPosition3D().getX(), leftShoulder.getPosition3D().getY(), leftShoulder.getPosition3D().getZ()},
//                    new float[]{leftElbow.getPosition3D().getX(), leftElbow.getPosition3D().getY(), leftElbow.getPosition3D().getZ()},
//                    new float[]{leftWrist.getPosition3D().getX(), leftWrist.getPosition3D().getY(), leftWrist.getPosition3D().getZ()});

            // Display left knee angle next to the left knee
//            canvas.drawText(String.format(Locale.US, "%.1f°", leftElbowAngle),
//                    translateX(leftElbow.getPosition().x) + 20,
//                    translateY(leftElbow.getPosition().y), whitePaint);
        }
//        if (showInFrameLikelihood) {
//            for (PoseLandmark landmark : landmarks) {
//                canvas.drawText(
//                        String.format(Locale.US, "%.2f", landmark.getInFrameLikelihood()),
//                        translateX(landmark.getPosition().x),
//                        translateY(landmark.getPosition().y),
//                        whitePaint);
//            }
//        }
//    }


    void drawPoint(Canvas canvas, PoseLandmark landmark, Paint paint) {
        PointF3D point = landmark.getPosition3D();
        updatePaintColorByZValue(
                paint, canvas, visualizeZ, rescaleZForVisualization, point.getZ(), zMin, zMax);
        canvas.drawCircle(translateX(point.getX()), translateY(point.getY()), DOT_RADIUS, paint);
    }

    void drawLine(Canvas canvas, PoseLandmark startLandmark, PoseLandmark endLandmark, Paint paint) {
        PointF3D start = startLandmark.getPosition3D();
        PointF3D end = endLandmark.getPosition3D();

        // Gets average z for the current body line
        float avgZInImagePixel = (start.getZ() + end.getZ()) / 2;
        updatePaintColorByZValue(
                paint, canvas, visualizeZ, rescaleZForVisualization, avgZInImagePixel, zMin, zMax);

        canvas.drawLine(
                translateX(start.getX()),
                translateY(start.getY()),
                translateX(end.getX()),
                translateY(end.getY()),
                paint);
    }

    public float calculateKneeAngle3D(float[] rightHip, float[] rightKnee, float[] rightAnkle) {
        // Calculate the squared distances
        float hipToKneeSquared = (rightKnee[0] - rightHip[0]) * (rightKnee[0] - rightHip[0]) +
                (rightKnee[1] - rightHip[1]) * (rightKnee[1] - rightHip[1]) +
                (rightKnee[2] - rightHip[2]) * (rightKnee[2] - rightHip[2]);

        float kneeToAnkleSquared = (rightAnkle[0] - rightKnee[0]) * (rightAnkle[0] - rightKnee[0]) +
                (rightAnkle[1] - rightKnee[1]) * (rightAnkle[1] - rightKnee[1]) +
                (rightAnkle[2] - rightKnee[2]) * (rightAnkle[2] - rightKnee[2]);

        float hipToAnkleSquared = (rightAnkle[0] - rightHip[0]) * (rightAnkle[0] - rightHip[0]) +
                (rightAnkle[1] - rightHip[1]) * (rightAnkle[1] - rightHip[1]) +
                (rightAnkle[2] - rightHip[2]) * (rightAnkle[2] - rightHip[2]);

        // Now calculate the actual distances
        float hipToKnee = (float) Math.sqrt(hipToKneeSquared);
        float kneeToAnkle = (float) Math.sqrt(kneeToAnkleSquared);

        // Use the law of cosines to calculate the angle at the knee
        float cosTheta = (hipToKneeSquared + kneeToAnkleSquared - hipToAnkleSquared) /
                (2 * hipToKnee * kneeToAnkle);

        // Clamp the value of cosTheta to avoid numerical issues
        cosTheta = Math.max(-1.0f, Math.min(1.0f, cosTheta));

        // Calculate the angle in radians and then convert to degrees
        float angleInRadians = (float) Math.acos(cosTheta);
        return (int) Math.toDegrees(angleInRadians);
    }
}

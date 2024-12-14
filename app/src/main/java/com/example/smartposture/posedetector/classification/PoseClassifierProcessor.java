package com.example.smartposture.posedetector.classification;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.WorkerThread;

import com.example.smartposture.R;
import com.example.smartposture.viewmodel.PoseDetectorViewModel;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Accepts a stream of {@link Pose} for classification and rep counting.
 */
public class PoseClassifierProcessor {
  private ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
  private static final String TAG = "PoseClassifierProcessor";
  private static final float SQUAT_DOWN_ANGLE_THRESHOLD = 40.0f;
  private static final float SQUAT_UP_ANGLE_THRESHOLD = 110.0f;
  private MediaPlayer squatlower;
  private MediaPlayer alignforearms;
  private MediaPlayer lowerbody;
  private boolean isSquattingDown = true;
  private boolean isPushingDown = true;// Track if the user is squatting down
  private boolean forearmIsAlign = true; // Track forearm alignment
  private boolean badPostureDetected = false;  // Track if a bad posture is currently detected
  private float lowestSquatAngle = Float.MAX_VALUE;
  private float lowestPushupAngle = Float.MAX_VALUE;
  private String lastErrorResult = "";  // Store the last error result for squats
  private static String POSE_SAMPLES_FILE;
//  private static final String POSE_SAMPLES_FILE = "pose/fitness_pose_samples.csv";

  // Specify classes for which we want rep counting.
  private static final String PUSHUPS_CLASS = "pushups_down";
  private static final String SQUATS_CLASS = "squats_down";
  private static final String[] POSE_CLASSES = {
          PUSHUPS_CLASS, SQUATS_CLASS
  };

  private final boolean isStreamMode;

  private EMASmoothing emaSmoothing;
  private List<RepetitionCounter> repCounters;
  private PoseClassifier poseClassifier;
  private String lastRepResult;
  private PoseDetectorViewModel homeViewModel;
  private String type;
  private ArrayList<Float> scores = new ArrayList<>(); // Acts as a stack to hold scores
  boolean repetitionCompleted = false;
  private Context context;


  public PoseClassifierProcessor(Context context, boolean isStreamMode, PoseDetectorViewModel homeViewModel, String type) {
    Log.d(TAG, "PoseClassifierProcessor constructor started.");
    this.context = context;
    this.isStreamMode = isStreamMode;
    this.homeViewModel = homeViewModel;
    this.type = type;
    if(type != null && type.trim().equals("pushup")){
      POSE_SAMPLES_FILE = "pose/fitness_pose_samples.csv";
    }else if(type != null && type.trim().equals("squat")){
      POSE_SAMPLES_FILE = "pose/squats_sample_data.csv";
    }
    if (isStreamMode) {
      emaSmoothing = new EMASmoothing();
      repCounters = new ArrayList<>();
      lastRepResult = "";
    }
    loadPoseSamplesAsync(context);
    squatlower = MediaPlayer.create(context, R.raw.badpostureswatlower);
    alignforearms = MediaPlayer.create(context, R.raw.alignyourforearms);
    lowerbody = MediaPlayer.create(context, R.raw.loweryourbody);
  }

  public ArrayList<Float> getScores(){
    return scores;
  }
  public void resetScores(){
    scores = new ArrayList<>();
  }

  public void resetRepCount(){
    repCounters = new ArrayList<>();
  }
  private void loadPoseSamplesAsync(final Context context) {
    new Thread(() -> loadPoseSamples(context)).start();
  }

  @WorkerThread
  private void loadPoseSamples(Context context) {
    List<PoseSample> poseSamples = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(context.getAssets().open(POSE_SAMPLES_FILE)))) {
      String csvLine;
      while ((csvLine = reader.readLine()) != null) {
        PoseSample poseSample = PoseSample.getPoseSample(csvLine, ",");
        if (poseSample != null) {
          poseSamples.add(poseSample);
        }
      }
      poseClassifier = new PoseClassifier(poseSamples);
      if (isStreamMode) {
        for (String className : POSE_CLASSES) {
          repCounters.add(new RepetitionCounter(className, homeViewModel, type));
        }
      }
      Log.d(TAG, "Pose samples loaded successfully.");
    } catch (IOException e) {
      Log.e(TAG, "Error when loading pose samples.\n" + e);
    }
  }

  /**
   * Given a new {@link Pose} input, returns a list of formatted {@link String}s with Pose
   * classification results.
   *
   * <p>Currently it returns up to 2 strings as follows:
   * 0: PoseClass : X reps
   * 1: PoseClass : [0.0-1.0] confidence
   */
  @WorkerThread
  public List<String> getPoseResult(Pose pose) {
//    Log.d(TAG, "Getting pose result.");
    if (Looper.myLooper() == Looper.getMainLooper()) {
      throw new IllegalStateException("getPoseResult should not be called on the main thread.");
    }
    List<String> result = new ArrayList<>();
    if (poseClassifier == null) {
      Log.e(TAG, "PoseClassifier is not initialized.");
      return result; // or handle as needed
    }
    ClassificationResult classification = poseClassifier.classify(pose);

    if (isStreamMode) {
      classification = emaSmoothing.getSmoothedResult(classification);

      if (pose.getAllPoseLandmarks().isEmpty()) {
//        result.add(lastRepResult);
        return result;
      }
      // Check the classification for squat phases
      isSquattingDown = classification.getMaxConfidenceClass().equals("squats_down");
      // Check the exercise type and apply relevant corrections
      if (type.equals("squat")) {
        // Get key landmarks: hip, knee, ankle
        PoseLandmark rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP);
        PoseLandmark rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE);
        PoseLandmark rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE);
        PoseLandmark leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP);
        PoseLandmark leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE);
        PoseLandmark leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE);

        if (rightHip != null && rightKnee != null && rightAnkle != null && leftHip != null && leftKnee != null && leftAnkle != null) {
          float[] rightHipCoords = {rightHip.getPosition3D().getX(), rightHip.getPosition3D().getY(), rightHip.getPosition3D().getZ()};
          float[] rightKneeCoords = {rightKnee.getPosition3D().getX(), rightKnee.getPosition3D().getY(), rightKnee.getPosition3D().getZ()};
          float[] rightAnkleCoords = {rightAnkle.getPosition3D().getX(), rightAnkle.getPosition3D().getY(), rightAnkle.getPosition3D().getZ()};

          float rightKneeAngle = calculateAngle3D(rightHipCoords, rightKneeCoords, rightAnkleCoords);

          // Left foot angle
          float[] leftHipCoords = {leftHip.getPosition3D().getX(), leftHip.getPosition3D().getY(), leftHip.getPosition3D().getZ()};
          float[] leftKneeCoords = {leftKnee.getPosition3D().getX(), leftKnee.getPosition3D().getY(), leftKnee.getPosition3D().getZ()};
          float[] leftAnkleCoords = {leftAnkle.getPosition3D().getX(), leftAnkle.getPosition3D().getY(), leftAnkle.getPosition3D().getZ()};

          float leftKneeAngle = calculateAngle3D(leftHipCoords, leftKneeCoords, leftAnkleCoords);

          // Calculate mean angle
          float kneeAngle = (rightKneeAngle + leftKneeAngle) / 2;
          if (isSquattingDown) {
            // Update the lowest squat angle while squatting down
            lowestSquatAngle = Math.min(lowestSquatAngle, kneeAngle);
          } else {
            // Evaluate squat once transitioning up
            if (lowestSquatAngle != Float.MAX_VALUE) { // Ensure a squat phase occurred
              float scoreToAdd = 0;

              if (lowestSquatAngle > 90) {
                scoreToAdd = 0.25f;
              } else if (lowestSquatAngle <= 90 && lowestSquatAngle >= 50) {
                scoreToAdd = 0.5f;
              } else if (lowestSquatAngle < 50) {
                scoreToAdd = 1f;
              }
              // Only add the score once per repetition

              if (!repetitionCompleted) {
                // Add score to list
                if (scores.isEmpty()) {
                  scores.add(0f);  // Initialize the total score if the list is empty
                }
                scores.add(scoreToAdd);

                // Update total score
//              float totalScore = scores.stream().reduce(0f, Float::sum);
                scores.set(0, scores.get(0) + scoreToAdd);
                result.add(String.format(Locale.US, "Score for repetition %.2f: ", scores.get(0)));

                // Mark the repetition as completed
                repetitionCompleted = true;
              }
            }
          }
        }
      } else if (type.equals("pushup")) {
        // Pushup posture correction
        PoseLandmark shoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER); // Or RIGHT_SHOULDER
        PoseLandmark elbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW); // Or RIGHT_ELBOW
        PoseLandmark wrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST); // Or RIGHT_WRIST

        if (shoulder != null && elbow != null && wrist != null) {
          // Get 3D coordinates for the angle calculation
          float[] shoulderPoint = new float[]{shoulder.getPosition3D().getX(), shoulder.getPosition3D().getY(), shoulder.getPosition3D().getZ()};
          float[] elbowPoint = new float[]{elbow.getPosition3D().getX(), elbow.getPosition3D().getY(), elbow.getPosition3D().getZ()};
          float[] wristPoint = new float[]{wrist.getPosition3D().getX(), wrist.getPosition3D().getY(), wrist.getPosition3D().getZ()};

          // Calculate the angle between shoulder, elbow, and wrist
          float pushupAngle = calculateAngle3D(shoulderPoint, elbowPoint, wristPoint);

          isPushingDown = classification.getMaxConfidenceClass().equals("pushups_down");
          // Check if the angle meets the pushup threshold (e.g., elbow at 90 degrees or lower)
          if (!isPushingDown) {
            lowestPushupAngle = Float.MAX_VALUE;
          } else {
//            result.add("Movement: Push Down");
            if (pushupAngle < lowestPushupAngle) {
              lowestPushupAngle = pushupAngle;
            }
            // Additional checks for bad posture
            if (pushupAngle > 90) {
              result.add("Bad Posture: Lower your body further");
              if (!alignforearms.isPlaying()) {
                alignforearms.start();
              }
            }

            if (!forearmIsAlign){
              result.add("Bad Posture: Align your forearms vertically");
              if (!alignforearms.isPlaying()) {
                alignforearms.start();
              }
            }

            // Check forearm alignment
            checkForearmAlignment(elbowPoint, wristPoint, result);
          }
        }
      }

      for (RepetitionCounter repCounter : repCounters) {
        int repsBefore = repCounter.getNumRepeats();
        int repsAfter = repCounter.addClassificationResult(classification);
        if (repsAfter > repsBefore) {
          ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
          tg.startTone(ToneGenerator.TONE_PROP_BEEP);
          lastRepResult = String.format(
                  Locale.US, "%s : %d reps", repCounter.getClassName(), repsAfter);
          break;
        }
      }
      if (isSquattingDown) {
        repetitionCompleted = false;
      }
      result.add(lastRepResult);
    }
    return result;
  }

  // Function to check forearm alignment
  private void checkForearmAlignment(float[] elbowPoint, float[] wristPoint, List<String> result) {
    // Calculate the direction vectors for the forearm and vertical alignment
    float[] forearmVector = new float[]{
            wristPoint[0] - elbowPoint[0],
            wristPoint[1] - elbowPoint[1],
            wristPoint[2] - elbowPoint[2]
    };

    // Calculate the vertical direction vector
    float[] verticalVector = new float[]{0, 1, 0}; // Assuming Y-axis is vertical

    // Normalize the vectors
    float forearmMagnitude = (float) Math.sqrt(Math.pow(forearmVector[0], 2) + Math.pow(forearmVector[1], 2) + Math.pow(forearmVector[2], 2));
    float verticalMagnitude = (float) Math.sqrt(Math.pow(verticalVector[0], 2) + Math.pow(verticalVector[1], 2) + Math.pow(verticalVector[2], 2));

    // Prevent division by zero
    if (forearmMagnitude > 0 && verticalMagnitude > 0) {
      forearmVector[0] /= forearmMagnitude;
      forearmVector[1] /= forearmMagnitude;
      forearmVector[2] /= forearmMagnitude;

      verticalVector[0] /= verticalMagnitude;
      verticalVector[1] /= verticalMagnitude;
      verticalVector[2] /= verticalMagnitude;

      // Calculate the dot product to check alignment
      float dotProduct = forearmVector[0] * verticalVector[0] + forearmVector[1] * verticalVector[1] + forearmVector[2] * verticalVector[2];

      // If the dot product is close to 1, the forearm is aligned vertically
      if (dotProduct < 0.7) { // Adjust this threshold as needed
        forearmIsAlign = false;
      }
    }
  }

  public void cleanup() {
    if (emaSmoothing != null) {
      emaSmoothing.cleanup(); // Assuming emaSmoothing has a cleanup method
    }
    // Add other cleanup operations if needed
    Log.d(TAG, "PoseClassifierProcessor cleaned up.");
  }
  public float calculateAngle3D(float[] point1, float[] point2, float[] point3) {
    // Calculate the squared distances
    float hipToKneeSquared = (point2[0] - point1[0]) * (point2[0] - point1[0]) +
            (point2[1] - point1[1]) * (point2[1] - point1[1]) +
            (point2[2] - point1[2]) * (point2[2] - point1[2]);

    float kneeToAnkleSquared = (point3[0] - point2[0]) * (point3[0] - point2[0]) +
            (point3[1] - point2[1]) * (point3[1] - point2[1]) +
            (point3[2] - point2[2]) * (point3[2] - point2[2]);

    float hipToAnkleSquared = (point3[0] - point1[0]) * (point3[0] - point1[0]) +
            (point3[1] - point1[1]) * (point3[1] - point1[1]) +
            (point3[2] - point1[2]) * (point3[2] - point1[2]);

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

  // Method to trigger the pop-up
//  private void showBadPosturePopup(String alert) {
//    if (context instanceof FragmentActivity) {
//      FragmentActivity activity = (FragmentActivity) context;
//      activity.runOnUiThread(new Runnable() {
//        @Override
//        public void run() {
//          AlertDialog.Builder builder = new AlertDialog.Builder(context);
//          builder.setTitle("Posture Alert");
//          builder.setMessage(alert);
//
//          builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//              dialog.dismiss();
//            }
//          });
//
//          AlertDialog alertDialog = builder.create();
//          alertDialog.show();
//        }
//      });
//    }
//  }
}
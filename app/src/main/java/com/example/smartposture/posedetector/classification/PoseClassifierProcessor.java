package com.example.smartposture.posedetector.classification;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.WorkerThread;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartposture.viewmodel.HomeViewModel;
import com.google.mlkit.vision.pose.Pose;

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
  private static final String TAG = "PoseClassifierProcessor";
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
  private HomeViewModel homeViewModel;
  private String type;


  public PoseClassifierProcessor(Context context, boolean isStreamMode, HomeViewModel homeViewModel, String type) {
    Log.d(TAG, "PoseClassifierProcessor constructor started.");
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
    Log.d(TAG, "Getting pose result.");
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
        result.add(lastRepResult);
        return result;
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
      result.add(lastRepResult);
    }

    if (!pose.getAllPoseLandmarks().isEmpty()) {
      String maxConfidenceClass = classification.getMaxConfidenceClass();
      String maxConfidenceClassResult = String.format(
              Locale.US,
              "%s : %.2f confidence",
              maxConfidenceClass,
              classification.getClassConfidence(maxConfidenceClass)
                      / poseClassifier.confidenceRange());
      result.add(maxConfidenceClassResult);
    }

    return result;
  }

  public void cleanup() {
    if (emaSmoothing != null) {
      emaSmoothing.cleanup(); // Assuming emaSmoothing has a cleanup method
    }
    // Add other cleanup operations if needed
    Log.d(TAG, "PoseClassifierProcessor cleaned up.");
  }
}

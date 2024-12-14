/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.smartposture.posedetector.classification;

import com.example.smartposture.viewmodel.PoseDetectorViewModel;

/**
 * Counts reps for the give class.
 */
public class RepetitionCounter {
  // These thresholds can be tuned in conjunction with the Top K values in {@link PoseClassifier}.
  // The default Top K value is 10 so the range here is [0-10].
  private static final float DEFAULT_ENTER_THRESHOLD = 6f;
  private static final float DEFAULT_EXIT_THRESHOLD = 4f;

  private final String className;
  private final float enterThreshold;
  private final float exitThreshold;

  private int numRepeats;
  private boolean poseEntered;
  private PoseDetectorViewModel homeViewModel;
  private String type;

  public RepetitionCounter(String className, PoseDetectorViewModel homeViewModel, String type) {
    this(className, DEFAULT_ENTER_THRESHOLD, DEFAULT_EXIT_THRESHOLD, homeViewModel, type);
  }

  public RepetitionCounter(String className, float enterThreshold, float exitThreshold, PoseDetectorViewModel homeViewModel, String type) {
    this.className = className;
    this.enterThreshold = enterThreshold;
    this.exitThreshold = exitThreshold;
    numRepeats = 0;
    poseEntered = false;
    this.homeViewModel = homeViewModel;
    this.type = type;
  }

  /**
   * Adds a new Pose classification result and updates reps for given class.
   *
   * @param classificationResult {link ClassificationResult} of class to confidence values.
   * @return number of reps.
   */
  public int addClassificationResult(ClassificationResult classificationResult) {
    float poseConfidence = classificationResult.getClassConfidence(className);

    if (!poseEntered) {
      poseEntered = poseConfidence > enterThreshold;
      return numRepeats;
    }

    if (poseConfidence < exitThreshold) {
      numRepeats++;
      if(type != null && type.trim().equals("pushup")){
//        homeViewModel.incrementPushupCount();
      }else if(type != null && type.trim().equals("squat")){
//        homeViewModel.incrementSquatCount();
      }

      poseEntered = false;
    }

    return numRepeats;
  }

  public String getClassName() {
    return className;
  }

  public int getNumRepeats() {
    return numRepeats;
  }
}

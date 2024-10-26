//package com.example.smartposture.posedetector;
//
//import android.content.Context;
//
//import androidx.annotation.NonNull;
//
//import com.example.smartposture.view.fragment.Display;
//import com.google.mlkit.vision.pose.Pose;
//import com.google.mlkit.vision.pose.PoseDetection;
//import com.google.mlkit.vision.pose.PoseDetector;
//import com.google.mlkit.vision.pose.PoseDetectorOptionsBase;
//
//import java.util.List;
//import java.util.concurrent.Executors;
//
//public class PoseDetectorProcessor extends VisionProcessorBase<PoseDetectorProcessor.PoseWithClassification>{
////    private final PoseDetector detector;
//    private static final String TAG = "PoseDetectorProcessor";
//    private final PoseDetector detector;
//
//    private final boolean showInFrameLikelihood;
//    private final boolean visualizeZ;
//    private final boolean rescaleZForVisualization;
////    private final boolean runClassification;
//    private final boolean isStreamMode;
//    private final Context context;
//    private PoseClassifierProcessor poseClassifierProcessor;
//
//
//    protected static class PoseWithClassification {
//        private final Pose pose;
//        private final List<String> classificationResult;
//
//        public PoseWithClassification(Pose pose, List<String> classificationResult) {
//            this.pose = pose;
//            this.classificationResult = classificationResult;
//        }
//
//    public PoseDetectorProcessor(Context context,
//                                 PoseDetectorOptionsBase options,
//                                 boolean showInFrameLikelihood,
//                                 boolean visualizeZ,
//                                 boolean rescaleZForVisualization){
//        super(context);
//        this.showInFrameLikelihood = showInFrameLikelihood;
//        this.visualizeZ = visualizeZ;
//        this.rescaleZForVisualization = rescaleZForVisualization;
//        detector = PoseDetection.getClient(options);
//        this.isStreamMode = true;
//        this.context = context;
//    }
//    @Override
//    public void stop() {
//        super.stop();
//        detector.close();
//    }
//    @Override
//    protected void onSuccess(
////            @NonNull PoseWithClassification poseWithClassification,
//            @NonNull GraphicOverlay graphicOverlay) {
//        graphicOverlay.add(
//                new Display(
//                        graphicOverlay,
//                        pose,
////                        showInFrameLikelihood,
//                        visualizeZ,
//                        rescaleZForVisualization
////                        poseWithClassification.classificationResult
//                ));
//    }
//
//
//}

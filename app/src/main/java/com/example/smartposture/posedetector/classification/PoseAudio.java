package com.example.smartposture.posedetector.classification;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.HashMap;
import java.util.Locale;

public class PoseAudio {

    private TextToSpeech textToSpeech;
    private boolean isInitialized = false;
    private boolean isSpeaking = false;

    public PoseAudio(Context context) {
        textToSpeech = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.US); // Set language to US English
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("PoseAudio", "Language not supported");
                } else {
                    isInitialized = true;
                }

                // Set the deprecated OnUtteranceCompletedListener
                textToSpeech.setOnUtteranceCompletedListener(utteranceId -> {
                    isSpeaking = false;
                });
            } else {
                Log.e("PoseAudio", "Initialization failed");
            }
        });
    }

    /**
     * Speaks out the given text if no other audio is currently playing.
     *
     * @param text The text to be spoken as feedback.
     */
    public void speak(String text) {
        if (isInitialized) {
            if (!isSpeaking) {
                isSpeaking = true; // Mark as speaking
                HashMap<String, String> params = new HashMap<>();
                params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "pose_audio_utterance");
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, params);
            } else {
                Log.d("PoseAudio", "Currently speaking. Waiting to finish.");
            }
        } else {
            Log.e("PoseAudio", "TextToSpeech not initialized");
        }
    }

    /**
     * Releases the TextToSpeech resources to prevent memory leaks.
     */
    public void release() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }
}


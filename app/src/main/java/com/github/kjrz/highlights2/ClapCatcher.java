package com.github.kjrz.highlights2;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import be.hogent.tarsos.dsp.MicrophoneAudioDispatcher;
import be.hogent.tarsos.dsp.onsets.OnsetHandler;
import be.hogent.tarsos.dsp.onsets.PercussionOnsetDetector;

import static be.hogent.tarsos.dsp.onsets.PercussionOnsetDetector.DEFAULT_SENSITIVITY;
import static be.hogent.tarsos.dsp.onsets.PercussionOnsetDetector.DEFAULT_THRESHOLD;

/**
 * @author kjrz
 */
public class ClapCatcher {
    private static final String TAG = "Clapper";

    public static final String SENSITIVITY = "sensitivity";

    public static final int SAMPLE_RATE = 44100;
    public static final int BUFFER_SIZE = 2048;
    public static final int BUFFER_OVERLAP = 1024;

    private MicrophoneAudioDispatcher audioDispatcher;
    private OnsetHandler clapHandler;

    private int sensitivity;

    protected void onCreate(int sensitivity, OnsetHandler clapHandler) {
        setSensitivity(sensitivity);
        setClapHandler(clapHandler);
    }

    public void setSensitivity(int sensitivity) {
        this.sensitivity = sensitivity;
        Log.d(TAG, "sensitivity = " + sensitivity);
    }

    private void setClapHandler(OnsetHandler clapHandler) {
        this.clapHandler = clapHandler;
    }

    public void onResume() {
        audioDispatcher = new MicrophoneAudioDispatcher(SAMPLE_RATE, BUFFER_SIZE, BUFFER_OVERLAP);
        PercussionOnsetDetector detector = new PercussionOnsetDetector(
                SAMPLE_RATE, BUFFER_SIZE, clapHandler, sensitivity, DEFAULT_THRESHOLD);
        audioDispatcher.addAudioProcessor(detector);
        (new Thread(audioDispatcher)).start();
    }

    public void onPause() {
        audioDispatcher.stop();
    }

    public static int getSensitivityFromPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getInt(SENSITIVITY, (int) DEFAULT_SENSITIVITY);
    }

    public static void saveSensitivityInPreferences(Context ctx, int sensitivity) {
        SharedPreferences.Editor pref = PreferenceManager.getDefaultSharedPreferences(ctx).edit();
        pref.putInt(ClapCatcher.SENSITIVITY, sensitivity);
        pref.apply();
    }
}

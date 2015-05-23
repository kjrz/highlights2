package com.github.kjrz.highlights2;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.grafika.CircularEncoder;

import java.lang.ref.WeakReference;

/**
 * @author kjrz
 */
public class CircEncoderHandler extends Handler implements CircularEncoder.Callback {

    private static final String TAG = "ContinuousCapture";
    public static final int MSG_BLINK_TEXT = 0;
    public static final int MSG_FRAME_AVAILABLE = 1;
    public static final int MSG_FILE_SAVE_COMPLETE = 2;
    public static final int MSG_BUFFER_STATUS = 3;

    private WeakReference<CameraContControlTower2> mWeakControllerRef;

    public CircEncoderHandler(CameraContControlTower2 cont) {
        mWeakControllerRef = new WeakReference<>(cont);
    }

    @Override
    public void fileSaveComplete(int status) {
        sendMessage(obtainMessage(MSG_FILE_SAVE_COMPLETE, status, 0, null));
    }

    @Override
    public void bufferStatus(long totalTimeMsec) {
        sendMessage(obtainMessage(MSG_BUFFER_STATUS, (int) (totalTimeMsec >> 32), (int) totalTimeMsec));
    }

    @Override
    public void handleMessage(Message msg) {
        CameraContControlTower2 control = mWeakControllerRef.get();
        if (control == null) {
            Log.d(TAG, "Got message for dead activity");
            return;
        }

        switch (msg.what) {
            case MSG_BLINK_TEXT:
                // disused
                break;
            case MSG_FRAME_AVAILABLE:
                control.drawFrame();
                break;
            case MSG_FILE_SAVE_COMPLETE:
                control.fileSaveComplete();
                break;
            case MSG_BUFFER_STATUS:
                // disused
                break;
            default:
                throw new RuntimeException("Unknown message " + msg.what);
        }

    }
}

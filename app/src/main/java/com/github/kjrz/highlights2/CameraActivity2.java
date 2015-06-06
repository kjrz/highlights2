package com.github.kjrz.highlights2;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;

import be.hogent.tarsos.dsp.onsets.OnsetHandler;

/**
 * @author kjrz
 */
public class CameraActivity2 extends Activity {
    private static final String TAG = "CameraActivity2";
    public static final String STORAGE_DIR = "highlights2";

    private CameraPreview2 mPreview;
    private Camera mCamera;
    private File mStorageDir;

    private final ClapCatcher mClapper = new ClapCatcher();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout();
        setPreview();
        getStorageDir();
        setClapHandler();
    }

    private void setLayout() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera2);
    }

    private void setPreview() {
        mPreview = new CameraPreview2(this, (SurfaceView) findViewById(R.id.surfaceView_camActivity2));
        mPreview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ((FrameLayout) findViewById(R.id.layout)).addView(mPreview);
        mPreview.setKeepScreenOn(true);
        mPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFile();
            }
        });
    }

    private void saveFile() {
        String timeStamp = DateFormat.getDateTimeInstance().format(new Date());
        File file = new File(mStorageDir.getPath(), "highlight_" + timeStamp + ".mp4");
        mPreview.saveFile(file);
    }

    private void getStorageDir() {
        mStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), STORAGE_DIR);
        if (!mStorageDir.exists()) {
            if (!mStorageDir.mkdirs()) {
                Log.w(TAG, "failed to create directory");
            }
        }
    }

    private void setClapHandler() {
        mClapper.onCreate(ClapCatcher.getSensitivityFromPreferences(this), new OnsetHandler() {
            @Override
            public void handleOnset(double time, double salience) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        saveFile();
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCamera = Camera.open(0);
        mPreview.setCamera(mCamera);
        mClapper.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCamera == null) return;
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
        mClapper.onPause();
    }
}

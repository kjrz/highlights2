package com.github.kjrz.highlights2;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.android.grafika.AspectFrameLayout;
import com.android.grafika.CameraUtils;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author kjrz
 */
public class CameraActivity2 extends Activity {
    private static final String TAG = "CameraActivity2";

    private CameraPreview2 mPreview;
    private Camera mCamera;
    private File mStorageDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout();
        setPreview();
        getStorageDir();
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
        mStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "highlights2");
        if (!mStorageDir.exists()) {
            if (!mStorageDir.mkdirs()) {
                Log.w(TAG, "failed to create directory");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCamera = Camera.open(0);
        mPreview.setCamera(mCamera);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCamera == null) return;
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }
}

package com.github.kjrz.highlights2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.android.grafika.CameraUtils;

import java.io.File;
import java.util.List;

/**
 * @author kjrz
 */
@SuppressLint("ViewConstructor")
public class CameraPreview2 extends ViewGroup implements SurfaceHolder.Callback {
    private static final String TAG = "CameraPreview2";
    private static final int FPS = 15;

    private Camera mCamera;
    private Camera.Size mPreviewSize;
    private int mFps;
    private final Context mContext;
    private CameraContControlTower2 mCont;

    public CameraPreview2(Context context, SurfaceView view) {
        super(context);
        view.getHolder().addCallback(this); // TODO?
        mContext = context;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        requestLayout(); // mPreviewSize
        mCont = new CameraContControlTower2(mContext, holder.getSurface(), mPreviewSize, mFps);
        Log.i(TAG, "mCont initialised");
        mCont.startCamera(mCamera);
        Log.i(TAG, "camera preview started");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // disused
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // disused
    }

    public void setCamera(Camera camera) {
        mCamera = camera;
        configureCamera(camera);
        Log.i(TAG, "camera set");
    }

    private void configureCamera(Camera camera) {
        Camera.Parameters params = camera.getParameters();
        params.setRecordingHint(true);
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        mFps = CameraUtils.chooseFixedPreviewFps(params, FPS * 1000);
        camera.setParameters(params);
        Log.i(TAG, "camera configured");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);
        if (mPreviewSize != null) return;
        mPreviewSize = getOptimalPreviewSize(width, height);
        Log.i(TAG, "mPreviewSize = " + mPreviewSize.width + ", " + mPreviewSize.height);
    }

    private Camera.Size getOptimalPreviewSize(int w, int h) {
        double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        List<Camera.Size> sizes = mCamera.getParameters().getSupportedPictureSizes();
        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - h) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - h);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - h) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - h);
                }
            }
        }
        return optimalSize;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (!changed || getChildCount() <= 0) return;

        View child = getChildAt(0);

        int width = r - l;
        int height = b - t;

        int previewWidth = width;
        int previewHeight = height;
        if (mPreviewSize != null) {
            previewWidth = mPreviewSize.width;
            previewHeight = mPreviewSize.height;
        }

        if (width * previewHeight > height * previewWidth) {
            int scaledChildWidth = previewWidth * height / previewHeight;
            child.layout((width - scaledChildWidth) / 2, 0,
                    (width + scaledChildWidth) / 2, height);
        } else {
            int scaledChildHeight = previewHeight * width / previewWidth;
            child.layout(0, (height - scaledChildHeight) / 2,
                    width, (height + scaledChildHeight) / 2);
        }
    }

    public void saveFile(File outputFile) {
        mCont.saveVideo(outputFile);
    }
}

package com.github.kjrz.highlights2;

import android.content.Context;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.net.Uri;
import android.opengl.GLES20;
import android.util.Log;
import android.view.Surface;
import android.widget.Toast;

import com.android.grafika.CircularEncoder;
import com.android.grafika.gles.EglCore;
import com.android.grafika.gles.FullFrameRect;
import com.android.grafika.gles.Texture2dProgram;
import com.android.grafika.gles.WindowSurface;

import java.io.File;
import java.io.IOException;

/**
 * @author kjrz
 */
public class CameraContControlTower2 implements SurfaceTexture.OnFrameAvailableListener {
    private static final String TAG = "CameraContinuous2";
    private final Context mContext;

    private FullFrameRect mFullFrameBlit;
    private int mTextureId;
    private SurfaceTexture mCameraTexture;
    private CircularEncoder mCircEncoder;
    private WindowSurface mEncoderSurface;
    private CircEncoderHandler mHandler;
    private EglCore mEglCore;
    private WindowSurface mDisplaySurface;
    private float[] mTmpMatrix = new float[16];

    private File mStorageDir;
    private final Camera.Size mPreviewSize;

    public CameraContControlTower2(Context ctx, Surface previewSurface, Camera.Size size, int fps) {
        mContext = ctx;

        mEglCore = new EglCore(null, EglCore.FLAG_RECORDABLE);
        mDisplaySurface = new WindowSurface(mEglCore, previewSurface, false);
        mDisplaySurface.makeCurrent();

        mFullFrameBlit = new FullFrameRect(new Texture2dProgram(Texture2dProgram.ProgramType.TEXTURE_EXT));
        mTextureId = mFullFrameBlit.createTextureObject();
        mCameraTexture = new SurfaceTexture(mTextureId);
        mCameraTexture.setOnFrameAvailableListener(this);

        mHandler = new CircEncoderHandler(this);

        mPreviewSize = size;
        try {
            mCircEncoder = new CircularEncoder(mPreviewSize.width, mPreviewSize.height, 6000000, fps / 1000, 7, mHandler);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        mEncoderSurface = new WindowSurface(mEglCore, mCircEncoder.getInputSurface(), true);
    }

    public void startCamera(Camera camera) {
        try {
            camera.setPreviewTexture(mCameraTexture);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        camera.startPreview();
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        mHandler.sendEmptyMessage(CircEncoderHandler.MSG_FRAME_AVAILABLE);
    }

    public void drawFrame() {
        if (mEglCore == null) {
            Log.d(TAG, "Skipping drawFrame after shutdown");
            return;
        }

        // Latch the next frame from the camera.
        mDisplaySurface.makeCurrent();
        mCameraTexture.updateTexImage();
        mCameraTexture.getTransformMatrix(mTmpMatrix);

        // Fill the SurfaceView with it.
        GLES20.glViewport(0, 0, mPreviewSize.width, mPreviewSize.height);
        mFullFrameBlit.drawFrame(mTextureId, mTmpMatrix);
//        drawExtra(mFrameNum, viewWidth, viewHeight);
        mDisplaySurface.swapBuffers();

        // Send it to the video encoder.
//        if (!mFileSaveInProgress) {
        mEncoderSurface.makeCurrent();
        GLES20.glViewport(0, 0, mPreviewSize.width, mPreviewSize.height);
        mFullFrameBlit.drawFrame(mTextureId, mTmpMatrix);
//            drawExtra(mFrameNum, VIDEO_WIDTH, VIDEO_HEIGHT);
        mCircEncoder.frameAvailableSoon();
        mEncoderSurface.setPresentationTime(mCameraTexture.getTimestamp());
        mEncoderSurface.swapBuffers();
//        }

//        mFrameNum++;
    }

    public void saveVideo(File outputFile) {
        mStorageDir = outputFile.getParentFile();
        mCircEncoder.saveVideo(outputFile);
    }

    public void fileSaveComplete() {
        reScanStorageDir();
        Toast.makeText(mContext, "File save complete", Toast.LENGTH_SHORT).show();
    }

    private void reScanStorageDir() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(mStorageDir));
        mContext.sendBroadcast(mediaScanIntent);
    }
}

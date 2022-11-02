package com.mygdx.jar;

import android.os.Handler;
import android.os.HandlerThread;

public class CameraHandlerThread extends HandlerThread {
    private final CameraLauncher cameraLauncher;
    Handler mHandler = null;

    CameraHandlerThread(CameraLauncher launcher) {
        super("CameraHandlerThread");
        start();
        mHandler = new Handler(getLooper());
        cameraLauncher = launcher;
    }

    private void stopBackgroundThread() {
        this.quitSafely();
        try {
            this.join();
            mHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
package com.mygdx.jar;

import android.os.Handler;
import android.os.HandlerThread;

public class CameraHandlerThread extends HandlerThread {
    private final CameraLauncher cameraLauncher;
    Handler mHandler = null;
    private boolean keepRunning;

    CameraHandlerThread(CameraLauncher launcher) {
        super("CameraHandlerThread");
        start();
        mHandler = new Handler(getLooper());
        cameraLauncher = launcher;
        keepRunning = true;
    }

    synchronized void notifyCameraOpened() {
        notify();
    }

    synchronized void openCamera() {
        boolean success = mHandler.post(new Runnable() {
            @Override
            public void run() {
                notifyCameraOpened();
                while (keepRunning){
                    cameraLauncher.captureImage();
                    System.out.println("Thread Running $$$$$$$$");
                }
            }
        });
        System.out.println("Success = " + success);
    }

    public void stopRun(){
        keepRunning = false;
    }
}
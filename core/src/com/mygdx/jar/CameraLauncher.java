package com.mygdx.jar;

import com.badlogic.gdx.graphics.Texture;

import java.io.File;

public interface CameraLauncher {
    Texture getCapturedImage();

    void captureImage();

    void openCamera();

    void closeCamera();

    void openGallery();

    void share();

    File getImagesDir();

    File getStoredImage();
}

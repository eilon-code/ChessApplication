package com.mygdx.jar;

import com.badlogic.gdx.graphics.Texture;

public interface CameraLauncher {
    Texture getCapturedImage();

    void captureImage();

    void openCamera();

    void closeCamera();
}

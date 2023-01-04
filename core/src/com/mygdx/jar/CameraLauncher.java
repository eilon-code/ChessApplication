package com.mygdx.jar;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mygdx.jar.gameObjects.BoardObjects.Board;

import java.io.File;
import java.util.Stack;

public interface CameraLauncher {
    void askAllPermissions();

    boolean isPermissionGranted();

    Texture getCapturedImage();
    
    void captureImage();

    void openCamera();

    void closeCamera();

    void openGallery();

    void share(String text);

    File getImagesDir();

    File getStoredImage();

    void resetSQL();

    void addBoard(Board board, int boardNum);

    void updateBoard(Board board, int boardNum);

    Stack<Board> getStackFromStorage();

    void deleteBoard(int boardNum);

    void deleteAll();

    void addCameraActor(Stage stage);

    void addSpriteBatchToTextureView(SpriteBatch batch);
}

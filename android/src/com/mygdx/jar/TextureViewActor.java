package com.mygdx.jar;

import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.Looper;
import android.view.TextureView;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class TextureViewActor extends Actor {
    private final TextureView textureView;
    private Texture texture;

    public TextureViewActor(final TextureView textureView, File imagesDirectory) {
        String filePath = imagesDirectory.getPath() + File.separator + "cameraTexture.png";
        this.textureView = textureView;
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = textureView.getBitmap();
                if (bitmap != null) {
                    try {
                        FileOutputStream fos = null;
                        fos = new FileOutputStream(filePath);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        fos.close();
                        System.out.println("Success");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    texture = new Texture(filePath);
                } else {
                    handler.postDelayed(this, 100);
                }
            }
        };
        handler.post(runnable);
    }

    @Override
    public void draw(final Batch batch, float parentAlpha) {
        final SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
        if (surfaceTexture != null) {
            // Update the camera preview
            surfaceTexture.updateTexImage();

            // Draw the Texture
            batch.draw(texture, getX(), getY(), getWidth(), getHeight());
            System.out.println("It should print your beloved Camera Output!");
        }
    }


    public TextureView getTextureView(){
        return textureView;
    }

    public Texture getTexture() {
        return this.texture;
    }
}
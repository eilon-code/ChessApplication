package com.mygdx.jar;

import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.Looper;
import android.view.TextureView;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
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
                final Bitmap bitmap = textureView.getBitmap();
                if (bitmap != null && bitmap.getByteCount() > 0) {
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            int width = bitmap.getWidth();
                            int height = bitmap.getHeight();
                            Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);

                            int[] pixels = new int[width * height];
                            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
                            pixmap.getPixels().asIntBuffer().put(pixels);

                            texture = new Texture(pixmap);
                        }
                    });
                }
                else {
                    System.out.println("Bitmap is null");
                    handler.postDelayed(this, 100);
                }
            }
        };
        handler.post(runnable);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        System.out.println("act method called");
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
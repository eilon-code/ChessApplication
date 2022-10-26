package com.mygdx.jar.imageHandlersObjects;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.ScreenUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;

public class ScreenshotFactory {

    private static int counter = 1;

    public static void saveScreenshot(int x, int y, int width, int height){
        try{
            FileHandle fh;
            do{
                fh = new FileHandle("screenshot" + counter++ + ".png");
            }while (fh.exists());
            Pixmap pixmap = getScreenshot(x, y, x + width, y + height, false);
            PixmapIO.writePNG(fh, pixmap);
            pixmap.dispose();
        } catch (Exception ignored){
        }
    }

    private static Pixmap getScreenshot(int x, int y, int w, int h, boolean yDown){
        final Pixmap pixmap = ScreenUtils.getFrameBufferPixmap(x, y, w, h);

        if (yDown) {
            // Flip the pixmap upside down
            ByteBuffer pixels = pixmap.getPixels();
            int numBytes = w * h * 4;
            byte[] lines = new byte[numBytes];
            int numBytesPerLine = w * 4;
            for (int i = 0; i < h; i++) {
                pixels.position((h - i - 1) * numBytesPerLine);
                pixels.get(lines, i * numBytesPerLine, numBytesPerLine);
            }
            pixels.clear();
            pixels.put(lines);
            pixels.clear();
        }

        return pixmap;
    }
}
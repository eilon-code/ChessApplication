package com.mygdx.jar.imageHandlersObjects;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.ScreenUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotFactory {
    public static int counter = 0;

    public static void saveScreenshot(File mediaStorageDir, int x, int y, int width, int height){
        try{
            FileHandle fh;
            do{
                counter++;
                fh = new FileHandle(mediaStorageDir + File.separator + "screenshot" + counter + ".png");
            }while (fh.exists());
            Pixmap pixmap = ScreenUtils.getFrameBufferPixmap(x, y, width, height);
            PixmapIO.writePNG(fh, pixmap, 0, true);
            pixmap.dispose();

            System.out.println("Saved Image Path: " + fh.path());
        } catch (Exception ignored){
        }
    }

    /** Create a File for saving an image or video */
    public static File getOutputMediaFile(File mediaStorageDir){
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                System.out.println("Return NULL");
                return null;
            }
        }
        // Create a media file name
        // String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        String mImageName= "screenshot" + counter + ".png";// "MI_"+ timeStamp +".png";
        System.out.println("Image name = " + mImageName);
        return new File(mediaStorageDir.getPath() + File.separator + mImageName);
    }

//    private void storeImage(File mediaStorageDir) {
//        File pictureFile = getOutputMediaFile(mediaStorageDir);
//        if (pictureFile == null) {
//            System.out.println("Error creating media file, check storage permissions: ");
//            return;
//        }
//        try {
//            FileOutputStream fos = new FileOutputStream(pictureFile);
//            pictureFile.compress(pixmap., 90, fos);
//            fos.close();
//        } catch (FileNotFoundException e) {
//            Log.d(TAG, "File not found: " + e.getMessage());
//        } catch (IOException e) {
//            Log.d(TAG, "Error accessing file: " + e.getMessage());
//        }
//    }
}
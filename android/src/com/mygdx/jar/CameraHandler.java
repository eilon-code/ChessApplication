package com.mygdx.jar;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class CameraHandler {
    Bitmap myBitmap;
    Intent data;
    Uri picUri;

    public CameraHandler(PackageManager packageManager){
        setIntent(packageManager, null);
    }

    public Bitmap getCapturedImage(Intent data, ContentResolver resolver, File getImage){
        updateBitmap(data, resolver, getImage);
        return myBitmap;
    }

    public void setIntent(PackageManager packageManager, File getImage){
        data = getPickImageChooserIntent(packageManager, getImage);
    }

    public void updateBitmap(Intent data_, ContentResolver resolver, File getImage){
        Bitmap bitmap;
        if (getPickImageResultUri(data_, getImage) != null) {
            picUri = getPickImageResultUri(data_, getImage);

            try {
                myBitmap = MediaStore.Images.Media.getBitmap(resolver, picUri);
                myBitmap = rotateImage(myBitmap, 0);
                myBitmap = getResizedBitmap(myBitmap, 500);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            bitmap = (Bitmap) data.getExtras().get("data");
            myBitmap = bitmap;
        }
    }

    /**
     * Create a chooser intent to select the source to get image from.<br />
     * The source can be camera's (ACTION_IMAGE_CAPTURE) or gallery's (ACTION_GET_CONTENT).<br />
     * All possible sources are added to the intent chooser.
     */
    public Intent getPickImageChooserIntent(PackageManager packageManager, File getImage) {
        // Determine Uri of camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri(getImage);

        List allIntents = new ArrayList();

        // collect all camera intents
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (Object res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(((ResolveInfo)res).activityInfo.packageName, ((ResolveInfo)res).activityInfo.name));
            intent.setPackage(((ResolveInfo)res).activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        // collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (Object res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(((ResolveInfo)res).activityInfo.packageName, ((ResolveInfo)res).activityInfo.name));
            intent.setPackage(((ResolveInfo)res).activityInfo.packageName);
            allIntents.add(intent);
        }

        // the main intent is the last in the list (fucking android) so pickup the useless one
        Intent mainIntent = (Intent) allIntents.get(allIntents.size() - 1);
        for (Object intent : allIntents) {
            if (Objects.requireNonNull(((Intent) intent).getComponent()).getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = (Intent) intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    /**
     * Get URI to image received from capture by camera.
     */
    private Uri getCaptureImageOutputUri(File getImage) {
        Uri outputFileUri = null;
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
        }
        return outputFileUri;
    }

    /**
     * Get the URI of the selected image from .<br />
     * Will return the correct URI for camera and gallery image.
     *
     * @param data the returned data of the activity result
     */
    public Uri getPickImageResultUri(Intent data, File getImage) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        System.out.println("Is Camera: " + isCamera);

        return isCamera ? getCaptureImageOutputUri(getImage) : data.getData();
    }

    private String storeImage(Activity activity, Bitmap image) {
        File pictureFile = getOutputMediaFile(activity);
        if (pictureFile == null) {
            return null;
        }
        try {
            if (image != null){
                FileOutputStream fos = new FileOutputStream(pictureFile);
                image.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
            }
            else{
                System.out.println("Bitmap Image Is NULL");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error Saving Picture");
        }
        return pictureFile.getPath();
    }

    private  File getOutputMediaFile(Activity activity) {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + activity.getApplicationContext().getPackageName()
                + "/CameraStream");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                System.out.println("System return NULL");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        String mImageName = "MI_" + timeStamp + ".png";
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);

        return mediaFile;
    }
}
package com.mygdx.jar;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageView;

import androidx.core.content.FileProvider;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.mygdx.jar.graphicsObjects.ScrollingGame;
import com.mygdx.jar.imageHandlersObjects.ScreenshotFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class AndroidLauncher extends AndroidApplication implements CameraLauncher {
	private static final String cameraID = "0";
	private PermissionManager permissionManager;
	private String[] permissions =
			{Manifest.permission.CAMERA,
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.READ_EXTERNAL_STORAGE};

	private static Bitmap capturedImage;
	private Texture cameraFootage;

	private CameraHandlerThread mThread = null;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new ScrollingGame(this), config);

		requestAppPermissions();
	}

	@Override
	public Texture getCapturedImage(){
		// captureImage();
		return cameraFootage;
	}

	private void syncFootage(){
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				if (capturedImage != null && capturedImage.getByteCount() != 0){
					Texture tex = new Texture(
							capturedImage.getWidth(),
							capturedImage.getHeight(),
							Pixmap.Format.RGBA8888);
					GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tex.getTextureObjectHandle());
					GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, capturedImage, 0);
					GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
					capturedImage.recycle();
					// now you have the texture to do whatever you want
					cameraFootage = tex;
				}
			}
		});
	}

	@Override
	public void captureImage() {
//		if (isPermissionEnabled){
//			// openCamera();
//			captureImageUsingCamera();
//			syncFootage();
//		}
//		else {
//			System.out.println("No Permission");
//		}
	}

	@Override
	public void openCamera() {
//		if (mThread == null) {
//			mThread = new CameraHandlerThread(this);
//		}
//		mThread.openCamera();

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, 100);
	}

	@Override
	public void openGallery() {
		Intent intent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, 101);
	}

	@Override
	public void share(String text) {
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);

		File storedImage = getStoredImage();
		System.out.println("Loaded Image Path: " + storedImage.getPath());

		Uri imageUri = FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()),
				BuildConfig.APPLICATION_ID + ".provider", storedImage);
		sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		sendIntent.putExtra(Intent.EXTRA_TEXT, text);
		sendIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
		sendIntent.setType("image/png");

		Intent shareIntent = Intent.createChooser(sendIntent, null);
		Intent chooser = Intent.createChooser(shareIntent, "Share File");
		List<ResolveInfo> resInfoList = this.getPackageManager().queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);
		for (ResolveInfo resolveInfo : resInfoList) {
			String packageName = resolveInfo.activityInfo.packageName;
			this.grantUriPermission(packageName, imageUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
		}

		startActivity(chooser);
		// startActivity(shareIntent);
	}

	@Override
	public File getImagesDir(){
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.
		return new File(Environment.getExternalStorageDirectory()
				+ "/Android/data/"
				+ getApplicationContext().getPackageName()
				+ "/Files");
	}

	@Override
	public File getStoredImage(){
		return ScreenshotFactory.getOutputMediaFile(getImagesDir());
	}

	@Override
	public void closeCamera() {
//		if (mThread != null){
//			mThread.stopRun();
//		}
//		mThread = null;
//		System.out.println("Camera Closed");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK){
			if (requestCode == 100){
				capturedImage = (Bitmap) data.getExtras().get("data");
				syncFootage();
			}
			else if (requestCode == 101){
				Uri targetUri = data.getData();
				try {
					capturedImage = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
					syncFootage();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private static void captureImageUsingCamera(){
		if (cameraID.equals("0")) {
			CaptureBackPhoto();
		} else {
			CaptureFrontPhoto();
		}
		System.out.println("Image taken");
	}

	private void requestAppPermissions() {
		System.out.println("Asking permission");
		Dexter.withActivity(this)
				.withPermissions(
						Manifest.permission.CAMERA)
				.withListener(new MultiplePermissionsListener() {
					@Override
					public void onPermissionsChecked(MultiplePermissionsReport report) {
						if (report.areAllPermissionsGranted()) {
							System.out.println("Permission granted");
							captureImageUsingCamera();
						}
						// check for permanent denial of any permission
						if (report.isAnyPermissionPermanentlyDenied()) {
							// permission is denied permanently, navigate user to app settings
							showSettingsDialog();
						}
					}
					@Override
					public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
						token.continuePermissionRequest();
					}
				})
				.onSameThread()
				.check();
	}

	private void showSettingsDialog() {
		System.out.println("No Permission");
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Need Permissions");
		builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
		builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				openSettings();
			}
		});

		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		builder.show();
	}

	private void openSettings() {
		Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		Uri uri = Uri.fromParts("package", getPackageName(), null);
		intent.setData(uri);
		startActivityForResult(intent, 101);
	}

	private static void CaptureFrontPhoto() {
		System.out.println("Preparing to take photo");
		Camera camera = null;
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		int frontCamera = 1;
		Camera.getCameraInfo(frontCamera, cameraInfo);
		try {
			camera = Camera.open(frontCamera);
			camera.enableShutterSound(false);
		} catch (RuntimeException e) {
			Log.d(TAG, "Camera not available: " + 1);
			camera = null;
		}
		try {
			if (null == camera) {
				Log.d(TAG, "Could not get camera instance");
			} else {
				Log.d(TAG, "Got the camera, creating the dummy surface texture");
				try {
					camera.setPreviewTexture(new SurfaceTexture(0));
					camera.startPreview();
				} catch (Exception e) {
					Log.d(TAG, "Could not set the surface preview texture");
					e.printStackTrace();
				}
				camera.takePicture(null, null, new Camera.PictureCallback() {
					@Override
					public void onPictureTaken(byte[] data, Camera camera) {
						try {
							capturedImage = BitmapFactory.decodeByteArray(data, 0, data.length);
						} catch (Exception e) {
							System.out.println(e.getMessage());
						}
						camera.release();
					}
				});
			}
		} catch (Exception e) {
			if (camera != null){
				camera.release();
			}
		}
	}

	private static void CaptureBackPhoto() {
		Log.d(TAG, "Preparing to take photo");
		Camera camera = null;
		try {
			camera = Camera.open();
			camera.enableShutterSound(false);
		} catch (RuntimeException e) {
			Log.d(TAG, "Camera not available: " + 1);
			camera = null;
			//e.printStackTrace();
		}
		try {
			if (null == camera) {
				Log.d(TAG, "Could not get camera instance");
			} else {
				Log.d(TAG, "Got the camera, creating the dummy surface texture");
				try {
					camera.setPreviewTexture(new SurfaceTexture(0));
					camera.startPreview();
				} catch (Exception e) {
					Log.d(TAG, "Could not set the surface preview texture");
					e.printStackTrace();
				}
				camera.takePicture(null, null, new Camera.PictureCallback() {
					@Override
					public void onPictureTaken(byte[] data, Camera camera) {
						try {
							capturedImage = BitmapFactory.decodeByteArray(data, 0, data.length);
						} catch (Exception e) {
							System.out.println(e.getMessage());
						}
						camera.release();
					}
				});
			}
		} catch (Exception e) {
			if (camera != null){
				camera.release();
			}
		}
	}
}

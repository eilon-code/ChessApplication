package com.mygdx.jar;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.icu.text.SimpleDateFormat;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.net.Uri;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.LifecycleOwner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.jar.graphicsObjects.ScrollingGame;
import com.mygdx.jar.imageHandlersObjects.ScreenshotFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class AndroidLauncher extends AndroidApplication implements CameraLauncher {
	private final String[] permissions = {
			Manifest.permission.CAMERA,
			Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.WRITE_EXTERNAL_STORAGE};
	private final int PERMISSION_REQUEST_CODE = 101;
	private PermissionManager permissionManager;

	private static Bitmap capturedImage;
	private Texture cameraFootage;
	private ImageCapture imgCap;

	private TextureView mTextureView;
	private String mImageFileName;
	private File mImageFolder;
	private int mTotalRotation;
	private Size mPreviewSize;
	private Size mVideoSize;
	private Size mImageSize;
	private ImageReader mImageReader;
	private CameraDevice mCameraDevice;
	private CameraDevice.StateCallback mCameraDeviceStateCallback;
	private CaptureRequest.Builder mCaptureRequestBuilder;
	private String mCameraId;
	private CameraCaptureSession mPreviewCaptureSession;
	private HandlerThread mBackgroundHandlerThread;
	private Handler mBackgroundHandler;
	private TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
		@Override
		public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
			setupCamera(width, height);
		}

		@Override
		public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

		}

		@Override
		public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
			return false;
		}

		@Override
		public void onSurfaceTextureUpdated(SurfaceTexture surface) {

		}
	};
	private class ImageSaver implements Runnable {
		private final Image mImage;

		public ImageSaver(Image image) {
			mImage = image;
		}

		@Override
		public void run() {
			ByteBuffer byteBuffer = mImage.getPlanes()[0].getBuffer();
			byte[] bytes = new byte[byteBuffer.remaining()];
			byteBuffer.get(bytes);

			FileOutputStream fileOutputStream = null;
			try {
				fileOutputStream = new FileOutputStream(mImageFileName);
				fileOutputStream.write(bytes);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				mImage.close();

				Intent mediaStoreUpdateIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
				mediaStoreUpdateIntent.setData(Uri.fromFile(new File(mImageFileName)));
				sendBroadcast(mediaStoreUpdateIntent);

				if(fileOutputStream != null) {
					try {
						fileOutputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		}
	}
	private static class CompareSizeByArea implements Comparator<Size> {
		@Override
		public int compare(Size lhs, Size rhs) {
			return Long.signum( (long)(lhs.getWidth() * lhs.getHeight()) -
					(long)(rhs.getWidth() * rhs.getHeight()));
		}
	}
	private final ImageReader.OnImageAvailableListener mOnImageAvailableListener = new
			ImageReader.OnImageAvailableListener() {
				@Override
				public void onImageAvailable(ImageReader reader) {
					mBackgroundHandler.post(new ImageSaver(reader.acquireLatestImage()));
				}
			};
	private static SparseIntArray ORIENTATIONS = new SparseIntArray();
	static {
		ORIENTATIONS.append(Surface.ROTATION_0, 0);
		ORIENTATIONS.append(Surface.ROTATION_90, 90);
		ORIENTATIONS.append(Surface.ROTATION_180, 180);
		ORIENTATIONS.append(Surface.ROTATION_270, 270);
	}

	private CameraHandlerThread mThread = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new ScrollingGame(this), config);
		permissionManager = PermissionManager.getInstance(this);

		setContentView(R.layout.activity_main);
		mTextureView = (TextureView) findViewById(R.id.textureView);
		System.out.println("TextureView is null? " + (mTextureView==null));
		mCameraDeviceStateCallback = new CameraDevice.StateCallback() {
			@Override
			public void onOpened(CameraDevice camera) {
				mCameraDevice = camera;
//				mMediaRecorder = new MediaRecorder();
//				if (mIsRecording) {
//					try {
//						createVideoFileName();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
////					startRecord();
////					mMediaRecorder.start();
////					runOnUiThread(new Runnable() {
////						@Override
////						public void run() {
////							mChronometer.setBase(SystemClock.elapsedRealtime());
////							mChronometer.setVisibility(View.VISIBLE);
////							mChronometer.start();
////						}
////					});
//				} else {
				startPreview();
//				}
				// Toast.makeText(getApplicationContext(),
				//         "Camera connection made!", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onDisconnected(@NonNull CameraDevice cameraDevice) {
				closeCamera();
			}

			@Override
			public void onError(@NonNull CameraDevice cameraDevice, int i) {
				closeCamera();
			}
		};
		askAllPermissions();
	}

	@Override
	public void askAllPermissions() {
		if (!isPermissionGranted()) {
			permissionManager.askPermissions(this, permissions, PERMISSION_REQUEST_CODE);
		} else {
			Toast.makeText(this, "Permission already granted!",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public boolean isPermissionGranted() {
		return permissionManager.checkPermissions(permissions);
	}

	@Override
	public Texture getCapturedImage() {
		// captureImage();
		return cameraFootage;
	}

	private void syncFootage() {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				// capturedImage
				Bitmap image = mTextureView.getBitmap();
				if (image != null && image.getByteCount() != 0) {
					Texture tex = new Texture(
							image.getWidth(),
							image.getHeight(),
							Pixmap.Format.RGBA8888);
					GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tex.getTextureObjectHandle());
					GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, image, 0);
					GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
					image.recycle();
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

//		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//		startActivityForResult(intent, 100);

		setupCamera(mTextureView.getWidth(), mTextureView.getHeight());
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
	public File getImagesDir() {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.
		return new File(Environment.getExternalStorageDirectory()
				+ "/Android/data/"
				+ getApplicationContext().getPackageName()
				+ "/Files");
	}

	@Override
	public File getStoredImage() {
		return ScreenshotFactory.getOutputMediaFile(getImagesDir());
	}

	@Override
	public void closeCamera() {
//		if (mThread != null){
//			mThread.stopRun();
//		}
//		mThread = null;
//		System.out.println("Camera Closed");
		if (mCameraDevice != null) {
			mCameraDevice.close();
			mCameraDevice = null;
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
										   @NonNull int[] grantResults) {
		if (requestCode == PERMISSION_REQUEST_CODE) {
			permissionManager.handlePermissionResult(this, requestCode, permissions, grantResults);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			if (requestCode == 100) {
				capturedImage = (Bitmap) data.getExtras().get("data");
				syncFootage();
			} else if (requestCode == 101) {
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

	@Override
	protected void onPause() {
		closeCamera();
		stopBackgroundThread();
		super.onPause();
	}

	private void setupCamera(int width, int height) {
		CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
		try {
			for (String cameraId : cameraManager.getCameraIdList()) {
				CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
				if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) ==
						CameraCharacteristics.LENS_FACING_FRONT) {
					continue;
				}
				StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
				int deviceOrientation = getWindowManager().getDefaultDisplay().getRotation();
				mTotalRotation = sensorToDeviceRotation(cameraCharacteristics, deviceOrientation);
				boolean swapRotation = mTotalRotation == 90 || mTotalRotation == 270;
				int rotatedWidth = width;
				int rotatedHeight = height;
				if (swapRotation) {
					rotatedWidth = height;
					rotatedHeight = width;
				}
				mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), rotatedWidth, rotatedHeight);
				mVideoSize = chooseOptimalSize(map.getOutputSizes(MediaRecorder.class), rotatedWidth, rotatedHeight);
				mImageSize = chooseOptimalSize(map.getOutputSizes(ImageFormat.JPEG), rotatedWidth, rotatedHeight);
				mImageReader = ImageReader.newInstance(mImageSize.getWidth(), mImageSize.getHeight(), ImageFormat.JPEG, 1);
				mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mBackgroundHandler);
				mCameraId = cameraId;
				connectCamera();
				return;
			}
		} catch (CameraAccessException e) {
			e.printStackTrace();
		}
	}

	private static int sensorToDeviceRotation(CameraCharacteristics cameraCharacteristics, int deviceOrientation) {
		int sensorOrientation = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
		deviceOrientation = ORIENTATIONS.get(deviceOrientation);
		return (sensorOrientation + deviceOrientation + 360) % 360;
	}

	private void connectCamera() {
		CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
		try {
			if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
				// TODO: Consider calling
				//    ActivityCompat#requestPermissions
				// here to request the missing permissions, and then overriding
				//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
				//                                          int[] grantResults)
				// to handle the case where the user grants the permission. See the documentation
				// for ActivityCompat#requestPermissions for more details.
				return;
			}
			cameraManager.openCamera(mCameraId, mCameraDeviceStateCallback, mBackgroundHandler);
		} catch (CameraAccessException e) {
			e.printStackTrace();
		}
	}

	private void startPreview() {
		SurfaceTexture surfaceTexture = mTextureView.getSurfaceTexture();
		surfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
		Surface previewSurface = new Surface(surfaceTexture);

		try {
			mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
			mCaptureRequestBuilder.addTarget(previewSurface);

			mCameraDevice.createCaptureSession(Arrays.asList(previewSurface, mImageReader.getSurface()),
					new CameraCaptureSession.StateCallback() {
						@Override
						public void onConfigured(CameraCaptureSession session) {
							Log.d(TAG, "onConfigured: startPreview");
							mPreviewCaptureSession = session;
							try {
								mPreviewCaptureSession.setRepeatingRequest(mCaptureRequestBuilder.build(),
										null, mBackgroundHandler);
							} catch (CameraAccessException e) {
								e.printStackTrace();
							}
						}

						@Override
						public void onConfigureFailed(CameraCaptureSession session) {
							Log.d(TAG, "onConfigureFailed: startPreview");

						}
					}, null);
		} catch (CameraAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mTextureView != null){
			startBackgroundThread();
			if(mTextureView.isAvailable()) {
				setupCamera(mTextureView.getWidth(), mTextureView.getHeight());
			} else {
				mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
			}
		}
	}

	private void startBackgroundThread() {
		mBackgroundHandlerThread = new HandlerThread("Camera2VideoImage");
		mBackgroundHandlerThread.start();
		mBackgroundHandler = new Handler(mBackgroundHandlerThread.getLooper());
	}

	private void stopBackgroundThread() {
		if (mBackgroundHandlerThread != null){
			mBackgroundHandlerThread.quitSafely();
			try {
				mBackgroundHandlerThread.join();
				mBackgroundHandlerThread = null;
				mBackgroundHandler = null;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private static Size chooseOptimalSize(Size[] choices, int width, int height) {
		List<Size> bigEnough = new ArrayList<Size>();
		for(Size option : choices) {
			if(option.getHeight() == option.getWidth() * height / width &&
					option.getWidth() >= width && option.getHeight() >= height) {
				bigEnough.add(option);
			}
		}
		if(bigEnough.size() > 0) {
			return Collections.min(bigEnough, new CompareSizeByArea());
		} else {
			return choices[0];
		}
	}

	private void createImageFolder() {
		File imageFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		mImageFolder = new File(imageFile, "camera2VideoImage");
		if(!mImageFolder.exists()) {
			mImageFolder.mkdirs();
		}
	}

	private File createImageFileName() throws IOException {
		String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String prepend = "IMAGE_" + timestamp + "_";
		File imageFile = File.createTempFile(prepend, ".jpg", mImageFolder);
		mImageFileName = imageFile.getAbsolutePath();
		return imageFile;
	}
//	private void lockFocus() {
//		mCaptureState = STATE_WAIT_LOCK;
//		mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_START);
//		try {
//			if(mIsRecording) {
//				mRecordCaptureSession.capture(mCaptureRequestBuilder.build(), mRecordCaptureCallback, mBackgroundHandler);
//			} else {
//				mPreviewCaptureSession.capture(mCaptureRequestBuilder.build(), mPreviewCaptureCallback, mBackgroundHandler);
//			}
//		} catch (CameraAccessException e) {
//			e.printStackTrace();
//		}
//	}

//	private static void captureImageUsingCamera(){
//		if (cameraID.equals("0")) {
//			CaptureBackPhoto();
//		} else {
//			CaptureFrontPhoto();
//		}
//		System.out.println("Image taken");
//	}
//
//	private static void CaptureFrontPhoto() {
//		System.out.println("Preparing to take photo");
//		Camera camera = null;
//		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
//		int frontCamera = 1;
//		Camera.getCameraInfo(frontCamera, cameraInfo);
//		try {
//			camera = Camera.open(frontCamera);
//			camera.enableShutterSound(false);
//		} catch (RuntimeException e) {
//			Log.d(TAG, "Camera not available: " + 1);
//			camera = null;
//		}
//		try {
//			if (null == camera) {
//				Log.d(TAG, "Could not get camera instance");
//			} else {
//				Log.d(TAG, "Got the camera, creating the dummy surface texture");
//				try {
//					camera.setPreviewTexture(new SurfaceTexture(0));
//					camera.startPreview();
//				} catch (Exception e) {
//					Log.d(TAG, "Could not set the surface preview texture");
//					e.printStackTrace();
//				}
//				camera.takePicture(null, null, new Camera.PictureCallback() {
//					@Override
//					public void onPictureTaken(byte[] data, Camera camera) {
//						try {
//							capturedImage = BitmapFactory.decodeByteArray(data, 0, data.length);
//						} catch (Exception e) {
//							System.out.println(e.getMessage());
//						}
//						camera.release();
//					}
//				});
//			}
//		} catch (Exception e) {
//			if (camera != null){
//				camera.release();
//			}
//		}
//	}
//
//	private static void CaptureBackPhoto() {
//		Log.d(TAG, "Preparing to take photo");
//		Camera camera = null;
//		try {
//			camera = Camera.open();
//			camera.enableShutterSound(false);
//		} catch (RuntimeException e) {
//			Log.d(TAG, "Camera not available: " + 1);
//			camera = null;
//			//e.printStackTrace();
//		}
//		try {
//			if (null == camera) {
//				Log.d(TAG, "Could not get camera instance");
//			} else {
//				Log.d(TAG, "Got the camera, creating the dummy surface texture");
//				try {
//					camera.setPreviewTexture(new SurfaceTexture(0));
//					camera.startPreview();
//				} catch (Exception e) {
//					Log.d(TAG, "Could not set the surface preview texture");
//					e.printStackTrace();
//				}
//				camera.takePicture(null, null, new Camera.PictureCallback() {
//					@Override
//					public void onPictureTaken(byte[] data, Camera camera) {
//						try {
//							capturedImage = BitmapFactory.decodeByteArray(data, 0, data.length);
//						} catch (Exception e) {
//							System.out.println(e.getMessage());
//						}
//						camera.release();
//					}
//				});
//			}
//		} catch (Exception e) {
//			if (camera != null){
//				camera.release();
//			}
//		}
//	}
}

package com.mygdx.jar;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Bundle;
import android.os.Environment;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mygdx.jar.gameObjects.BoardObjects.Board;
import com.mygdx.jar.graphicsObjects.ScrollingGame;
import com.mygdx.jar.imageHandlersObjects.ScreenshotFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

public class AndroidLauncher extends AndroidApplication implements CameraLauncher {
	private SQLiteDataBaseHandler SQLite;
	private final String[] permissions = {
			Manifest.permission.CAMERA,
			Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.WRITE_EXTERNAL_STORAGE};
	private final int PERMISSION_REQUEST_CODE = 101;
	private PermissionManager permissionManager;

	private TextureViewActor cameraTextureView;

	private Texture cameraTexture;

	private static Bitmap capturedImage;
	private Texture cameraFootage;
	private CameraHandler cameraHandler;
	private ScrollingGame game;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		game = new ScrollingGame(this);
		initialize(game, config);
		permissionManager = PermissionManager.getInstance(this);
		askAllPermissions();
		SQLite = new SQLiteDataBaseHandler(this);

		///////////////////////////////////////////////////////////////////
		// Problem starts here:
		setContentView(R.layout.activity_main);
 		TextureView textureView = findViewById(R.id.textureView);

//		TextureView textureView = new TextureView((Context) Gdx.app);
//		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(100, 100);
//		textureView.setLayoutParams(params);
		cameraTextureView = new TextureViewActor(textureView, getImagesDir());
		cameraTextureView.setPosition(100, 100);
		cameraTextureView.setSize(500, 500);
		cameraHandler = new CameraHandler(this, cameraTextureView.getTextureView());
		///////////////////////////////////////////////////////////////////

		cameraTexture = cameraFootage;
	}

	private void renderGame(){
		game.render();
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
//		return cameraFootage;
		String filePath = getImagesDir().getPath() + File.separator + "cameraTexture.png";

		Bitmap bitmap = cameraTextureView.getTextureView().getBitmap();
		if (bitmap != null) {
			System.out.println("Heyyy");
			try {
				FileOutputStream fos = null;
				fos = new FileOutputStream(filePath);
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
				fos.close();
				System.out.println("Success");
			} catch (IOException e) {
				e.printStackTrace();
			}
			cameraTexture = new Texture(filePath);
		}
		else{
//			System.out.println("Bitmap is null !!!!!!!");
		}
		return cameraTexture;
	}

	private void syncFootage() {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				System.out.println("Looking at image");
				if (capturedImage != null && capturedImage.getByteCount() != 0) {
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
				else{
					System.out.println("Image is FuCKing NULL !@@#!@$!$");
				}
			}
		});
	}

	@Override
	public void captureImage() {
//		capturedImage = cameraHandler.getBitmap();
//		syncFootage();
	}

	@Override
	public void openCamera() {
		cameraHandler.setupCamera(cameraHandler.getTextureViewWidth(), cameraHandler.getTextureViewHeight());
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
//		startActivity(shareIntent);
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
		cameraHandler.closeCamera();
	}

	@Override
	public void resetSQL(){
		SQLite.reset();
	}

	@Override
	public void addBoard(Board board, int boardNum){
		SQLite.addBoard(board, Integer.toString(boardNum));
	}

	@Override
	public void updateBoard(Board board, int boardNum){
		SQLite.updateBoard(board, Integer.toString(boardNum));
	}

	@Override
	public Stack<Board> getStackFromStorage(){
		return SQLite.readAllData();
	}

	@Override
	public void deleteBoard(int boardNum){
		SQLite.deleteBoard(Integer.toString(boardNum));
	}

	@Override
	public void deleteAll() {
		SQLite.deleteAllData();
	}

	@Override
	public void addCameraActor(Stage stage) {
		stage.addActor(cameraTextureView);
	}

	@Override
	public void addSpriteBatchToTextureView(SpriteBatch batch) {
		System.out.println("Drawing TextureViewActor");
		cameraTextureView.draw(batch, 0.5f);
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
		cameraHandler.stopBackgroundThread();
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		cameraHandler.start();
	}
}

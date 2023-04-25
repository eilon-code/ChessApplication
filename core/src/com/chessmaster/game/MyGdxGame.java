package com.chessmaster.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.chessmaster.game.components.CameraLauncher;
import com.chessmaster.game.components.graphicsObjects.GameScreen;

public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	private final CameraLauncher launcher;
	private GameScreen gameScreen;

	public MyGdxGame() {
		this.launcher = null;
	}

	public MyGdxGame(CameraLauncher launcher) {
		this.launcher = launcher;
	}

	@Override
	public void create () {
		this.batch = new SpriteBatch();
		this.gameScreen = new GameScreen(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), this.batch, this.launcher);
	}

	@Override
	public void render () {
		ScreenUtils.clear(1, 0, 0, 1);
		batch.begin();
		this.gameScreen.render(Gdx.graphics.getDeltaTime());
		batch.end();
	}

	@Override
	public void dispose () {
		batch.dispose();
	}

	@Override
	public void resize(int width, int height) {
		this.gameScreen.resize(width, height);
	}
}

package com.mygdx.jar.graphicsObjects;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.mygdx.jar.CameraLauncher;

public class ScrollingGame extends Game {
	GameScreen gameScreen;
	CameraLauncher launcher;

	public ScrollingGame(CameraLauncher launcher){
		super();
		this.launcher = launcher;
	}

	@Override
	public void create() {
		gameScreen = new GameScreen(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), launcher);
		setScreen(gameScreen);
	}

	@Override
	public void dispose() {
		gameScreen.dispose();
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void resize(int width, int height) {
		gameScreen.resize(width, height);
	}
}

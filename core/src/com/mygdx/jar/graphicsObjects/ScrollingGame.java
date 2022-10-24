package com.mygdx.jar.graphicsObjects;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

public class ScrollingGame extends Game {
	GameScreen gameScreen;

	@Override
	public void create() {
		gameScreen = new GameScreen(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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

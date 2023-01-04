package com.mygdx.jar.graphicsObjects;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mygdx.jar.CameraLauncher;

public class ScrollingGame extends Game {
	private final CameraLauncher launcher;
	private GameScreen gameScreen;
	private Stage stage;

	public ScrollingGame(CameraLauncher launcher){
		super();
		this.launcher = launcher;
	}

	@Override
	public void create() {
		gameScreen = new GameScreen(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), launcher);
		setScreen(gameScreen);
		stage = new Stage();
		launcher.addCameraActor(stage);
	}

	@Override
	public void dispose() {
		gameScreen.dispose();
	}

	@Override
	public void render() {
		super.render();

		// Call the render method of the game's screen
		getScreen().render(Gdx.graphics.getDeltaTime());

		// Update the stage and its actors
		stage.act();

		// Clear the screen
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Draw the stage and its actors
		stage.draw();
		System.out.println("Game render is called");
	}

	@Override
	public void resize(int width, int height) {
		gameScreen.resize(width, height);
	}
}

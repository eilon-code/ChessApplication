package com.mygdx.jar.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.jar.graphicsObjects.ScrollingGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 350;
		config.height = 700;
		new LwjglApplication(new ScrollingGame(null), config);
	}
}

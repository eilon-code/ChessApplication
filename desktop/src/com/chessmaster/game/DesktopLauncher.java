package com.chessmaster.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowedMode(350, 700);
		config.setResizable(false);
		config.setForegroundFPS(60);
		config.setTitle("ChessMaster");
		new Lwjgl3Application(new MyGdxGame(), config);
	}
}

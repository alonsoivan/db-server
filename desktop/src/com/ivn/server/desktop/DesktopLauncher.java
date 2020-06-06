package com.ivn.server.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.ivn.server.ServerAplication;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.height = 750;
		config.width = 750;
		config.foregroundFPS = 30;
		config.backgroundFPS = 30;

		new LwjglApplication(new ServerAplication(), config);
	}
}

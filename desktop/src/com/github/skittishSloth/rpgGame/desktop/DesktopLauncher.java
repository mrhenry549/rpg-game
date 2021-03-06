package com.github.skittishSloth.rpgGame.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.github.skittishSloth.rpgGame.RPGGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
                config.vSyncEnabled = false;
                config.foregroundFPS = 0;
                config.backgroundFPS = 0;
		new LwjglApplication(new RPGGame(), config);
	}
}

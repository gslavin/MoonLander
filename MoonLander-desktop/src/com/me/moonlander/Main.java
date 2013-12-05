package com.me.moonlander;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "MoonLander";
		cfg.useGL20 = false;
		cfg.width = 850;
		cfg.height = 480;
		
		new LwjglApplication(new MoonLander(), cfg);
	}
}

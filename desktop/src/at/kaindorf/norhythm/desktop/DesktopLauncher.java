package at.kaindorf.norhythm.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import at.kaindorf.norhythm.RhythmGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.fullscreen = true;
		config.title = "NoRhythm";
		config.width = 1920;
		config.height = 800;

		new LwjglApplication(new RhythmGame(), config);
	}
}

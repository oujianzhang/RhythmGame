package at.kaindorf.myRhythmGame;


import com.badlogic.gdx.Game;

public class RhythmGame extends Game {
	/**
	 * Initialization method for Animations and Texture for RhythmGame.
	 */
	@Override
	public void create() {
		setScreen(new GameScreen());
	}
}

package at.kaindorf.norhythm;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.Array;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class RhythmGame extends ApplicationAdapter {

	private Preferences prefs;
	private SpriteBatch batch;
	private TextureAtlas textureAtlas;
	private float elapsedTime = 0f;
	private Texture background;
	private float background_width;
	private float xCoordBg1, xCoordBg2;
	private final int BACKGROUND_MOVING_SPEED = 800;
	private Map<String, Animation<TextureRegion>> animations_map = new HashMap<>();
	private static final String[] animation_names = {"run", "melee", "jump", "dead", "idle", "missile", "hitarea"};


	/**
	 * Initialization method for Animations and Texture for RhythmGame.
	 */
	@Override
	public void create() {
		prefs = Gdx.app.getPreferences("rhythm_game");
		prefs.putInteger("background_moving_speed", 800);
		prefs.putFloat("player_run_position_x", 0.10f);
		prefs.putFloat("player_run_position_y", 0.16f);
		prefs.putFloat("player_jump_position_y", 0.48f);
		prefs.putFloat("player_melee_position_x", 0.15f);
		prefs.putString("key_up", "x");
		prefs.putString("key_down", "c");

		batch = new SpriteBatch();
		textureAtlas = new TextureAtlas(Gdx.files.internal("spritessheet.atlas"));
		Array<TextureAtlas.AtlasRegion> sprites = textureAtlas.getRegions();

		// Set Background
		background = new Texture(Gdx.files.internal("bg_snowytrees 1.png"));
		background_width = 2399;
		xCoordBg1 = background_width*(-1); xCoordBg2 = 0;

		// Fill Animations Hash Map with all animations available in TextureAtlas
		for (int i = 0; i < animation_names.length - 1; i++) {
			animations_map.put(
					animation_names[i],
					new Animation<TextureRegion>(0.115f, getSprites(sprites, animation_names[i]))
			);
		}
	}

	public Array<TextureAtlas.AtlasRegion> getSprites(Array<TextureAtlas.AtlasRegion> atlas, String sprite_name) {
		Array<TextureAtlas.AtlasRegion> sprites = new Array<>();
		
		for (int i = 0; i < atlas.size-1; i++) {
			String name = atlas.get(i).name.toLowerCase();
			if(name.contains(sprite_name.toLowerCase())) {
				sprites.add(atlas.get(i));
			}
		}
		return sprites;
	}

	@Override
	public void render() {
		elapsedTime += Gdx.graphics.getDeltaTime();
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // repaint memory buffer with specified color for optimization

		batch.begin();
		xCoordBg1 += BACKGROUND_MOVING_SPEED * Gdx.graphics.getDeltaTime();
		xCoordBg2 = xCoordBg1 + background_width;
		if (xCoordBg1 >= 0) {
			xCoordBg1 = background_width*(-1); xCoordBg2 = 0;
		}
		batch.draw(background, -xCoordBg1, 0);
		batch.draw(background, -xCoordBg2, 0);

		if(Gdx.input.isKeyPressed(Input.Keys.X)) {

			batch.draw(animations_map.get("melee").getKeyFrame(elapsedTime, true),
					prefs.getFloat("player_melee_position_x") * Gdx.graphics.getWidth(),
					prefs.getFloat("player_jump_position_y") * Gdx.graphics.getHeight()
			);
		} else if(Gdx.input.isKeyPressed(Input.Keys.C)) {
			batch.draw(animations_map.get("melee").getKeyFrame(elapsedTime, true),
					prefs.getFloat("player_melee_position_x") * Gdx.graphics.getWidth(),
					prefs.getFloat("player_run_position_y") * Gdx.graphics.getHeight()
			);
		} else {
			batch.draw(
					animations_map.get("run").getKeyFrame(elapsedTime, true),
					prefs.getFloat("player_run_position_x") * Gdx.graphics.getWidth(),
					prefs.getFloat("player_run_position_y") * Gdx.graphics.getHeight()
			);
		}


		batch.end();
	}

	@Override
	public void dispose() {
		batch.dispose();
		textureAtlas.dispose();
	}
}

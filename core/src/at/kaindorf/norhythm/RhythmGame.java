package at.kaindorf.norhythm;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.Array;

import java.util.Locale;


public class RhythmGame extends ApplicationAdapter {
	RhythmGame game;

	private SpriteBatch batch;
	private TextureAtlas textureAtlas;
	private Animation<TextureRegion> animation;
	private float elapsedTime = 0f;
	private final char ASSIGNED_KEY_UP = 'x';
	private final char ASSIGNED_KEY_DOWN = 'c';

	public void Game(RhythmGame game) {

	}

	@Override
	public void create() {
		batch = new SpriteBatch();
		textureAtlas = new TextureAtlas(Gdx.files.internal("spritessheet.atlas"));
		Array<TextureAtlas.AtlasRegion> adventure_girl_sprites = textureAtlas.getRegions();


		
		animation = new Animation<TextureRegion>(0.12f, getSprites(adventure_girl_sprites, "jump"));
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
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(animation.getKeyFrame(elapsedTime, true), 100, 100);
		batch.end();
	}

	@Override
	public void dispose() {
		batch.dispose();
		textureAtlas.dispose();
	}
}

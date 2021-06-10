package at.kaindorf.norhythm;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.Array;
import sun.jvm.hotspot.HelloWorld;

import java.util.Locale;


public class RhythmGame extends ApplicationAdapter {

	private SpriteBatch batch;
	private TextureAtlas textureAtlas;
	private Animation<TextureRegion> animation_girl;
	private Animation<TextureRegion> animation_missile;
	private float elapsedTime = 0f;
	private OrthographicCamera camera = new OrthographicCamera();
	private Texture background;
	private float background_width;
	private float xCoordBg1, xCoordBg2;
	private final int BACKGROUND_MOVING_SPEED = 800;
	private float playerOriginPositionX = 0.10f;
	private float playerOriginPositionY = 0.16f;

	@Override
	public void create() {
		batch = new SpriteBatch();
		textureAtlas = new TextureAtlas(Gdx.files.internal("spritessheet.atlas"));
		Array<TextureAtlas.AtlasRegion> sprites = textureAtlas.getRegions();

		setBackgroundTexture();

		animation_girl = new Animation<TextureRegion>(0.115f, getSprites(sprites, "run"));
		animation_missile = new Animation<TextureRegion>(0.115f, getSprites(sprites,"missiles"));
	}



	public void setBackgroundTexture() {
		background = new Texture(Gdx.files.internal("bg_snowytrees 1.png"));
		background_width = 2399;
		xCoordBg1 = background_width*(-1); xCoordBg2 = 0;
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

		batch.draw(
				animation_girl.getKeyFrame(elapsedTime, true),
				playerOriginPositionX * Gdx.graphics.getWidth(),
				playerOriginPositionY * Gdx.graphics.getHeight()
		);

//		for (int i = 0; i < testMissileRate; i++) {
//			batch.draw(animation_missile.getKeyFrame(elapsedTime, true), 0, 0);
//		}
		batch.end();
	}

	@Override
	public void dispose() {
		batch.dispose();
		textureAtlas.dispose();
	}
}

package at.kaindorf.myRhythmGame;

import Scenes.RhythmHud;
import at.kaindorf.util.WorldContactListener;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


import java.util.HashMap;
import java.util.Map;

public class GameScreen implements Screen {

    public static final float PPM = 100f; // pixels per meter for scaling since Box2d uses Meters as their unit of measurement

    private Preferences prefs;
    private SpriteBatch batch = new SpriteBatch();
    private TextureAtlas textureAtlas;
    private OrthographicCamera cam;
    private Viewport viewport;
    private RhythmHud hud;
    private float elapsedTime;

    private Texture background;

    private float background_width;
    private float xCoordBg1, xCoordBg2;
    private Map<String, Animation<TextureRegion>> animations_map = new HashMap<>();
    private static final String[] animation_names = {"run", "melee", "jump", "dead", "idle", "missile", "hitarea"};
    private boolean isMeleeFinished = true;


    private World rhythmWorld;
    private Body b2GroundBody;
    private Body b2PlayerBody;
    private Body b2CeilingBody;
    private Body b2TopMissile;
    private Body b2BottomMissile;


    private Box2DDebugRenderer b2dr;

    /**
     * Initialize Game World when switching to GameScreen
     */
    public GameScreen() {
        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();

        cam = new OrthographicCamera();
        viewport = new FitViewport(w / PPM, h / PPM, cam);
        hud = new RhythmHud(batch);
        cam.position.set(viewport.getWorldWidth() / 1.5f , viewport.getWorldHeight() / 1.5f, 0);
        prefs = Gdx.app.getPreferences("rhythm_game");
        prefs.putInteger("background_moving_speed", 800);


        prefs.putFloat("running_player_xCoords", 0.10f);
        prefs.putFloat("running_player_yCoords", 0.16f);
        prefs.putFloat("jumping_player_xCoords", 0.65f);
        prefs.putFloat("jumping_player_yCoords", 0.15f);

        prefs.putFloat("hitarea_top_xCoords", 0.15f);
        prefs.putFloat("hitarea_top_yCoords", 0.63f);
        prefs.putFloat("hitarea_bottom_xCoords", 0.15f);
        prefs.putFloat("hitarea_bottom_yCoords", 0.23f);

        prefs.putFloat("missile_top_yCoords", 0.62f);
        prefs.putFloat("missile_bottom_yCoords", 0.32f);
        prefs.putFloat("missile_speed", 5f);

        prefs.putString("key_up", "x");
        prefs.putString("key_down", "c");

        rhythmWorld = new World(new Vector2(0.0f, -9.81f), false);
        b2GroundBody = createBox(0, 0, w, h / 5f, false, false);
        b2PlayerBody = createBox(
                w * prefs.getFloat("running_player_xCoords"),
                h / 5f + 200,
                70,
                120,
                true,
                false
        );
        b2CeilingBody = createBox(0, h, w, h / 8f, false, false);
//        b2TopMissile = createBox(w, h*prefs.getFloat("missile_top_yCoords"),80,70,false, true);
//        b2BottomMissile = createBox(w, h*prefs.getFloat("missile_bottom_yCoords"),80, 70,false, true);

        b2dr = new Box2DDebugRenderer();

    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        textureAtlas = new TextureAtlas(Gdx.files.internal("spritessheet.atlas"));
        Array<TextureAtlas.AtlasRegion> sprites = textureAtlas.getRegions();

        // Set Background
        background = new Texture(Gdx.files.internal("bg_snowytrees 1.png"));
        background_width = 2399;
        xCoordBg1 = background_width * (-1);
        xCoordBg2 = 0;

        // Fill Animations Hash Map with all animations available in TextureAtlas
        for (int i = 0; i < animation_names.length; i++) {
            animations_map.put(
                    animation_names[i],
                    new Animation<TextureRegion>(0.09f, getSprites(sprites, animation_names[i]))
            );
        }
    }

    public Array<TextureAtlas.AtlasRegion> getSprites(Array<TextureAtlas.AtlasRegion> atlas, String sprite_name) {
        Array<TextureAtlas.AtlasRegion> sprites = new Array<>();

        for (int i = 0; i < atlas.size - 1; i++) {
            String name = atlas.get(i).name.toLowerCase();
            if (name.contains(sprite_name.toLowerCase())) {
                sprites.add(atlas.get(i));
            }
        }
        return sprites;
    }

    @Override
    public void render(float delta) {
//        batch.setProjectionMatrix(cam.combined);
        Gdx.gl.glClearColor(10f, 10f, 10f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // repaint memory buffer with specified color for optimization
        update(delta);
//        batch.setProjectionMatrix(hud.stage.getCamera().combined);
//        hud.stage.draw();
//        b2dr.render(rhythmWorld, cam.combined);
    }

    public void update(float delta) {
        rhythmWorld.step(1/60f, 6, 2) ;
        elapsedTime += delta;
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        batch.begin();

        updateBackground(delta);
        inputManager(delta);


        Vector2 velocity = b2PlayerBody.getLinearVelocity();
        Animation animation;
        if (Gdx.input.isKeyJustPressed(Input.Keys.E) || Gdx.input.isKeyJustPressed(Input.Keys.R) && !isMeleeFinished) {
            animation = new Animation(0.09f, animations_map.get("melee").getKeyFrame(elapsedTime, false));
        } else if((velocity.y > 3 || (velocity.y < 3 && !velocity.isZero()))){
            animation = new Animation(0.09f, animations_map.get("jump").getKeyFrame(elapsedTime, false));
        } else {
            animation = new Animation(0.09f, animations_map.get("run").getKeyFrame(elapsedTime, true));
        }

        // - drawnPlayer.getRegionWidth/Height / 2 so it draws it in the bottom left corner of the b2dShape
        TextureRegion player;
        player = (TextureRegion) animation.getKeyFrame(elapsedTime, false);

        batch.draw(
                player,
                b2PlayerBody.getPosition().x * PPM - player.getRegionWidth() / 2,
                b2PlayerBody.getPosition().y * PPM - player.getRegionHeight() / 2
        );

        TextureRegion hitcircle = animations_map.get("hitarea").getKeyFrame(elapsedTime, true);
        batch.draw(
                hitcircle,
                w * prefs.getFloat("hitarea_top_xCoords"),
                h * (prefs.getFloat("hitarea_top_yCoords"))
        );
        batch.draw(
                hitcircle,
                w * prefs.getFloat("hitarea_bottom_xCoords"),
                h* (prefs.getFloat("hitarea_bottom_yCoords"))
        );

        spawnMissile(true);
        batch.end();
    }

    float songspeed = 60f;
    float missilePosX;

    public void spawnMissile(boolean isTop) {
        TextureRegion missile = animations_map.get("missile").getKeyFrame(elapsedTime, true);
        missilePosX = Gdx.graphics.getWidth() - (prefs.getFloat("missile_speed") * elapsedTime * songspeed);
        if(isTop) {
            batch.draw(
                    missile,
                    missilePosX,
                    Gdx.graphics.getHeight() / 1.5f
            );
        } else {
            batch.draw(
                    missile,
                    missilePosX,
                    Gdx.graphics.getHeight() / 3f
            );
        }
    }


    public void updateBackground(float delta) {

        xCoordBg1 += prefs.getInteger("background_moving_speed") * delta;
        xCoordBg2 = xCoordBg1 + background_width;

        if (xCoordBg1 >= 0) {
            xCoordBg1 = background_width * (-1);
            xCoordBg2 = 0;
        }
        batch.draw(background, -xCoordBg1, 0);
        batch.draw(background, -xCoordBg2, 0);
    }

    public void inputManager(float delta) {

        if(Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            b2PlayerBody.setLinearVelocity(0f, -13);
            float xPos = Gdx.graphics.getWidth() * prefs.getFloat("hitarea_top_xCoords");
            if(missilePosX > xPos && missilePosX < xPos + 16) {

            }
            isMeleeFinished = false;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            b2PlayerBody.setLinearVelocity(0f, 15);
        }
    }

    @Override
    public void resize(int width, int height) {
        cam.setToOrtho(false, Gdx.graphics.getWidth() / PPM , Gdx.graphics.getHeight() / PPM);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    /**
     * Creates a geometric Square Body in our Game World allowing physics and collision detection to take place.
     *
     * @param xCoords   starting x-coordinates of the body
     * @param yCoords   starting y-coordinates of the body
     * @param bWidth   body width
     * @param bHeight  body height
     * @param isDynamic static: unmovable, no physical forces apply; dynamic: physical forces apply
     * @return Box2d Body
     */
    public Body createBox(float xCoords, float yCoords, float bWidth, float bHeight, boolean isDynamic, boolean isKinematic) {
        Body box_body;
        BodyDef box_def = new BodyDef();
        if (isDynamic) {
            box_def.type = BodyDef.BodyType.DynamicBody;
        } else if(isKinematic) {
            box_def.type = BodyDef.BodyType.KinematicBody;
        } else {
            box_def.type = BodyDef.BodyType.StaticBody;
        }

        box_def.position.set(xCoords / PPM, yCoords / PPM);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(bWidth / PPM, bHeight / PPM);

        box_body = rhythmWorld.createBody(box_def);
        box_body.createFixture(shape, 1.0f);
        box_body.setFixedRotation(true);
        shape.dispose();
        return box_body;
    }
    
    
    
//    public Body createCircle(float xCoords, float yCoords, float radius, boolean isStatic) {
//        Body circular_body;
//
//        BodyDef circle_def = new BodyDef();
//        if(isStatic) {
//            circle_def.type = BodyDef.BodyType.StaticBody;
//        } else {
//            circle_def.type = BodyDef.BodyType.KinematicBody;
//        }
//
//        CircleShape circleShape = new CircleShape();
//        FixtureDef fDef = new FixtureDef();
//        fDef.shape = circleShape;
//        circleShape.setRadius(radius / PPM);
//        circle_def.position.set(new Vector2(xCoords / PPM,  yCoords / PPM));
//
//        circular_body = rhythmWorld.createBody(circle_def);
//        circular_body.createFixture(fDef);
//        circular_body.setFixedRotation(true);
//
//        circleShape.dispose();
//        return circular_body;
//    }

    @Override
    public void dispose() {
        batch.dispose();
        textureAtlas.dispose();
        rhythmWorld.dispose();
        hud.dispose();
    }
}

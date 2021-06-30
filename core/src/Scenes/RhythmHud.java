package Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import static at.kaindorf.myRhythmGame.GameScreen.PPM;


public class RhythmHud implements Disposable{

    //Scene2D.ui Stage and its own Viewport for HUD
    public Stage stage;
    private Viewport viewport;

    // Game Score
    private static Integer combo;
    private static Integer score;

    //Scene2D widgets
    private static Label scoreLabel;
    private static Label comboLabel;

    public RhythmHud(SpriteBatch sb) {
        //define our tracking variables
        combo = 300;
        score = 0;


        //setup the HUD viewport using a new camera seperate from our gamecam
        //define our stage using that viewport and our games spritebatch
        viewport = new FitViewport(Gdx.graphics.getWidth() / PPM, Gdx.graphics.getHeight() / PPM / 2, new OrthographicCamera());
        stage = new Stage(viewport, sb);

        //define a table used to organize our hud's labels
        Table table = new Table();
        //Top-Align table
        table.top();
        //make the table fill the entire stage
        table.setFillParent(true);

        //define our labels using the String, and a Label style consisting of a font and color
        comboLabel = new Label(String.format("%03d", combo), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        scoreLabel = new Label(String.format("%06d", score), new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        //add our labels to our table, padding the top, and giving them all equal width with expandX
        table.add(comboLabel).expandX().padTop(10);
        table.add(scoreLabel).expandX().padTop(10);

        //add our table to the stage
        stage.addActor(table);

    }

    public static void addScore(int value){
        score += value;
        scoreLabel.setText(String.format("%06d", score));
    }

    public static void addCombo(int value) {
        combo += value;
        comboLabel.setText(String.format("%03d", combo));
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

}
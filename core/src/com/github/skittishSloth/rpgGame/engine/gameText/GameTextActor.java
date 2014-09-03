/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.skittishSloth.rpgGame.engine.gameText;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 *
 * @author mcory01
 */
public class GameTextActor extends Actor {

    private static final String text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam lacinia semper tortor. Praesent fringilla nisi ac justo ornare fermentum. Vestibulum vel maximus nunc. Mauris hendrerit faucibus urna, vel vestibulum est tempus eu. Donec ut malesuada mauris, at malesuada augue. Morbi a dolor congue, vehicula orci a, faucibus nisi. Suspendisse ex arcu, pretium at purus non, luctus suscipit lacus. Quisque pulvinar, eros sit amet interdum pharetra, eros elit maximus nisi, tempus finibus tortor dui suscipit nibh. Nullam id egestas arcu. Phasellus vitae diam in risus lobortis fermentum. Sed vel vestibulum libero, et dictum neque. Aenean scelerisque arcu in orci semper, eu tristique erat venenatis. Nulla metus odio, consectetur at mi eget, commodo tristique est. Integer euismod mollis felis, sed venenatis quam maximus eu. Praesent eu nisi diam.";

    private static final int MARGIN = 200;
    
    public GameTextActor() {
        super();

        final Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        

        table = new Table(skin);
//        table.add(nameLabel);
//        table.add(nameText).width(100);
//        table.row();
//        table.add(addressLabel);
//        table.add(addressText).width(100);
        textlabel = new Label(text, skin);
        textlabel.setWrap(true);
        table.add(textlabel).width(600);
        table.layout();
    }

    public void update(final OrthographicCamera camera) {
        final float width = camera.viewportWidth - (2 * MARGIN);
        table.setWidth(width);
        table.layout();
        final Vector3 screenCoords = new Vector3(MARGIN, camera.viewportHeight, 0);
        final Vector3 worldCoords = camera.unproject(screenCoords);
        table.setX(worldCoords.x);
        table.setY(worldCoords.y);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        table.draw(batch, parentAlpha);
    }

    private final Table table;
    private final Label textlabel;
}


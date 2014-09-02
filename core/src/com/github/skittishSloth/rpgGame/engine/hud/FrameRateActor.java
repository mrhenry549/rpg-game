/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.skittishSloth.rpgGame.engine.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 *
 * @author mcory01
 */
public class FrameRateActor extends Actor {
    
    public FrameRateActor() {
        super();
        final Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        final BitmapFont font = new BitmapFont(Gdx.files.internal("default.fnt"));
        final Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);
        label = new Label("FPS: -1", style);
    }

    @Override
    public void draw(final Batch batch, final float parentAlpha) {
        final int fps = Gdx.graphics.getFramesPerSecond();
        label.setText("FPS: " + fps);
        label.draw(batch, parentAlpha);
    }
    
    public void update(final Camera camera) {
        if (coordsSet) {
            return;
        }
        
        coordsSet = true;
        final Vector3 screenCoords = new Vector3(2, 40, 0);
        final Vector3 worldCoords = camera.unproject(screenCoords);
        label.setX(worldCoords.x);
        label.setY(worldCoords.y);
    }
    
    private boolean coordsSet = false;
    private final Label label;
}

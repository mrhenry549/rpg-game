/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.skittishSloth.rpgGame.engine.hud;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.github.skittishSloth.rpgGame.engine.player.Player;

/**
 *
 * @author mcory01
 */
public class HealthBarActor extends Actor {

    public HealthBarActor(final Player player) {
        super();

        this.player = player;

        final Pixmap healthBarBoxPm = new Pixmap(104, 19, Pixmap.Format.RGBA8888);
        healthBarBoxPm.setColor(0, 0, 0, 1);
        healthBarBoxPm.drawRectangle(0, 0, 104, 19);
        final Texture healthBarBoxTexture = new Texture(healthBarBoxPm);
        healthBarBoxPm.dispose();

        healthBarBox = new Image(healthBarBoxTexture);

        final Pixmap healthBarPm = new Pixmap(100, 15, Pixmap.Format.RGBA8888);
        healthBarPm.setColor(1, 0, 0, 0.75f);
        healthBarPm.fill();
        final Texture healthBarTexture = new Texture(healthBarPm);
        healthBarPm.dispose();

        healthBar = new Image(healthBarTexture);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        healthBarBox.draw(batch, parentAlpha);
        healthBar.draw(batch, parentAlpha);
    }

    public void update(final Camera camera) {
        updateHealthBar(camera);
        updateHealthBarBox(camera);
        
        if (coordsSet) {
            return;
        }
        
        coordsSet = true;
    }

    private void updateHealthBar(final Camera camera) {
        final Vector3 screenCoords = new Vector3(2, healthBar.getHeight() + 2, 0);
        final Vector3 worldCoords = camera.unproject(screenCoords);
        healthBar.setX(worldCoords.x);
        healthBar.setY(worldCoords.y);
        final float healthPercent = player.getHealthPercentage();
        healthBar.setScaleX(healthPercent);
    }

    private void updateHealthBarBox(final Camera camera) {
        final Vector3 screenCoords = new Vector3(0, healthBarBox.getHeight(), 0);
        final Vector3 worldCoords = camera.unproject(screenCoords);
        healthBarBox.setX(worldCoords.x);
        healthBarBox.setY(worldCoords.y);
    }

    private boolean coordsSet;
    private final Player player;
    private final Image healthBarBox;
    private final Image healthBar;
}

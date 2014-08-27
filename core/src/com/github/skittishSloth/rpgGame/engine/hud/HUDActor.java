/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.skittishSloth.rpgGame.engine.hud;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.github.skittishSloth.rpgGame.engine.player.Player;

/**
 *
 * @author mcory01
 */
public class HUDActor extends Actor {
    
    public HUDActor(final Player player) {
        super();
        
        this.player = player;
        
        this.healthBarActor = new HealthBarActor(this.player);
    }

    @Override
    public void draw(final Batch batch, final float parentAlpha) {
        final Stage stage = getStage();
        final Camera camera = stage.getCamera();
        updateHealthBar(camera);
        healthBarActor.draw(batch, parentAlpha);
    }
    
    private void updateHealthBar(final Camera camera) {
        healthBarActor.update(camera);
    }
    
    private final Player player;
    private final HealthBarActor healthBarActor;
}

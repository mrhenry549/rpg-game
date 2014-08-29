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
import com.github.skittishSloth.rpgGame.engine.maps.ManagedMap;
import com.github.skittishSloth.rpgGame.engine.maps.MiniMapActor;
import com.github.skittishSloth.rpgGame.engine.player.AtlasPlayer;
import com.github.skittishSloth.rpgGame.engine.player.Player;

/**
 *
 * @author mcory01
 */
public class HUDActor extends Actor {
    
    public HUDActor(final AtlasPlayer player) {
        super();
        
        this.player = player;
        
        this.healthBarActor = new HealthBarActor(this.player);
        this.miniMapActor = new MiniMapActor();
    }

    @Override
    public void draw(final Batch batch, final float parentAlpha) {
        final Stage stage = getStage();
        final Camera camera = stage.getCamera();
        updateHealthBar(camera);
        healthBarActor.draw(batch, parentAlpha);
        if (miniMapActive) {
            updateMiniMap(camera);
            miniMapActor.draw(batch, parentAlpha);
        }
    }
    
    public void setCurrentMap(final ManagedMap map) {
        miniMapActor.setCurrentMap(map);
    }

    public boolean isMiniMapActive() {
        return miniMapActive;
    }

    public void setMiniMapActive(final boolean miniMapActive) {
        this.miniMapActive = miniMapActive;
    }
    
    private void updateHealthBar(final Camera camera) {
        healthBarActor.update(camera);
    }
    
    private void updateMiniMap(final Camera camera) {
        miniMapActor.updateMapSprite(camera);
    }
    
    private final AtlasPlayer player;
    private final HealthBarActor healthBarActor;
    private final MiniMapActor miniMapActor;
    
    private boolean miniMapActive = false;
}

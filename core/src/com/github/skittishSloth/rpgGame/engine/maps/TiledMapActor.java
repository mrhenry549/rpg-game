/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.skittishSloth.rpgGame.engine.maps;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 *
 * @author mcory01
 */
public class TiledMapActor extends Actor {

    public TiledMapActor(OrthogonalTiledMapRendererWithSprites mapRenderer) {
        this.mapRenderer = mapRenderer;
    }
    
    private final OrthogonalTiledMapRendererWithSprites mapRenderer;
}

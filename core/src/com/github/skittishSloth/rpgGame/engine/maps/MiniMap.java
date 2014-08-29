/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.skittishSloth.rpgGame.engine.maps;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Matrix4;
import com.github.skittishSloth.rpgGame.engine.player.PositionInformation;

/**
 *
 * @author mcory01
 */
public class MiniMap {

    private static final float SCALE = 15.0f;
    private static final int WIDTH = 200;
    private static final int HEIGHT = 200;

    public MiniMap(final OrthogonalTiledMapRendererWithSprites renderer, final OrthographicCamera origCamera) {
        this.renderer = renderer;
        this.origCamera = origCamera;

        //this.miniMapCamera = new OrthographicCamera(origCamera.viewportWidth, origCamera.viewportHeight);
        this.miniMapCamera = new OrthographicCamera(WIDTH, HEIGHT);
        this.miniMapCamera.zoom = SCALE;
    }

    public void render(final Batch batch) {
        final Color origColor = batch.getColor();
        final Color alphaColor = Color.CYAN;
        final float parentAlpha = origColor.a;
        alphaColor.a = 0.5f * parentAlpha;
        batch.setColor(alphaColor);
        
        final Matrix4 origProjectionMatrix = batch.getProjectionMatrix();
        batch.setProjectionMatrix(miniMapCamera.combined);
        renderer.setView(miniMapCamera);
        renderer.render();
        renderer.setView(origCamera);

        batch.setColor(origColor);
        batch.setProjectionMatrix(origProjectionMatrix);
    }

    public void updateCamera(final int worldSizeWidth, final int worldSizeHeight, final PositionInformation playerPos) {
        // calc min/max camera points inside the map
        final float minCameraX = miniMapCamera.zoom * (WIDTH / 2);
        final float maxCameraX = worldSizeWidth - minCameraX;
        final float minCameraY = miniMapCamera.zoom * (HEIGHT / 2);
        final float maxCameraY = worldSizeHeight - minCameraY;

        float charX = playerPos.getX();
        float charY = playerPos.getY();

        miniMapCamera.position.set(
                Math.min(maxCameraX, Math.max(charX, minCameraX)),
                Math.min(maxCameraY, Math.max(charY, minCameraY)),
                0
        );

        miniMapCamera.update(true);
    }

    private final OrthographicCamera origCamera, miniMapCamera;
    private final OrthogonalTiledMapRendererWithSprites renderer;

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.skittishSloth.rpgGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.skittishSloth.rpgGame.engine.maps.ManagedMap;
import com.github.skittishSloth.rpgGame.engine.maps.OrthogonalTiledMapRendererWithSprites;
import com.github.skittishSloth.rpgGame.engine.maps.TiledMapManager;
import com.github.skittishSloth.rpgGame.engine.maps.Transition;
import com.github.skittishSloth.rpgGame.engine.hud.HUDActor;
import com.github.skittishSloth.rpgGame.engine.maps.Item;
import com.github.skittishSloth.rpgGame.engine.player.Player;
import com.github.skittishSloth.rpgGame.engine.player.PositionInformation;
import com.github.skittishSloth.rpgGame.engine.transitionEffects.FadeInOutEffect;

/**
 *
 * @author mcory01
 */
public class WorldScreen implements Screen {

    private ManagedMap currentMap = null;

    private final Player player;

    private final TiledMapManager mapManager = new TiledMapManager();
    private final OrthographicCamera camera;

    private final OrthogonalTiledMapRendererWithSprites tiledMapRenderer;
    private final Stage stage;

    private final HUDActor hudActor;
    private final SpriteBatch batch;

    public WorldScreen() {
        stage = new Stage(new ScreenViewport());
        //camera = OrthographicCamera.class.cast(stage.getCamera());
        camera = new OrthographicCamera();
        batch = new SpriteBatch();
        mapManager.addMap("TheMap", "TheMap.tmx");
        mapManager.addMap("eastworld", "eastworld.tmx");
        mapManager.addMap("otherworld", "otherworld.tmx");
        mapManager.addMap("town", "town.tmx");

        final float w = Gdx.graphics.getWidth();
        final float h = Gdx.graphics.getHeight();

        currentMap = mapManager.getMap("TheMap");

        tiledMapRenderer = new OrthogonalTiledMapRendererWithSprites(currentMap, batch);

        player = new Player();

        hudActor = new HUDActor(player);
        stage.addActor(hudActor);

        currentMap.initializePlayer(null, null, 0, player);
        hudActor.setCurrentMap(currentMap);

        camera.setToOrtho(false, w, h);
        camera.update();

        Gdx.input.setInputProcessor(stage);
    }

    private boolean inTransition = false;

    @Override
    public void render(final float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!inTransition) {
            handleMovement(delta);

            handleCollisions();

            updateMap(delta);

            handleTransition(delta);
        }

        updateCamera();

        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        stage.act(delta);
        stage.draw();
    }

    private void updateMap(final float deltaTime) {
        currentMap.updatePlayer(player, deltaTime);
        currentMap.updateItems();
    }

    private void handleTransition(final float deltaTime) {
        final PositionInformation playerPos = player.getPositionInformation();
        float charX = playerPos.getX();
        float charY = playerPos.getY();
        final float width = player.getWidth();
        final float collisionHeight = player.getHeight() / 2;
        final Transition nextMap = currentMap.getTransition(charX, charY, width, collisionHeight);
        if (nextMap != null) {
            inTransition = true;

            final FadeInOutEffect fade = new FadeInOutEffect(stage);
            final Runnable afterFadeOut = new Runnable() {
                @Override
                public void run() {
                    final String prevMap = currentMap.getName();
                    currentMap.removePlayer();
                    currentMap = mapManager.getMap(nextMap.getMapName());
                    currentMap.initializePlayer(prevMap, nextMap.getIndex(), deltaTime, player);
                    tiledMapRenderer.setMap(currentMap);
                    hudActor.setCurrentMap(currentMap);
                }
            };

            final Runnable afterFadeIn = new Runnable() {
                @Override
                public void run() {
                    inTransition = false;
                }
            };

            fade.runEffect(afterFadeOut, afterFadeIn);
        }
    }

    private void updateCamera() {
        // calc total map size
        final int worldSizeWidth = currentMap.getWorldWidth();
        final int worldSizeHeight = currentMap.getWorldHeight();

        // calc min/max camera points inside the map
        final float minCameraX = camera.zoom * (camera.viewportWidth / 2);
        final float maxCameraX = worldSizeWidth - minCameraX;
        final float minCameraY = camera.zoom * (camera.viewportHeight / 2);
        final float maxCameraY = worldSizeHeight - minCameraY;

        final PositionInformation playerPos = player.getPositionInformation();

        float charX = playerPos.getX();
        float charY = playerPos.getY();

        camera.position.set(
                Math.min(maxCameraX, Math.max(charX, minCameraX)),
                Math.min(maxCameraY, Math.max(charY, minCameraY)),
                0
        );

        camera.update();
    }

    private void handleCollisions() {
        final float width = player.getWidth();
        final float collisionHeight = player.getHeight() / 2;

        final PositionInformation playerPos = player.getPositionInformation();
        float charX = playerPos.getX();
        float charY = playerPos.getY();
        final PositionInformation previousPosition = player.getPreviousPosition();
        float prevX = previousPosition.getX();
        float prevY = previousPosition.getY();
        if (currentMap.isCollision(charX, charY, width, collisionHeight) || currentMap.isOutOfBounds(charX, charY, width, collisionHeight)) {
            charX = prevX;
            charY = prevY;
        }

        playerPos.setX(charX);
        playerPos.setY(charY);
    }

    private void handleMovement(final float deltaTime) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            player.takeHit(50);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            performAction();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            player.attack();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            player.moveWest(deltaTime);
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            player.moveEast(deltaTime);
        } else if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            player.moveNorth(deltaTime);
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            player.moveSouth(deltaTime);
        } else {
            player.setMoving(false);
        }

        if (player.isAllAnimationFinished()) {
            player.setMoving(false);
        }
    }

    @Override
    public void resize(final int width, final int height) {

    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        mapManager.dispose();
        tiledMapRenderer.dispose();
        player.dispose();
        stage.dispose();
        batch.dispose();
    }

    private void performAction() {
        // based on the player's position and facing direction,
        // get any items within x tiles from their current position
        // in the direction they're facing.
        final Item item = currentMap.getNearbyItems(player);
        if (item == null) {
            return;
        }

        if (item.isActionPerformed()) {
            return;
        }

        if (!item.isAlive()) {
            return;
        }

        final String contains = item.getContains();
        System.err.println("You just got " + contains);
        item.setActionPerformed(true);
    }

}

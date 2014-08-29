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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.skittishSloth.rpgGame.engine.gameText.GameTextActor;
import com.github.skittishSloth.rpgGame.engine.maps.ManagedMap;
import com.github.skittishSloth.rpgGame.engine.maps.OrthogonalTiledMapRendererWithSprites;
import com.github.skittishSloth.rpgGame.engine.maps.TiledMapManager;
import com.github.skittishSloth.rpgGame.engine.maps.Transition;
import com.github.skittishSloth.rpgGame.engine.hud.HUDActor;
import com.github.skittishSloth.rpgGame.engine.player.Player;
import com.github.skittishSloth.rpgGame.engine.player.PositionInformation;

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
    private final GameTextActor gameTextActor;

    public WorldScreen() {
        stage = new Stage(new ScreenViewport());
        camera = OrthographicCamera.class.cast(stage.getCamera());
        mapManager.addMap("TheMap", "TheMap.tmx");
        mapManager.addMap("eastworld", "eastworld.tmx");
        mapManager.addMap("otherworld", "otherworld.tmx");

        final float w = Gdx.graphics.getWidth();
        final float h = Gdx.graphics.getHeight();

        currentMap = mapManager.getMap("TheMap");

        tiledMapRenderer = new OrthogonalTiledMapRendererWithSprites(currentMap, stage.getBatch());

        player = new Player();

        hudActor = new HUDActor(player);
        stage.addActor(hudActor);

        currentMap.initializePlayer(null, null, 0, player);
        hudActor.setCurrentMap(currentMap);

        camera.setToOrtho(false, w, h);
        camera.update();

        gameTextActor = new GameTextActor();
        //stage.addActor(gameTextActor);

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

            updateCharacter(delta);
            
            handleTransition(delta);
        }

        updateCamera();

        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        gameTextActor.update(camera);
        //miniMap.render(stage.getBatch());

        stage.act(delta);
        stage.draw();
    }

    private void updateCharacter(final float deltaTime) {
        currentMap.updatePlayer(player, deltaTime);
//        final TextureMapObject character = currentMap.getPlayerMapObject();
//
//        final PositionInformation playerPos = player.getPositionInformation();
//        character.setTextureRegion(player.getTextureRegion(deltaTime));
//        character.setX(playerPos.getX());
//        character.setY(playerPos.getY());
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
            stage.addAction(Actions.alpha(0.0f, 0.5f));
            final Actor actor = new Actor();
            actor.addAction(
                    Actions.sequence(
                            Actions.delay(0.5f),
                            Actions.run(new Runnable() {
                                @Override
                                public void run() {
                                    final String prevMap = currentMap.getName();
                                    currentMap.removePlayer();
                                    currentMap = mapManager.getMap(nextMap.getMapName());
                                    currentMap.initializePlayer(prevMap, nextMap.getIndex(), deltaTime, player);
                                    tiledMapRenderer.setMap(currentMap);
                                    hudActor.setCurrentMap(currentMap);
                                }
                            }),
                            Actions.run(new Runnable() {
                                @Override
                                public void run() {
                                    stage.getActors().removeValue(actor, true);
                                    stage.addAction(Actions.alpha(1.0f, 0.5f));
                                    inTransition = false;
                                }

                            })));
            stage.addActor(actor);
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
    }

}

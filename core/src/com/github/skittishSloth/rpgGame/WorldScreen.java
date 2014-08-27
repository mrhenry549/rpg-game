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
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.skittishSloth.rpgGame.engine.maps.ManagedMap;
import com.github.skittishSloth.rpgGame.engine.maps.OrthogonalTiledMapRendererWithSprites;
import com.github.skittishSloth.rpgGame.engine.maps.TiledMapActor;
import com.github.skittishSloth.rpgGame.engine.maps.TiledMapManager;
import com.github.skittishSloth.rpgGame.engine.maps.Transition;
import com.github.skittishSloth.rpgGame.engine.player.Direction;
import com.github.skittishSloth.rpgGame.engine.hud.HUDActor;
import com.github.skittishSloth.rpgGame.engine.player.Player;
import com.github.skittishSloth.rpgGame.engine.player.PositionInformation;
import java.util.EnumMap;
import java.util.Map;

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

    private final TiledMapActor mapActor;
    private final HUDActor hudActor;

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
        mapActor = new TiledMapActor(tiledMapRenderer);
        stage.addActor(mapActor);

        final Texture textureNorth = new Texture(Gdx.files.internal("pikachu-north.png"));
        final Texture textureNE = new Texture(Gdx.files.internal("pikachu-ne.png"));
        final Texture textureEast = new Texture(Gdx.files.internal("pikachu-east.png"));
        final Texture textureSE = new Texture(Gdx.files.internal("pikachu-se.png"));
        final Texture textureSouth = new Texture(Gdx.files.internal("pikachu-south.png"));
        final Texture textureSW = new Texture(Gdx.files.internal("pikachu-sw.png"));
        final Texture textureWest = new Texture(Gdx.files.internal("pikachu-west.png"));
        final Texture textureNW = new Texture(Gdx.files.internal("pikachu-nw.png"));
        final Map<Direction, Texture> dirTextures = new EnumMap<Direction, Texture>(Direction.class);
        for (final Direction dir : Direction.values()) {
            final Texture toUse;
            switch (dir) {
                case NORTH:
                    toUse = textureNorth;
                    break;
                case NORTH_EAST:
                    toUse = textureNE;
                    break;
                case EAST:
                    toUse = textureEast;
                    break;
                case SOUTH_EAST:
                    toUse = textureSE;
                    break;
                case SOUTH:
                    toUse = textureSouth;
                    break;
                case SOUTH_WEST:
                    toUse = textureSW;
                    break;
                case WEST:
                    toUse = textureWest;
                    break;
                case NORTH_WEST:
                    toUse = textureNW;
                    break;
                default:
                    toUse = null;
                    break;
            }

            dirTextures.put(dir, toUse);
        }

        player = new Player(dirTextures);
        
        hudActor = new HUDActor(player);
        stage.addActor(hudActor);

        currentMap.initializePlayer(null, null, player);

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

            updateCharacter();

            handleTransition();
        }

        updateCamera();

        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        stage.act(delta);
        stage.draw();
    }

    private void updateCharacter() {
        final TextureMapObject character = currentMap.getPlayerMapObject();

        final PositionInformation playerPos = player.getPositionInformation();

        character.setTextureRegion(player.getCurrentSprite());
        character.setX(playerPos.getX());
        character.setY(playerPos.getY());
    }

    private void handleTransition() {
        final PositionInformation playerPos = player.getPositionInformation();
        float charX = playerPos.getX();
        float charY = playerPos.getY();
        final float width = player.getWidth();
        final float collisionHeight = player.getHeight() / 2;
        final Transition nextMap = currentMap.getTransition(charX, charY, width, collisionHeight);
        if (nextMap != null) {
            inTransition = true;
            final Image img = new Image();
            img.setColor(1, 1, 1, 1);
            img.setSize(800, 800);
            img.addAction(
                    Actions.sequence(
                            Actions.alpha(0.0f, 0.5f),
                            Actions.run(new Runnable() {
                                @Override
                                public void run() {
                                    final String prevMap = currentMap.getName();
                                    currentMap.removePlayer();
                                    currentMap = mapManager.getMap(nextMap.getMapName());
                                    currentMap.initializePlayer(prevMap, nextMap.getIndex(), player);
                                    tiledMapRenderer.setMap(currentMap);
                                }
                            }),
                            Actions.alpha(1.0f, 0.5f),
                            Actions.run(new Runnable() {
                                @Override
                                public void run() {
                                    stage.getActors().removeValue(img, true);
                                    inTransition = false;
                                    img.setSize(0, 0);
                                }

                            })));

            stage.addActor(img);
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
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                player.moveNorthWest(deltaTime);
            } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                player.moveSouthWest(deltaTime);
            } else {
                player.moveWest(deltaTime);
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                player.moveNorthEast(deltaTime);
            } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                player.moveSouthEast(deltaTime);
            } else {
                player.moveEast(deltaTime);
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            player.moveNorth(deltaTime);
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            player.moveSouth(deltaTime);
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
    }

}

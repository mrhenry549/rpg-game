/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.skittishSloth.rpgGame.engine.maps;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 *
 * @author mcory01
 */
public class MiniMapActor extends Actor {

    public MiniMapActor() {
        super();
    }

    public void setCurrentMap(final ManagedMap currentMap) {
        this.currentMap = currentMap;

        buildMapTexture();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (currentMap == null) {
            return;
        }
        
        mapImage.draw(batch, parentAlpha);
    }

    public void updateMapSprite(final Camera camera) {
        if (mapImage == null) {
            System.err.println("Map image was null.");
            return;
        }
        
        final Vector3 screenCoords = new Vector3(camera.viewportWidth - mapImage.getWidth(), mapImage.getHeight(), 0);
        final Vector3 worldCoords = camera.unproject(screenCoords);
        mapImage.setX(worldCoords.x);
        mapImage.setY(worldCoords.y);
    }
    
    public void updatePlayerSprite(final Camera camera) {
        
    }

    private void buildMapTexture() {
        if (mapTexture != null) {
            mapTexture.dispose();
        }

        final int width = currentMap.getWorldWidth();
        final int height = currentMap.getWorldHeight();

        final Pixmap mapBase = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        mapBase.setColor(0, 0, 1, 0.5f);
        mapBase.fill();
        mapTexture = new Texture(mapBase);
        mapBase.dispose();

        final MapLayer eventLayer = currentMap.getEventsLayer();
        final MapObjects eventObjects = eventLayer.getObjects();
        for (final MapObject event : eventObjects) {
            final String eventType = event.getProperties().get("type", String.class);
            printProperties(event.getProperties());
            if ("transition".equals(eventType)) {
                if (event instanceof RectangleMapObject) {
                    final RectangleMapObject rectObj = RectangleMapObject.class.cast(event);
                    final Rectangle rect = rectObj.getRectangle();
                    final Float eventWidth = rect.getWidth();
                    final Float eventHeight = rect.getHeight();
                    final Float eventX = rect.getX();
                    final Float eventY = rect.getY();
                    final Pixmap exitPointPm = createExitPointPixmap(eventWidth.intValue(), eventHeight.intValue());

                    mapTexture.draw(exitPointPm, eventX.intValue(), eventY.intValue());

                    exitPointPm.dispose();
                }
            }
        }
        
        mapImage = new Image(mapTexture);
        
        mapImage.setSize(150, 150);
    }

    private Pixmap createExitPointPixmap(final int width, final int height) {
        final Pixmap pm = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pm.setColor(0.5f, 0.5f, 1.0f, 0.75f);
        pm.fillRectangle(0, 0, width, height);

        return pm;
    }

    private static void printProperties(final MapProperties properties) {
//        final Iterator<String> keys = properties.getKeys();
//        System.err.println("Properties:");
//        while (keys.hasNext()) {
//            final String key = keys.next();
//            final Object value = properties.get(key);
//            System.err.println("\t'" + key + "' : '" + value + "'");
//        }
    }

    private Texture mapTexture;
    private ManagedMap currentMap;
    private Image mapImage;
}

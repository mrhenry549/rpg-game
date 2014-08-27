/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.skittishSloth.rpgGame.engine.maps;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.github.skittishSloth.rpgGame.engine.player.Player;
import com.github.skittishSloth.rpgGame.engine.player.PositionInformation;

/**
 *
 * @author mcory01
 */
public class ManagedMap {

    private static final TmxMapLoader MAP_LOADER = new TmxMapLoader();

    public ManagedMap(final String name, final String path) {
        this.name = name;

        this.map = MAP_LOADER.load(path);

        final MapProperties prop = map.getProperties();
        this.mapWidth = prop.get("width", Integer.class); //how many tiles in map
        this.mapHeight = prop.get("height", Integer.class);
        this.tilePixelWidth = prop.get("tilewidth", Integer.class); //size of each tile
        this.tilePixelHeight = prop.get("tileheight", Integer.class);
        // calc total map size
        this.worldWidth = mapWidth * tilePixelWidth;
        this.worldHeight = mapHeight * tilePixelHeight;
    }

    public String getName() {
        return name;
    }

    public TiledMap getMap() {
        return map;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public int getTilePixelWidth() {
        return tilePixelWidth;
    }

    public int getTilePixelHeight() {
        return tilePixelHeight;
    }

    public int getWorldWidth() {
        return worldWidth;
    }

    public int getWorldHeight() {
        return worldHeight;
    }

    public MapLayer getObjectsLayer() {
        return map.getLayers().get("objects");
    }

    public MapLayer getEventsLayer() {
        return map.getLayers().get("events");
    }

    public MapLayer getCollisionsLayer() {
        return map.getLayers().get("collisions");
    }

    public MapLayer getPlayerLayer() {
        return map.getLayers().get("player");
    }
    
    public MapLayers getAllLayers() {
        return map.getLayers();
    }

    public MapObject getEntryPoint(final String source, final Integer index) {
        final MapLayer eventsLayer = getEventsLayer();
        final MapObjects events = eventsLayer.getObjects();

        for (final MapObject event : events) {
            final String type = event.getProperties().get("type", String.class);
            if (!("map_entry".equals(type))) {
                continue;
            }

            final String src = event.getProperties().get("source", String.class);
            if ((source == null) && (src == null)) {
                return event;
            }

            if (src == null) {
                continue;
            }
            
            if (!src.equals(source)) {
                continue;
            }

            final String tempEventIdx = event.getProperties().get("source_index", String.class);
            final Integer eventIdx;
            if (tempEventIdx == null) {
                eventIdx = null;
            } else {
                eventIdx = Integer.valueOf(tempEventIdx);
            }
            
            if (index == null) {
                System.err.println("Index was null.");
                if (eventIdx == null) {
                    System.err.println("But event index was also null, so we're good.");
                    return event;
                } else {
                    continue;
                }
            }

            if (eventIdx == null) {
                System.err.println("Original index wasn't null, but event index was.");
                continue; // this is probably indicative of a misformed map.
            }

            if (!(eventIdx.equals(index))) {
                System.err.println("Event idx (" + eventIdx + ") wasn't the same as index (" + index + ").");
                continue;
            }

            return event;
        }

        return null;
    }

    public TextureMapObject getPlayerMapObject() {
        return TextureMapObject.class.cast(getPlayerLayer().getObjects().get(0));
    }

    public void initializePlayer(final String source, final Integer index, final Player player) {
        final MapObject entryPoint = getEntryPoint(source, index);
        final TextureRegion textureRegion = player.getTextureRegion();
        final RectangleMapObject rectStartPoint;
        if (entryPoint == null) {
            System.err.println("[Map " + getName() + "]: entry point was null.");
            rectStartPoint = new RectangleMapObject(0, 0, textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
        } else {
            System.err.println("[Map " + getName() + "]: had an entry point!");
            rectStartPoint = RectangleMapObject.class.cast(entryPoint);
        }

        final float x = rectStartPoint.getRectangle().getX();
        final float y = rectStartPoint.getRectangle().getY();

        final TextureMapObject tmo = new TextureMapObject(textureRegion);
        tmo.setX(x);
        tmo.setY(y);
        final PositionInformation playerPos = player.getPositionInformation();
        playerPos.setX(x);
        playerPos.setY(y);
        getPlayerLayer().getObjects().add(tmo);
    }

    public void removePlayer() {
        getPlayerLayer().getObjects().remove(0);
    }

    public boolean isCollision(final float x, final float y, final float w, final float h) {
        // just checks collisions layer
        final MapLayer collisionsLayer = getCollisionsLayer();
        final MapObjects collisions = collisionsLayer.getObjects();
        final Rectangle charRect = new Rectangle(x, y, w, h);
        // there are several other types, Rectangle is probably the most common one
        for (final RectangleMapObject rectangleObject : collisions.getByType(RectangleMapObject.class)) {
            final Rectangle rectangle = rectangleObject.getRectangle();
            if (Intersector.overlaps(rectangle, charRect)) {
                return true;
            }
        }
        
        for (final CircleMapObject circleObj : collisions.getByType(CircleMapObject.class)) {
            System.err.println("Got a circle!");
            final Circle circle = circleObj.getCircle();
            if (Intersector.overlaps(circle, charRect)) {
                return true;
            }
        }
        
        final float rightX = x + w;
        final float bottomY = y + h;
        for (final PolygonMapObject polyObject : collisions.getByType(PolygonMapObject.class)) {
            final Polygon poly = polyObject.getPolygon();
            if (Intersector.overlaps(poly.getBoundingRectangle(), charRect)) {
                // use poly.contains(x, y) based on each rectangle vertex.
                if (poly.contains(x, y)) {
                    return true;
                }
                
                if (poly.contains(rightX, y)) {
                    return true;
                }
                
                if (poly.contains(x, bottomY)) {
                    return true;
                }
                
                if (poly.contains(rightX, bottomY)) {
                    return true;
                }
            }
        }

        return false;
    }

    public Transition getTransition(float x, float y, float w, float h) {
        final MapLayer eventsLayer = getEventsLayer();
        final MapObjects events = eventsLayer.getObjects();

        final Rectangle charRect = new Rectangle(x, y, w, h);
        for (RectangleMapObject rectangleObject : events.getByType(RectangleMapObject.class)) {
            final MapProperties props = rectangleObject.getProperties();
            final String objType = props.get("type", String.class);
            if (!("transition".equals(objType))) {
                continue;
            }

            final String dest = props.get("dest", String.class);

            final Integer destIndex;
            if (props.containsKey("dest_index")) {
                final String tempIdx = props.get("dest_index", String.class);
                destIndex = Integer.valueOf(tempIdx);
            } else {
                destIndex = null;
            }

            final Rectangle rectangle = rectangleObject.getRectangle();
            if (Intersector.overlaps(rectangle, charRect)) {
                if (dest != null) {
                    final Transition transition = new Transition(dest, destIndex);
                    return transition;
                }
            }
        }

        return null;
    }

    public boolean isOutOfBounds(float charX, float charY, float width, float height) {
        if (charX < 0) {
            return true;
        } else if (charY < 0) {
            return true;
        } else if (charX > (worldWidth - width)) {
            return true;
        } else if (charY > (worldHeight - height)) {
            return true;
        }

        return false;
    }

    public void dispose() {
        map.dispose();
    }

    private final TiledMap map;
    private final String name;

    private final int mapWidth;
    private final int mapHeight;
    private final int tilePixelWidth;
    private final int tilePixelHeight;
    private final int worldWidth;
    private final int worldHeight;

}

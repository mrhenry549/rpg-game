/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.skittishSloth.rpgGame.engine.maps;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;


/**
 *
 * @author mcory01
 */
public class OrthogonalTiledMapRendererWithSprites extends OrthogonalTiledMapRenderer {

    public OrthogonalTiledMapRendererWithSprites(final TiledMap map) {
        super(map);
    }
    
    public OrthogonalTiledMapRendererWithSprites(final ManagedMap map) {
        super(map.getMap());
    }
    
    public OrthogonalTiledMapRendererWithSprites(final ManagedMap map, final Batch batch) {
        super(map.getMap(), batch);
    }
    
    @Override
    public void renderObject(final MapObject object) {
        if (object instanceof TextureMapObject) {
            final TextureMapObject textureObj = (TextureMapObject) object;
            spriteBatch.draw(textureObj.getTextureRegion(), textureObj.getX(), textureObj.getY());
        }
    }
    
    public void setMap(final ManagedMap currentMap) {
        setMap(currentMap.getMap());
    }
}

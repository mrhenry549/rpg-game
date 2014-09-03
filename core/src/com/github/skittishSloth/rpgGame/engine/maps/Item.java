/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.skittishSloth.rpgGame.engine.maps;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;

/**
 *
 * @author mcory01
 */
public class Item implements Disposable {

    public Item(final String id, final Texture initialTexture, final Texture actionTexture, final String contains, final Rectangle rectangle) {
        this.id = id;
        this.initialTexture = initialTexture;
        this.actionTexture = actionTexture;
        this.contains = contains;
        this.rectangle = rectangle;
    }
    
    public String getId() {
        return id;
    }
    
    public boolean isActionable() {
        return (actionTexture != null);
    }

    public boolean isActionPerformed() {
        return actionPerformed;
    }

    public void setActionPerformed(final boolean actionPerformed) {
        this.actionPerformed = actionPerformed;
    }
    
    public Rectangle getRectangle() {
        return rectangle;
    }
    
    public TextureRegion getTextureRegion() {
        final TextureRegion res;
        
        if (actionPerformed && isActionable()) {
            res = new TextureRegion(actionTexture);
        } else {
            res = new TextureRegion(initialTexture);
        }
        
        return res;
    }

    public String getContains() {
        return contains;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(final boolean alive) {
        this.alive = alive;
    }
    
    @Override
    public void dispose() {
        if (initialTexture != null) {
            initialTexture.dispose();
        }
        
        if (actionTexture != null) {
            actionTexture.dispose();
        }
    }
    
    private final String id;
    private final Texture initialTexture;
    private final Texture actionTexture;
    private final String contains;
    private final Rectangle rectangle;
    
    private boolean actionPerformed = false;
    private boolean alive = true;
}

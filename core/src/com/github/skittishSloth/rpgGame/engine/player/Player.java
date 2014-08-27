/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.skittishSloth.rpgGame.engine.player;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.EnumMap;
import java.util.Map;

/**
 *
 * @author mcory01
 */
public class Player {
    
    public Player(final Map<Direction, Texture> textures) {
        for (final Direction dir : textures.keySet()) {
            final Texture texture = textures.get(dir);
            final Sprite dirSprite = new Sprite(texture);
            
            final float angle = dir.getAngle();
            dirSprite.setRotation(angle);
            
            directionalSprites.put(dir, dirSprite);
        }
    }
    
    public Sprite getCurrentSprite() {
        final Direction curDirection = position.getDirection();
        return directionalSprites.get(curDirection);
    }
    
    public TextureRegion getTextureRegion() {
        final Texture texture = getCurrentSprite().getTexture();
        final TextureRegion textureRegion = new TextureRegion(texture, texture.getWidth(), texture.getHeight());
        return textureRegion;
    }
    
    public PositionInformation getPositionInformation() {
        return position;
    }
    
    public void dispose() {
        for (final Sprite sprite : directionalSprites.values()) {
            sprite.getTexture().dispose();
        }
    }

    public float getWidth() {
        return getCurrentSprite().getWidth();
    }

    public float getHeight() {
        return getCurrentSprite().getHeight();
    }

    public void moveNorth(final float deltaTime) {
        final float curY = position.getY();
        previousPosition.setFrom(position);
        position.setY(curY + getMovementDelta(deltaTime));
        position.setDirection(Direction.NORTH);
    }
    
    public void moveNorthEast(final float deltaTime) {
        previousPosition.setFrom(position);
        final float curY = position.getY();
        final float curX = position.getX();
        final float movementDelta = getMovementDelta(deltaTime);
        position.setY(curY + movementDelta);
        position.setX(curX + movementDelta);
        position.setDirection(Direction.NORTH_EAST);
    }

    public void moveEast(final float deltaTime) {
        final float curX = position.getX();
        previousPosition.setFrom(position);
        position.setX(curX + getMovementDelta(deltaTime));
        position.setDirection(Direction.EAST);
    }
    
    public void moveSouthEast(final float deltaTime) {
        previousPosition.setFrom(position);
        final float curY = position.getY();
        final float curX = position.getX();
        final float movementDelta = getMovementDelta(deltaTime);
        position.setY(curY - movementDelta);
        position.setX(curX + movementDelta);
        position.setDirection(Direction.SOUTH_EAST);
    }

    public void moveSouth(final float deltaTime) {
        final float curY = position.getY();
        previousPosition.setFrom(position);
        position.setY(curY - getMovementDelta(deltaTime));
        position.setDirection(Direction.SOUTH);
    }
    
    public void moveSouthWest(final float deltaTime) {
        previousPosition.setFrom(position);
        final float curY = position.getY();
        final float curX = position.getX();
        final float movementDelta = getMovementDelta(deltaTime);
        position.setY(curY - movementDelta);
        position.setX(curX - movementDelta);
        position.setDirection(Direction.SOUTH_WEST);
    }

    public void moveWest(final float deltaTime) {
        final float curX = position.getX();
        previousPosition.setFrom(position);
        position.setX(curX - getMovementDelta(deltaTime));
        position.setDirection(Direction.WEST);
    }
    
    public void moveNorthWest(final float deltaTime) {
        previousPosition.setFrom(position);
        final float curY = position.getY();
        final float curX = position.getX();
        final float movementDelta = getMovementDelta(deltaTime);
        position.setY(curY + movementDelta);
        position.setX(curX - movementDelta);
        position.setDirection(Direction.NORTH_WEST);
    }
    
    public PositionInformation getPreviousPosition() {
        return previousPosition;
    }
    
    public float getMovementSpeed() {
        return 200.0f;
    }
    
    public float getMovementDelta(final float deltaTime) {
        return getMovementSpeed() * deltaTime;
    }
    
    public int getMaxHealth() {
        return maxHealth;
    }
    
    public int getCurrentHealth() {
        return currentHealth;
    }
    
    public void takeHit(final int amount) {
        currentHealth -= amount;
        if (currentHealth < 0) {
            currentHealth = 0;
        }
    }
    
    public float getHealthPercentage() {
        return ((float)currentHealth) / ((float)maxHealth);
    }
    
    private int maxHealth = 500;
    
    private int currentHealth = maxHealth;
    
    private final Map<Direction, Sprite> directionalSprites = new EnumMap<Direction, Sprite>(Direction.class);
    private final PositionInformation position = new PositionInformation();
    private final PositionInformation previousPosition = new PositionInformation();
}

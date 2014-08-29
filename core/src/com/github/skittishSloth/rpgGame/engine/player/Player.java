/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.skittishSloth.rpgGame.engine.player;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import com.github.skittishSloth.rpgGame.engine.common.Direction;
import com.github.skittishSloth.rpgGame.engine.sprites.AnimationState;
import com.github.skittishSloth.rpgGame.engine.sprites.UniversalDirectionalSprite;

/**
 *
 * @author mcory01
 */
public class Player implements Disposable {

    private static final float FRAME_RATE = 1 / 15f;

    public Player() {
        this.bodySprite = new UniversalDirectionalSprite("gfx/sprites/body/male/light.png", FRAME_RATE, AnimationState.values());
        this.hatSprite = new UniversalDirectionalSprite("gfx/sprites/head/caps/male/leather_cap_male.png", FRAME_RATE, AnimationState.values());
        this.pantsSprite = new UniversalDirectionalSprite("gfx/sprites/legs/pants/male/teal_pants_male.png", FRAME_RATE, AnimationState.values());
        this.torsoSprite = new UniversalDirectionalSprite("gfx/sprites/torso/chain/mail_male.png", FRAME_RATE, AnimationState.values());
    }

    public PositionInformation getPositionInformation() {
        return position;
    }

    public void moveNorth(final float deltaTime) {
        final float curY = position.getY();
        previousPosition.setFrom(position);
        position.setY(curY + getMovementDelta(deltaTime));
        position.setDirection(Direction.UP);
        setCurrentDirection(Direction.UP);
        setMoving(true);
        setAnimationState(AnimationState.WALKING);
    }

    public void moveEast(final float deltaTime) {
        final float curX = position.getX();
        previousPosition.setFrom(position);
        position.setX(curX + getMovementDelta(deltaTime));
        position.setDirection(Direction.RIGHT);
        setCurrentDirection(Direction.RIGHT);
        setMoving(true);
        setAnimationState(AnimationState.WALKING);
    }

    public void moveSouth(final float deltaTime) {
        final float curY = position.getY();
        previousPosition.setFrom(position);
        position.setY(curY - getMovementDelta(deltaTime));
        position.setDirection(Direction.DOWN);
        setCurrentDirection(Direction.DOWN);
        setMoving(true);
        setAnimationState(AnimationState.WALKING);
    }

    public void moveWest(final float deltaTime) {
        final float curX = position.getX();
        previousPosition.setFrom(position);
        position.setX(curX - getMovementDelta(deltaTime));
        position.setDirection(Direction.LEFT);
        setCurrentDirection(Direction.LEFT);
        setMoving(true);
        setAnimationState(AnimationState.WALKING);
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
        if (currentHealth <= 0) {
            currentHealth = 0;
            setAnimationState(AnimationState.HURT);
        }
    }

    public TextureRegion[] getTextureRegions(final float deltaTime) {
        final TextureRegion bodyRegion = bodySprite.getTextureRegion(deltaTime);
        final TextureRegion capRegion = hatSprite.getTextureRegion(deltaTime);
        final TextureRegion pantsRegion = pantsSprite.getTextureRegion(deltaTime);
        final TextureRegion torsoRegion = torsoSprite.getTextureRegion(deltaTime);
        return new TextureRegion[] {
            bodyRegion,
            capRegion,
            pantsRegion,
            torsoRegion
        };
    }

    public float getWidth() {
        return bodySprite.getWidth();
    }

    public int getHeight() {
        return bodySprite.getHeight();
    }
    
    @Override
    public void dispose() {
        bodySprite.dispose();
        hatSprite.dispose();
        pantsSprite.dispose();
        torsoSprite.dispose();
    }

    public float getHealthPercentage() {
        return ((float) currentHealth) / ((float) maxHealth);
    }

    public void setCurrentDirection(Direction direction) {
        bodySprite.setCurrentDirection(direction);
        hatSprite.setCurrentDirection(direction);
        pantsSprite.setCurrentDirection(direction);
        torsoSprite.setCurrentDirection(direction);
    }

    public void setMoving(boolean moving) {
        bodySprite.setMoving(moving);
        hatSprite.setMoving(moving);
        pantsSprite.setMoving(moving);
        torsoSprite.setMoving(moving);
    }

    public void setAnimationState(AnimationState animationState) {
        bodySprite.setAnimationState(animationState);
        hatSprite.setAnimationState(animationState);
        pantsSprite.setAnimationState(animationState);
        torsoSprite.setAnimationState(animationState);
    }

    private int maxHealth = 500;

    private int currentHealth = maxHealth;
    
    private final UniversalDirectionalSprite bodySprite, hatSprite, pantsSprite, torsoSprite;
    
    private final PositionInformation position = new PositionInformation();
    private final PositionInformation previousPosition = new PositionInformation();
}

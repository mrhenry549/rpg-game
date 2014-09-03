/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.skittishSloth.rpgGame.engine.player;

import com.badlogic.gdx.graphics.Texture;
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

        final Texture bodyTexture = new Texture("gfx/sprites/body/male/light.png");
        final Texture headTexture = new Texture("gfx/sprites/head/caps/male/leather_cap_male.png");
        final Texture pantsTexture = new Texture("gfx/sprites/legs/pants/male/teal_pants_male.png");
        final Texture torsoTexture = new Texture("gfx/sprites/torso/chain/mail_male.png");
        final Texture weaponTexture = new Texture("gfx/sprites/weapons/right hand/male/dagger_male.png");
        final Texture[] textures = new Texture[]{
            bodyTexture,
            headTexture,
            pantsTexture,
            torsoTexture,
            weaponTexture
        };

        mergedSprite = UniversalDirectionalSprite.createdMergedSprite(FRAME_RATE, AnimationState.values(), textures);
    }

    public PositionInformation getPositionInformation() {
        return position;
    }

    public void moveNorth(final float deltaTime) {
        if (!canMove()) {
            return;
        }

        final float curY = position.getY();
        previousPosition.setFrom(position);
        position.setY(curY + getMovementDelta(deltaTime));
        position.setDirection(Direction.UP);
        setCurrentDirection(Direction.UP);
        setMoving(true);
        setAnimationState(AnimationState.WALKING);
    }

    public void moveEast(final float deltaTime) {
        if (!canMove()) {
            return;
        }

        final float curX = position.getX();
        previousPosition.setFrom(position);
        position.setX(curX + getMovementDelta(deltaTime));
        position.setDirection(Direction.RIGHT);
        setCurrentDirection(Direction.RIGHT);
        setMoving(true);
        setAnimationState(AnimationState.WALKING);
    }

    public void moveSouth(final float deltaTime) {
        if (!canMove()) {
            return;
        }

        final float curY = position.getY();
        previousPosition.setFrom(position);
        position.setY(curY - getMovementDelta(deltaTime));
        position.setDirection(Direction.DOWN);
        setCurrentDirection(Direction.DOWN);
        setMoving(true);
        setAnimationState(AnimationState.WALKING);
    }

    public void moveWest(final float deltaTime) {
        if (!canMove()) {
            return;
        }

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

    public void attack() {
        setAnimationState(AnimationState.SLASHING);
    }

    public TextureRegion getTextureRegion(final float deltaTime) {
        return mergedSprite.getTextureRegion(deltaTime);
    }

    public float getWidth() {
        return mergedSprite.getWidth();
    }

    public int getHeight() {
        return mergedSprite.getHeight();
    }

    @Override
    public void dispose() {
        mergedSprite.dispose();
    }

    public float getHealthPercentage() {
        return ((float) currentHealth) / ((float) maxHealth);
    }

    public void setCurrentDirection(final Direction direction) {
        mergedSprite.setCurrentDirection(direction);
    }

    public void setMoving(final boolean moving) {
        mergedSprite.setMoving(moving);
    }

    public void setAnimationState(final AnimationState animationState) {
        currentAnimation = animationState;
        mergedSprite.setAnimationState(animationState);
    }

    public boolean isAllAnimationFinished() {
        return mergedSprite.isAnimationFinished();
    }

    public boolean needsUpdate() {
        if (!(position.equals(previousPosition))) {
            return true;
        }

        return (!(isAllAnimationFinished()));
    }

    private boolean canMove() {
        final boolean bodySpriteMoveable = mergedSprite.isMoveable();
        return (currentAnimation.isMoveable() || bodySpriteMoveable);
    }

    private final int maxHealth = 500;

    private int currentHealth = maxHealth;

    private final UniversalDirectionalSprite mergedSprite;

    private AnimationState currentAnimation = AnimationState.IDLE;

    private final PositionInformation position = new PositionInformation();
    private final PositionInformation previousPosition = new PositionInformation();
}

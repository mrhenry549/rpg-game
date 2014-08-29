/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.skittishSloth.rpgGame.engine.player;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 *
 * @author mcory01
 */
public class AtlasPlayer {

    private static final int NUM_FRAMES = 3;
    private static final float FRAME_RATE = 1 / 10f;

    public AtlasPlayer() {
        atlas = new TextureAtlas("gfx/sprites/kavi-32-64.pack");

        final TextureRegion[] upFrames = getFrames("up_", atlas);
        upAnimation = new Animation(FRAME_RATE, upFrames);

        upIdle = getIdleRegion("up_", atlas);

        final TextureRegion[] downFrames = getFrames("down_", atlas);
        downAnimation = new Animation(FRAME_RATE, downFrames);

        downIdle = getIdleRegion("down_", atlas);

        final TextureRegion[] leftFrames = getFrames("left_", atlas);
        leftAnimation = new Animation(FRAME_RATE, leftFrames);

        leftIdle = getIdleRegion("left_", atlas);

        final TextureRegion[] rightFrames = getFrames("right_", atlas);
        rightAnimation = new Animation(FRAME_RATE, rightFrames);

        rightIdle = getIdleRegion("right_", atlas);
    }

    public TextureRegion getTextureRegion(final float deltaTime) {
        final TextureRegion res;
        final Direction dir = position.getDirection();
        if (!moving) {
            switch (dir) {
                case NORTH:
                    res = upIdle;
                    break;
                case EAST:
                    res = rightIdle;
                    break;
                case SOUTH:
                    res = downIdle;
                    break;
                case WEST:
                    res = leftIdle;
                    break;
                default:
                    res = upIdle;
                    break;
            }
        } else {
            movingTime += deltaTime;
            final Animation curAnim;
            switch (dir) {
                case NORTH:
                    curAnim = upAnimation;
                    break;
                case EAST:
                    curAnim = rightAnimation;
                    break;
                case SOUTH:
                    curAnim = downAnimation;
                    break;
                case WEST:
                    curAnim = leftAnimation;
                    break;
                default:
                    curAnim = upAnimation;
                    break;
            }

            res = curAnim.getKeyFrame(movingTime, true);
        }
        
        return res;
    }

    public boolean isMoving() {
        return moving;
    }

    public void setMoving(final boolean moving) {
        if (!moving) {
            movingTime = 0;
        }

        this.moving = moving;
    }

    public PositionInformation getPositionInformation() {
        return position;
    }

    public void dispose() {
        atlas.dispose();
    }

    public float getWidth() {
        //return getCurrentSprite().getWidth();
        return 32f;
    }

    public float getHeight() {
//        return getCurrentSprite().getHeight();
        return 64f;
    }

    public void moveNorth(final float deltaTime) {
        final float curY = position.getY();
        previousPosition.setFrom(position);
        position.setY(curY + getMovementDelta(deltaTime));
        position.setDirection(Direction.NORTH);
        setMoving(true);
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
        setMoving(true);
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
        setMoving(true);
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
        setMoving(true);
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
        return ((float) currentHealth) / ((float) maxHealth);
    }

    private static TextureRegion[] getFrames(final String prefix, final TextureAtlas atlas) {
        System.err.println("Building frames for '" + prefix + "' prefix.");
        final TextureRegion[] frames = new TextureRegion[3];
        for (int i = 0; i < NUM_FRAMES; ++i) {
            final String frameName = prefix + "walk_0" + (i + 1);
            frames[i] = atlas.findRegion(frameName);
            System.err.println("\tFrames[" + i + "] ('" + frameName + "') null? " + (frames[i] == null));
        }

        return frames;
    }

    private static TextureRegion getIdleRegion(final String prefix, final TextureAtlas atlas) {
        final TextureRegion res = atlas.findRegion(prefix + "idle");
        System.err.println("Idle region: '" + prefix + "' null? " + (res == null));
        return res;
    }

    private int maxHealth = 500;

    private int currentHealth = maxHealth;
    private boolean moving = false;
    private float movingTime = 0.0f;

    private final TextureAtlas atlas;
    private final TextureRegion upIdle, leftIdle, rightIdle, downIdle;
    private final Animation upAnimation, leftAnimation, rightAnimation, downAnimation;
    private final PositionInformation position = new PositionInformation();
    private final PositionInformation previousPosition = new PositionInformation();
}

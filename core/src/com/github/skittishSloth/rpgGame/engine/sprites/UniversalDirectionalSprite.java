/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.skittishSloth.rpgGame.engine.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import com.github.skittishSloth.rpgGame.engine.common.Direction;
import java.util.EnumMap;
import java.util.Map;

/**
 *
 * @author mcory01
 */
public class UniversalDirectionalSprite implements Disposable {

    public static final int SPRITE_WIDTH = 64;
    public static final int SPRITE_HEIGHT = 64;

    public UniversalDirectionalSprite(final String imgName, final float frameRate, final AnimationState... availableAnimations) {
        this.frameRate = frameRate;
        baseTexture = new Texture(imgName);

        this.availableAnimations = availableAnimations;

        for (final AnimationState mState : this.availableAnimations) {
            final Map<Direction, Animation> directionAnimations = new EnumMap<Direction, Animation>(Direction.class);
            for (final Direction dir : Direction.values()) {
                if (mState == AnimationState.IDLE) {
                    final TextureRegion idle = getIdleRegion(dir, baseTexture);
                    System.err.println("Got idle region for direction " + dir);
                    System.err.println("W: " + idle.getRegionWidth() + ", H: " + idle.getRegionHeight());
                    final Animation idleAnimation = new Animation(this.frameRate, idle);
                    directionAnimations.put(dir, idleAnimation);
                } else {
                    final TextureRegion[] frames = getFrames(dir, mState, baseTexture);
                    final Animation anim = new Animation(this.frameRate, frames);
                    directionAnimations.put(dir, anim);
                }
            }
            this.animations.put(mState, directionAnimations);
        }
    }

    public TextureRegion getTextureRegion(final float deltaTime) {
        final Map<Direction, Animation> stateAnimations = animations.get(animationState);
        final Animation animation = stateAnimations.get(currentDirection);
        if (animationState != AnimationState.IDLE) {
            movementTime += deltaTime;
        }

        final TextureRegion res = animation.getKeyFrame(movementTime, animationState.isLoopable());
        
        this.width = res.getRegionWidth();
        this.height = res.getRegionHeight();
        return res;
    }

    @Override
    public void dispose() {
        baseTexture.dispose();
    }

    public Direction getCurrentDirection() {
        return currentDirection;
    }

    public void setCurrentDirection(final Direction currentDirection) {
        this.currentDirection = currentDirection;
    }

    public AnimationState getAnimationState() {
        return animationState;
    }

    public void setAnimationState(final AnimationState animationState) {
        this.animationState = animationState;
    }

    public boolean isMoving() {
        return moving;
    }

    public void setMoving(final boolean moving) {
        if (!moving) {
            movementTime = 0;
            for (final AnimationState available : availableAnimations) {
                if (available == AnimationState.IDLE) {
                    setAnimationState(AnimationState.IDLE);
                    break;
                }
            }
        }

        this.moving = moving;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    
    private static TextureRegion[] getFrames(final Direction direction, final AnimationState animationState, final Texture texture) {
        System.err.println("Building frames for " + direction + ", " + animationState);
        final int section = animationState.getSectionIndex();
        final int rowInSection = direction.getRowInSection();
        final int numFrames = animationState.getNumFrames();
        
        System.err.println("S: " + section + ", R: " + rowInSection + ", N: " + numFrames);
        final TextureRegion[] frames = new TextureRegion[numFrames];
        
        final int sectionOffset = section * 4 * SPRITE_HEIGHT;
        System.err.println("SO: " + sectionOffset);
        
        final int rowOffset = rowInSection * SPRITE_HEIGHT;
        System.err.println("RO: " + rowOffset);
        
        final int y = sectionOffset + rowOffset;
        System.err.println("YO: " + y);
        
        for (int i = 0; i < numFrames; ++i) {
            final int x = i * SPRITE_WIDTH;
            System.err.println("F[" + i + "]: x, y:" + x + ", " + y + ", w: " + SPRITE_WIDTH + ", h: " + SPRITE_HEIGHT);
            frames[i] = new TextureRegion(texture, x, y, SPRITE_WIDTH, SPRITE_HEIGHT);
        }

        return frames;
    }

    private static TextureRegion getIdleRegion(final Direction direction, final Texture texture) {
        final TextureRegion[] walkingFrames = getFrames(direction, AnimationState.WALKING, texture);
        return walkingFrames[0];
    }

    private Direction currentDirection = Direction.DOWN;
    private AnimationState animationState = AnimationState.IDLE;

    private final AnimationState[] availableAnimations;
    private final Texture baseTexture;

    private boolean moving = false;
    private float movementTime = 0f;
    private int width, height;

    private final float frameRate;
    private final Map<AnimationState, Map<Direction, Animation>> animations = new EnumMap<AnimationState, Map<Direction, Animation>>(AnimationState.class);
}

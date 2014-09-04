/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.skittishSloth.rpgGame.engine.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.skittishSloth.rpgGame.engine.common.Direction;
import com.github.skittishSloth.rpgGame.engine.equipment.PlayerEquipment;
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

        final int height = bodyTexture.getHeight();
        final int width = bodyTexture.getWidth();
        
        final String VERTEX = Gdx.files.internal("passthrough.vert").readString();
        final String FRAGMENT = Gdx.files.internal("invert.frag").readString();
        final ShaderProgram program = new ShaderProgram(VERTEX, FRAGMENT);
        //Good idea to log any warnings if they exist
        if (program.getLog().length() != 0) {
            System.out.println("PrgLog: " + program.getLog());
        }
        final SpriteBatch sb = new SpriteBatch();
        sb.setShader(program);
        
        //Set the projection matrix for the SpriteBatch.
        final Matrix4 projectionMatrix = new Matrix4();

        //because Pixmap has its origin on the topleft and everything else in LibGDX has the origin left bottom
        //we flip the projection matrix on y and move it -height. So it will end up side up in the .png
        projectionMatrix.setToOrtho2D(0, -height, width, height).scale(1, -1, 1);

        //Set the projection matrix on the SpriteBatch
        sb.setProjectionMatrix(projectionMatrix);
        sb.enableBlending();
        
        final FrameBuffer fb = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, true);
        //make the FBO the current buffer
        fb.begin();
        
        //render some sprites 
        sb.begin();
        sb.draw(bodyTexture, 0, 0);

        sb.end();
        
        final Pixmap bodyTintedPm = ScreenUtils.getFrameBufferPixmap(0, 0, width, height);
        fb.end();
        
        
        final Texture bodyTinted = new Texture(bodyTintedPm);
        bodyTintedPm.dispose();
       
        fb.dispose();
        sb.dispose();
        final Texture[] textures = new Texture[]{
            bodyTinted,
            headTexture,
            pantsTexture,
            torsoTexture,
            weaponTexture,
        };

        mergedSprite = UniversalDirectionalSprite.createdMergedSprite(FRAME_RATE, AnimationState.values(), textures);
        
        for (final Texture texture : textures) {
            
            texture.dispose();
        }
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
    private final PlayerEquipment equipment = new PlayerEquipment();
}

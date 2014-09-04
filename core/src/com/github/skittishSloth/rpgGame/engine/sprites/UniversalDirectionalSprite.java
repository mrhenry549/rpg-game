/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.skittishSloth.rpgGame.engine.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.skittishSloth.rpgGame.engine.common.Direction;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.EnumMap;
import java.util.Map;

/**
 *
 * @author mcory01
 */
public class UniversalDirectionalSprite implements Disposable {

    public static final int SPRITE_WIDTH = 64;
    public static final int SPRITE_HEIGHT = 64;

    public static UniversalDirectionalSprite createdMergedSprite(final float frameRate, final AnimationState[] availableAnimations, final Texture[] textures) {
        // get the first texture as a baseline.
        final Texture baselineTexture = textures[0];
        final int width = baselineTexture.getWidth();
        final int height = baselineTexture.getHeight();

        final FrameBuffer fb = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);

        final int numTextures = textures.length;
        //read the files into strings
        final String VERTEX = Gdx.files.internal("passthrough.vert").readString();
        final String FRAGMENT = Gdx.files.internal("invert.frag").readString();

        final String VERT
                = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
                + "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n"
                + "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n"
                + "uniform mat4 u_projTrans;\n"
                + " \n"
                + "varying vec4 vColor;\n"
                + "varying vec2 vTexCoord;\n"
                + "void main() {\n"
                + "	vColor = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n"
                + "	vTexCoord = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n"
                + "	gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
                + "}";

        //This will be dumped to System.out for clarity
        final String FRAG
                = //GL ES specific stuff
                "#ifdef GL_ES\n" //
                + "#define LOWP lowp\n" //
                + "precision mediump float;\n" //
                + "#else\n" //
                + "#define LOWP \n" //
                + "#endif\n" + //
                "//texture 0\n"
                + "uniform sampler2D u_texture;\n"
                + "\n"
                + "//our screen resolution, set from Java whenever the display is resized\n"
                + "uniform vec2 resolution;\n"
                + "\n"
                + "//\"in\" attributes from our vertex shader\n"
                + "varying LOWP vec4 vColor;\n"
                + "varying vec2 vTexCoord;\n"
                + "\n"
                + "//RADIUS of our vignette, where 0.5 results in a circle fitting the screen\n"
                + "const float RADIUS = 0.75;\n"
                + "\n"
                + "//softness of our vignette, between 0.0 and 1.0\n"
                + "const float SOFTNESS = 0.45;\n"
                + "\n"
                + "//sepia colour, adjust to taste\n"
                + "const vec3 SEPIA = vec3(1.2, 1.0, 0.8); \n"
                + "\n"
                + "void main() {\n"
                + "	//sample our texture\n"
                + "	vec4 texColor = texture2D(u_texture, vTexCoord);\n"
                + "		\n"
                + "	//1. VIGNETTE\n"
                + "	\n"
                + "	//determine center position\n"
                + "	vec2 position = (gl_FragCoord.xy / resolution.xy) - vec2(0.5);\n"
                + "	\n"
                + "	//determine the vector length of the center position\n"
                + "	float len = length(position);\n"
                + "	\n"
                + "	//use smoothstep to create a smooth vignette\n"
                + "	float vignette = smoothstep(RADIUS, RADIUS-SOFTNESS, len);\n"
                + "	\n"
                + "	//apply the vignette with 50% opacity\n"
                + "	texColor.rgb = mix(texColor.rgb, texColor.rgb * vignette, 0.5);\n"
                + "		\n"
                + "	//2. GRAYSCALE\n"
                + "	\n"
                + "	//convert to grayscale using NTSC conversion weights\n"
                + "	float gray = dot(texColor.rgb, vec3(0.299, 0.587, 0.114));\n"
                + "	\n"
                + "	//3. SEPIA\n"
                + "	\n"
                + "	//create our sepia tone from some constant value\n"
                + "	vec3 sepiaColor = vec3(gray) * SEPIA;\n"
                + "		\n"
                + "	//again we'll use mix so that the sepia effect is at 75%\n"
                + "	texColor.rgb = mix(texColor.rgb, sepiaColor, 0.75);\n"
                + "		\n"
                + "	//final colour, multiplied by vertex colour\n"
                + "	gl_FragColor = texColor * vColor;\n"
                + "}";

        //create our shader program -- be sure to pass SpriteBatch's default attributes!
        final ShaderProgram program = new ShaderProgram(VERTEX, FRAGMENT);
        //final ShaderProgram program = new ShaderProgram(vertexShader, fragmentShader);

        //Set the projection matrix for the SpriteBatch.
        final Matrix4 projectionMatrix = new Matrix4();

        //because Pixmap has its origin on the topleft and everything else in LibGDX has the origin left bottom
        //we flip the projection matrix on y and move it -height. So it will end up side up in the .png
        projectionMatrix.setToOrtho2D(0, -height, width, height).scale(1, -1, 1);

        //Good idea to log any warnings if they exist
        if (program.getLog().length() != 0) {
            System.out.println("PrgLog: " + program.getLog());
        }

        //create our sprite batch
        final SpriteBatch sb = new SpriteBatch();
//        sb.setShader(program);

        //Set the projection matrix on the SpriteBatch
        sb.setProjectionMatrix(projectionMatrix);
        fb.begin();
        sb.begin();
        for (int i = 0; i < numTextures; ++i) {
            final Texture texture = textures[i];
            sb.draw(texture, 0, 0);
        }
        sb.end();

        final Pixmap pm = ScreenUtils.getFrameBufferPixmap(0, 0, width, height);

        fb.end();

        final UniversalDirectionalSprite res = new UniversalDirectionalSprite(new Texture(pm), frameRate, availableAnimations);
        pm.dispose();
        sb.dispose();
        fb.dispose();
        return res;
    }

    public static String readFile(InputStream in) throws IOException {
        final StringBuffer sBuffer = new StringBuffer();
        final BufferedReader br = new BufferedReader(new InputStreamReader(in));
        final char[] buffer = new char[1024];

        int cnt;
        while ((cnt = br.read(buffer, 0, buffer.length)) > -1) {
            sBuffer.append(buffer, 0, cnt);
        }
        br.close();
        in.close();
        return sBuffer.toString();
    }

    public UniversalDirectionalSprite(final Texture baseTexture, final float frameRate, final AnimationState... availableAnimations) {
        this.frameRate = frameRate;
        this.baseTexture = baseTexture;

        this.availableAnimations = availableAnimations;

        for (final AnimationState mState : this.availableAnimations) {
            final Map<Direction, Animation> directionAnimations = new EnumMap<Direction, Animation>(Direction.class);
            for (final Direction dir : Direction.values()) {
                if (mState == AnimationState.IDLE) {
                    final TextureRegion idle = getIdleRegion(dir, baseTexture);
                    final Animation idleAnimation = new Animation(this.frameRate, idle);
                    directionAnimations.put(dir, idleAnimation);
                } else if (mState != AnimationState.HURT) {
                    final TextureRegion[] frames = getFrames(dir, mState, baseTexture);
                    final Animation anim = new Animation(this.frameRate, frames);
                    directionAnimations.put(dir, anim);
                } else {
                    final TextureRegion[] frames = getHurtFrames(baseTexture);
                    final Animation anim = new Animation(this.frameRate, frames);
                    directionAnimations.put(dir, anim);
                }
            }
            this.animations.put(mState, directionAnimations);
        }
    }

    public UniversalDirectionalSprite(final String imgName, final float frameRate, final AnimationState... availableAnimations) {
        this(new Texture(imgName), frameRate, availableAnimations);
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
            if (animationState == AnimationState.WALKING) {
                movementTime = 0;
                for (final AnimationState available : availableAnimations) {
                    if (available == AnimationState.IDLE) {
                        setAnimationState(AnimationState.IDLE);
                        break;
                    }
                }
            } else if (animationState != AnimationState.HURT) {
                final Map<Direction, Animation> stateAnimations = animations.get(animationState);
                final Animation animation = stateAnimations.get(currentDirection);
                if (animation.isAnimationFinished(movementTime)) {
                    movementTime = 0;
                    for (final AnimationState available : availableAnimations) {
                        if (available == AnimationState.IDLE) {
                            setAnimationState(AnimationState.IDLE);
                            break;
                        }
                    }
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

    public boolean isMoveable() {
        return animationState.isMoveable();
    }

    public boolean isAnimationFinished() {
        final Map<Direction, Animation> stateAnimations = animations.get(animationState);
        final Animation animation = stateAnimations.get(currentDirection);
        return (animation.isAnimationFinished(movementTime));
    }

    public Texture getBaseTexture() {
        return baseTexture;
    }

    private static TextureRegion[] getHurtFrames(final Texture texture) {
        return getFrames(Direction.UP, AnimationState.HURT, texture);
    }

    private static TextureRegion[] getFrames(final Direction direction, final AnimationState animationState, final Texture texture) {
        final int section = animationState.getSectionIndex();
        final int rowInSection = direction.getRowInSection();
        final int numFrames = animationState.getNumFrames();

        final TextureRegion[] frames = new TextureRegion[numFrames];

        final int sectionOffset = section * 4 * SPRITE_HEIGHT;

        final int rowOffset = rowInSection * SPRITE_HEIGHT;

        final int y = sectionOffset + rowOffset;

        for (int i = 0; i < numFrames; ++i) {
            final int x = i * SPRITE_WIDTH;
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

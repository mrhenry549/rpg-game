/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.skittishSloth.rpgGame.engine.sprites;

/**
 *
 * @author mcory01
 */
public enum AnimationState {
    IDLE("idle", true, 1, -1),
    WALKING("walk", true, 9, 2),
    SLASHING("slash", false, 6, 3),
    SHOOTING("shoot", false, 13, 4),
    HURT("hurt", false, 6, 5),
    THRUSTING("thrust", false, 8, 1),
    CASTING("cast", false, 7, 0);
    
    private final String frameName;
    private final boolean loopable;
    private final int numFrames;
    private final int sectionIndex;
    
    private AnimationState(final String frameName, final boolean loopable, final int numFrames, final int sectionIndex) {
        this.frameName = frameName;
        this.loopable = loopable;
        this.numFrames = numFrames;
        this.sectionIndex = sectionIndex;
    }
    
    public String getFrameName() {
        return frameName;
    }
    
    public boolean isLoopable() {
        return loopable;
    }

    public int getNumFrames() {
        return numFrames;
    }

    public int getSectionIndex() {
        return sectionIndex;
    }
}

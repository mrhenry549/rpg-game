/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.skittishSloth.rpgGame.engine.player;

/**
 *
 * @author mcory01
 */
public enum Direction {
    NORTH(0f),
    NORTH_EAST(45f),
    EAST(90f),
    SOUTH_EAST(135f),
    SOUTH(180f),
    SOUTH_WEST(225f),
    WEST(270f),
    NORTH_WEST(315f);
    
    private final float angle;
    
    private Direction(final float angle) {
        this.angle = angle;
    }
    
    public float getAngle() {
        return angle;
    }
    
    public float getXDifference() {
        return 0.0f;
    }
    
    public float getYDifference() {
        return 0.0f;
    }
}

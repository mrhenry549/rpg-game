/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.skittishSloth.rpgGame.engine.player;

import com.github.skittishSloth.rpgGame.engine.common.Direction;

/**
 *
 * @author mcory01
 */
public class PositionInformation {
    
    public PositionInformation() {
        direction = Direction.UP;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(final Direction direction) {
        this.direction = direction;
    }
    
    public void setFrom(final PositionInformation other) {
        setX(other.getX());
        setY(other.getY());
        setDirection(other.getDirection());
    }
    
    private float x;
    private float y;
    private Direction direction;
}

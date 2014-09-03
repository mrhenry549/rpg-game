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

    public void setX(final float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(final float y) {
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Float.floatToIntBits(this.x);
        hash = 71 * hash + Float.floatToIntBits(this.y);
        hash = 71 * hash + (this.direction != null ? this.direction.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (getClass() != obj.getClass()) {
            return false;
        }

        final PositionInformation other = (PositionInformation) obj;
        if (Float.floatToIntBits(this.x) != Float.floatToIntBits(other.x)) {
            return false;
        }
        
        if (Float.floatToIntBits(this.y) != Float.floatToIntBits(other.y)) {
            return false;
        }
        
        return (this.direction == other.direction);
    }

    private float x;
    private float y;
    private Direction direction;
}

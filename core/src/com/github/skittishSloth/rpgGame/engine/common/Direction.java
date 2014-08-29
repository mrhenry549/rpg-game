/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.skittishSloth.rpgGame.engine.common;

/**
 *
 * @author mcory01
 */
public enum Direction {
    UP("up_", 0),
    RIGHT("right_", 3),
    DOWN("down_", 2),
    LEFT("left_", 1);
    
    private final String prefix;
    private final int rowInSection;
    
    private Direction(final String prefix, final int rowInSection) {
        this.prefix = prefix;
        this.rowInSection = rowInSection;
    }
    
    public String getPrefix() {
        return prefix;
    }

    public int getRowInSection() {
        return rowInSection;
    }
    
}

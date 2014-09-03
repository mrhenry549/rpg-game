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
public enum PlayerSpriteType {
    BODY,
    HEAD,
    TORSO,
    PANTS,
    WEAPON;
    
    public static PlayerSpriteType getByOrdinal(final int ordinal) {
        for (final PlayerSpriteType pst : values()) {
            if (pst.ordinal() == ordinal) {
                return pst;
            }
        }
        
        return null;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.skittishSloth.rpgGame.engine.maps;

/**
 *
 * @author mcory01
 */
public class Transition {

    public Transition(final String mapName, final Integer index) {
        this.mapName = mapName;
        this.index = index;
        
        System.err.println("New transition: n: '" + mapName + "', index: '" + index + "'");
    }

    public String getMapName() {
        return mapName;
    }

    public Integer getIndex() {
        return index;
    }

    private final String mapName;
    private final Integer index;
}

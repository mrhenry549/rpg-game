/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.skittishSloth.rpgGame.engine.maps;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author mcory01
 */
public class TiledMapManager {
    
    public TiledMapManager() {
        
    }
    
    public void addMap(final String name, final String path) {
        final ManagedMap map = new ManagedMap(name, path);
        maps.put(name, map);
    }
    
    public ManagedMap getMap(final String name) {
        return maps.get(name);
    }

    public void dispose() {
        for (final ManagedMap map : maps.values()) {
            map.dispose();
        }
    }
    
    private final Map<String, ManagedMap> maps = new HashMap<String, ManagedMap>();
}

package com.github.skittishSloth.rpgGame;

import com.badlogic.gdx.Game;

public class RPGGame extends Game {

    private WorldScreen worldScreen;
    
    @Override
    public void create() {
        worldScreen = new WorldScreen();
        
        setScreen(worldScreen);
    }
    
    @Override
    public void dispose() {
    }
}

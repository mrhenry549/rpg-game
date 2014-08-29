package com.github.skittishSloth.rpgGame;

import com.badlogic.gdx.Game;
import com.github.skittishSloth.rpgGame.utils.AnimationDemoScreen;

public class RPGGame extends Game {

    private WorldScreen worldScreen;
    private AnimationDemoScreen animDemoScreen;
    
    @Override
    public void create() {
        worldScreen = new WorldScreen();
//        animDemoScreen = new AnimationDemoScreen();
        
        setScreen(worldScreen);
//        setScreen(animDemoScreen);
    }
    
    @Override
    public void dispose() {
        worldScreen.dispose();
//        animDemoScreen.dispose();
    }
}

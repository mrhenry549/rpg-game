/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.skittishSloth.rpgGame.engine.transitionEffects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 *
 * @author mcory01
 */
public class FadeInOutEffect {

    public FadeInOutEffect(final Stage stage) {
        this.stage = stage;
        final Pixmap pm = new Pixmap(800, 800, Pixmap.Format.RGBA8888);
        pm.setColor(Color.BLACK);
        pm.fillRectangle(0, 0, pm.getWidth(), pm.getHeight());
        final Texture t = new Texture(pm);
        pm.dispose();
        img = new Image(t);
        t.dispose();

        img.setColor(0, 0, 0, 0);
        img.setSize(stage.getWidth(), stage.getHeight());
        stage.addActor(img);
        img.addAction(Actions.alpha(1f, 0.5f));
        actor = new Actor();
    }
    
    public void runEffect() {
        runEffect(this.afterFadeOut, this.afterFadeIn);
    }

    public void runEffect(final Runnable afterFadeOut, final Runnable afterFadeIn) {
        actor.getActions().clear();

        actor.addAction(
                Actions.sequence(
                        Actions.delay(0.5f),
                        Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                if (afterFadeOut != null) {
                                    afterFadeOut.run();
                                }
                                
                                img.addAction(Actions.alpha(0f, 0.5f));
                            }
                        }),
                        Actions.delay(0.5f),
                        Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                stage.getActors().removeValue(actor, true);
                                stage.getActors().removeValue(img, true);
                                if (afterFadeIn != null) {
                                    afterFadeIn.run();
                                }
                            }
                        })));
        stage.addActor(actor);
    }

    public void setAfterFadeOut(final Runnable afterFadeOut) {
        this.afterFadeOut = afterFadeOut;
    }

    public void setAfterFadeIn(final Runnable afterFadeIn) {
        this.afterFadeIn = afterFadeIn;
    }

    private Runnable afterFadeOut, afterFadeIn;
    private final Stage stage;
    private final Actor actor;
    private final Image img;
}

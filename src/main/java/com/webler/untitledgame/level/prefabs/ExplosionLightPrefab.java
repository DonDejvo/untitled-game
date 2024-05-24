package com.webler.untitledgame.level.prefabs;

import com.webler.goliath.core.GameObject;
import com.webler.goliath.core.Scene;
import com.webler.goliath.graphics.Color;
import com.webler.goliath.graphics.light.SpotLight;
import com.webler.goliath.prefabs.Prefab;
import com.webler.goliath.time.Timer;
import com.webler.untitledgame.level.controllers.ExplosionLightController;

public class ExplosionLightPrefab implements Prefab {
    /**
    * Creates a GameObject to be used for explosion. You can override this method to create your own game object.
    * 
    * @param scene - The scene to create the game object in.
    * 
    * @return The newly created game object that will be used for explosion or null if there is no object
    */
    @Override
    public GameObject create(Scene scene) {
        GameObject go = new GameObject(scene);
        SpotLight light = new SpotLight(new Color(1, 0.75, 0.5), 1, 8);
        light.setIntensity(4);
        go.addComponent("Light", light);
        Timer timer = new Timer();
        go.addComponent("Timer", timer);
        go.addComponent("Controller", new ExplosionLightController(timer, light));
        return go;
    }
}

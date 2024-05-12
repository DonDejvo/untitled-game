package com.webler.untitledgame.prefabs.level;

import com.webler.goliath.core.GameObject;
import com.webler.goliath.core.Scene;
import com.webler.goliath.graphics.Color;
import com.webler.goliath.graphics.light.SpotLight;
import com.webler.goliath.prefabs.Prefab;
import com.webler.goliath.time.Timer;
import com.webler.untitledgame.level.controllers.ExplosionLightController;

public class ExplosionLightPrefab implements Prefab {
    @Override
    public GameObject create(Scene scene) {
        GameObject go = new GameObject(scene);
        SpotLight light = new SpotLight(new Color(1, 0.75, 0.55), 1, 2);
        light.setIntensity(5);
        go.addComponent("Light", light);
        Timer timer = new Timer();
        go.addComponent("Timer", timer);
        go.addComponent("Controller", new ExplosionLightController(timer, light));
        return go;
    }
}

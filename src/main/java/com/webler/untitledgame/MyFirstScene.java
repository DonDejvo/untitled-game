package com.webler.untitledgame;

import com.webler.goliath.Game;
import com.webler.goliath.core.GameObject;
import com.webler.goliath.core.Scene;
import com.webler.goliath.core.SceneParams;
import com.webler.goliath.graphics.Color;
import com.webler.goliath.graphics.light.AmbientLight;
import com.webler.goliath.graphics.light.SpotLight;
import com.webler.goliath.prefabs.PerspectiveCameraPrefab;

public class MyFirstScene extends Scene {
    public MyFirstScene(Game game) {
        super(game);
    }

    @Override
    public void init(SceneParams params) {
        GameObject perspectiveCamera = new PerspectiveCameraPrefab(Math.PI / 3, 0.1, 1000).create(this);
        perspectiveCamera.transform.position.z = 5;
        add(perspectiveCamera);

        GameObject cube = new CubePrefab().create(this);
        add(cube);

        GameObject light = new GameObject(this);
        light.transform.position.set(0, 0, 10);
        light.addComponent("Ambient", new AmbientLight(new Color(0.1, 0.1, 0.1)));
        light.addComponent("SpotLight", new SpotLight(Color.ORANGE, 25, 50));
        add(light);
    }
}

package com.webler.untitledgame;

import com.webler.goliath.core.GameObject;
import com.webler.goliath.core.Scene;
import com.webler.goliath.graphics.Texture;
import com.webler.goliath.graphics.components.MeshRenderer;
import com.webler.goliath.graphics.geometry.Cube;
import com.webler.goliath.graphics.geometry.Quad;
import com.webler.goliath.prefabs.Prefab;
import com.webler.goliath.utils.AssetPool;

public class CubePrefab implements Prefab {
    @Override
    public GameObject create(Scene scene) {
        GameObject g = new GameObject(scene);

        Texture maxwellCat = AssetPool.getTexture("assets/tiles/wall.png");
        g.addComponent("Renderer", new MeshRenderer(new Cube(maxwellCat.getTexId())));
        g.addComponent("Controller", new CubeController());

        return g;
    }
}

package com.webler.untitledgame.scenes;

import com.webler.goliath.Game;
import com.webler.goliath.core.SceneParams;
import com.webler.goliath.graphics.Color;
import com.webler.goliath.graphics.components.MeshRenderer;
import com.webler.goliath.graphics.geometry.Cube;
import com.webler.goliath.graphics.light.AmbientLight;
import com.webler.goliath.graphics.light.SpotLight;
import com.webler.goliath.prefabs.PerspectiveCameraPrefab;
import com.webler.goliath.utils.AssetPool;
import com.webler.goliath.core.GameObject;
import com.webler.goliath.core.Scene;
import com.webler.goliath.logger.Logger;
import com.webler.untitledgame.components.*;
import org.joml.Vector3d;

public class Test extends Scene {
    public Test(Game game) {
        super(game);
    }

    @Override
    public void init(SceneParams params) {
        TestParams testParams = (TestParams) params;

        Logger.log("This is test scene!", Logger.LEVEL_INFO);

        GameObject cameraGameObject = new PerspectiveCameraPrefab(Math.PI / 3, 0.1, 1000).create(this);
        add(cameraGameObject);

        GameObject levelGameObject = new GameObject(this, "Level");
        Level level = new Level();
        level.load(testParams.getLevelPath());
        levelGameObject.addComponent("Level", level);
        levelGameObject.addComponent("LevelRenderer", new LevelRenderer(level));
        add(levelGameObject);

//        GameObject maxwell = new GameObject(this);
//        MeshRenderer maxwellRenderer = new MeshRenderer(new Cube(AssetPool.getTexture("assets/images/maxwell.gif").getTexId()));
//        maxwellRenderer.setColor(new Color(1,1,0));
//        maxwell.addComponent("Maxwell", maxwellRenderer);
//        add(maxwell);
//        maxwell.transform.position.set(2, 1, 2);
//        maxwell.transform.scale.set(2);
//        maxwell.addComponent("Rotor", new MaxwellRotor());

    }
}

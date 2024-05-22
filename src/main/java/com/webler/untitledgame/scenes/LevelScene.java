package com.webler.untitledgame.scenes;

import com.webler.goliath.Game;
import com.webler.goliath.core.SceneParams;
import com.webler.goliath.dialogs.components.DialogManager;
import com.webler.goliath.graphics.Color;
import com.webler.goliath.graphics.components.Camera;
import com.webler.goliath.graphics.components.CameraMovement;
import com.webler.goliath.prefabs.PerspectiveCameraPrefab;
import com.webler.goliath.core.GameObject;
import com.webler.goliath.core.Scene;
import com.webler.goliath.utils.FPSCounter;
import com.webler.untitledgame.components.*;

public class LevelScene extends Scene {
    public LevelScene(Game game) {
        super(game);
    }

    /**
    * Called when the scene is loaded. This is where you can set up your scene by calling SceneParams#setScene ( Scene )
    * 
    * @param params - parameters used to load
    */
    @Override
    public void init(SceneParams params) {
        LevelParams levelParams = (LevelParams) params;

        getGame().getRenderer().lightOn = true;
        getGame().getRenderer().fogOn = true;

        GameObject cameraGameObject = new PerspectiveCameraPrefab(Math.PI / 3, 0.1, 1000).create(this);
        cameraGameObject.getComponent(Camera.class, "Camera").setBackgroundColor(Color.BLUE);
        cameraGameObject.addComponent("CameraMovement", new CameraMovement(getCamera(), 40));
        add(cameraGameObject);

        DialogManager dialogManager = new DialogManager();
        dialogManager.loadDialogs("untitled-game/dialogs/default.csv");

        GameObject levelGameObject = new GameObject(this, "Level");
        Level level = new Level();
        level.load(levelParams.getLevelPath());
        levelGameObject.addComponent("Level", level);
        levelGameObject.addComponent("LevelRenderer", new LevelRenderer(level));
        levelGameObject.addComponent("DialogManager", dialogManager);
        levelGameObject.addComponent("FPSCounter", new FPSCounter());
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

package com.webler.untitledgame.scenes;

import com.webler.goliath.Game;
import com.webler.goliath.core.SceneParams;
import com.webler.goliath.dialogs.Dialog;
import com.webler.goliath.dialogs.components.DialogManager;
import com.webler.goliath.graphics.Color;
import com.webler.goliath.graphics.Spritesheet;
import com.webler.goliath.graphics.Texture;
import com.webler.goliath.graphics.components.Camera;
import com.webler.goliath.prefabs.PerspectiveCameraPrefab;
import com.webler.goliath.core.GameObject;
import com.webler.goliath.core.Scene;
import com.webler.goliath.utils.AssetPool;
import com.webler.untitledgame.components.*;

public class LevelScene extends Scene {
    public LevelScene(Game game) {
        super(game);
    }

    @Override
    public void init(SceneParams params) {
        LevelParams levelParams = (LevelParams) params;

        getGame().getRenderer().lightOn = true;
        getGame().getRenderer().fogOn = true;

        Texture potionsTexture = AssetPool.getTexture("assets/tiles/potions.png");
        AssetPool.addSpritesheet("assets/tiles/potions.png", new Spritesheet(potionsTexture, 16, 16, 3, 3));

        GameObject cameraGameObject = new PerspectiveCameraPrefab(Math.PI / 3, 0.1, 1000).create(this);
        cameraGameObject.getComponent(Camera.class, "Camera").setBackgroundColor(Color.BLUE);
        add(cameraGameObject);

        DialogManager dialogManager = new DialogManager();
        dialogManager.addDialog("maid_chan__first", new Dialog("This is first part of answer", "Maid Chan"));
        dialogManager.addDialog("you__confusion", new Dialog("What is going on?", null));
        dialogManager.addDialog("maid_chan__second", new Dialog("This is second part of the answer. This one is a little bit longer.", "Maid Chan"));

        dialogManager.addDialog("maid_chan__no-repeat", new Dialog("This answer can be seen only once", "Maid Chan"));

        dialogManager.addDialog("you__no_key", new Dialog("I need find a key first", null));

        GameObject levelGameObject = new GameObject(this, "Level");
        Level level = new Level();
        level.load(levelParams.getLevelPath());
        levelGameObject.addComponent("Level", level);
        levelGameObject.addComponent("LevelRenderer", new LevelRenderer(level));
        levelGameObject.addComponent("DialogManager", dialogManager);
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

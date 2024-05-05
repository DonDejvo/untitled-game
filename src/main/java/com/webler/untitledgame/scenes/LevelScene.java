package com.webler.untitledgame.scenes;

import com.webler.goliath.Game;
import com.webler.goliath.core.SceneParams;
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

        Texture tilemapTexture = AssetPool.getTexture("untitled-game/spritesheets/tileset.png");
        AssetPool.addSpritesheet("untitled-game/spritesheets/tileset.png",
                new Spritesheet(tilemapTexture, 16, 16, 70, 9, 2, 2));

        Texture catgirlsTexture = AssetPool.getTexture("untitled-game/spritesheets/catgirls.png");
        AssetPool.addSpritesheet("untitled-game/spritesheets/catgirls.png",
                new Spritesheet(catgirlsTexture, 66, 86, 107, 12, 0, 0));

        GameObject cameraGameObject = new PerspectiveCameraPrefab(Math.PI / 3, 0.1, 1000).create(this);
        cameraGameObject.getComponent(Camera.class, "Camera").setBackgroundColor(Color.BLUE);
        add(cameraGameObject);

        DialogManager dialogManager = new DialogManager();
        dialogManager.loadDialogs("untitled-game/dialogs/default.csv");

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

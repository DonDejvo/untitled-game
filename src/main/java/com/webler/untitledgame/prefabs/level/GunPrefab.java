package com.webler.untitledgame.prefabs.level;

import com.webler.goliath.core.GameObject;
import com.webler.goliath.core.Scene;
import com.webler.goliath.graphics.Sprite;
import com.webler.goliath.graphics.components.SpriteRenderer;
import com.webler.goliath.prefabs.Prefab;
import com.webler.untitledgame.components.Level;
import com.webler.untitledgame.components.LevelObject;
import com.webler.untitledgame.level.controllers.AssaultRifleController;
import com.webler.untitledgame.level.controllers.ShotgunController;

public class GunPrefab implements Prefab {
    private final Level level;
    private final LevelObject levelObject;

    public GunPrefab(Level level, String identifier) {
        this.level = level;
        this.levelObject = level.getRegisteredObject(identifier);
    }

    /**
    * Creates a GameObject based on the level object. This is called when the game starts or when the level is changed
    * 
    * @param scene - The scene to create the game object in
    * 
    * @return The game object that was created or null if there was an error creating the level object ( in which case the error will be logged
    */
    @Override
    public GameObject create(Scene scene) {
        GameObject go = new GameObject(scene);
        Sprite sprite = new Sprite(levelObject.getSprite());
        sprite.setWidth((int)(Level.TILE_SIZE * levelObject.getScale().x));
        sprite.setHeight((int)(Level.TILE_SIZE * levelObject.getScale().y));
        SpriteRenderer renderer = new SpriteRenderer(sprite, -1);
        go.addComponent("Renderer", renderer);
        // This method is called by the levelObject. getIdentifier method.
        switch (levelObject.getIdentifier()) {
            case "ak47": {
                go.addComponent("Controller", new AssaultRifleController(level, levelObject.getIdentifier()));
                break;
            }
            case "shotgun": {
                go.addComponent("Controller", new ShotgunController(level, levelObject.getIdentifier()));
                break;
            }
        }
        return go;
    }
}

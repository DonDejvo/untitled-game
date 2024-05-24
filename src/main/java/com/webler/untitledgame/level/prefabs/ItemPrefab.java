package com.webler.untitledgame.level.prefabs;

import com.webler.goliath.colliders.BoxCollider3D;
import com.webler.goliath.core.GameObject;
import com.webler.goliath.core.Scene;
import com.webler.goliath.graphics.Sprite;
import com.webler.goliath.graphics.components.Billboard;
import com.webler.goliath.graphics.components.SpriteRenderer;
import com.webler.goliath.prefabs.Prefab;
import com.webler.untitledgame.level.Level;
import com.webler.untitledgame.level.controllers.entity.ItemController;
import com.webler.untitledgame.level.objects.LevelObject;
import org.joml.Vector3d;

public class ItemPrefab implements Prefab {
    private final Level level;
    private final LevelObject levelItem;

    public ItemPrefab(Level level, LevelObject levelItem) {
        this.level = level;
        this.levelItem = levelItem;
    }

    @Override
    public GameObject create(Scene scene) {
        GameObject go = new GameObject(scene);

        Sprite sprite = new Sprite(levelItem.getSprite());
        sprite.setWidth((int)(Level.TILE_SIZE * levelItem.getScale().x));
        sprite.setHeight((int)(Level.TILE_SIZE * levelItem.getScale().y));
        SpriteRenderer renderer = new SpriteRenderer(sprite, -1);

        go.addComponent("Renderer", renderer);
        go.addComponent("Bilboard", new Billboard());

        BoxCollider3D collider = new BoxCollider3D(new Vector3d(1, 1, 1));
        go.addComponent("Collider", collider);
        go.addComponent("Controller", new ItemController(level, levelItem.getIdentifier(), collider));

        return go;
    }
}

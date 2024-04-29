package com.webler.untitledgame.prefabs.editor;

import com.webler.goliath.core.GameObject;
import com.webler.goliath.core.Scene;
import com.webler.goliath.graphics.Sprite;
import com.webler.goliath.graphics.Texture;
import com.webler.goliath.graphics.components.SpriteRenderer;
import com.webler.goliath.prefabs.Prefab;
import com.webler.goliath.utils.AssetPool;
import com.webler.untitledgame.editor.EditorComponent;
import com.webler.untitledgame.editor.controllers.EntityEditorController;
import com.webler.untitledgame.level.levelmap.Entity;

public class EntityPrefab implements Prefab {
    private Entity entity;
    private int tileWidth, tileHeight;

    public EntityPrefab(Entity entity, int tileWidth, int tileHeight) {
        this.entity = entity;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
    }

    @Override
    public GameObject create(Scene scene) {
        Texture texture = switch (entity.name) {
            case "player" -> AssetPool.getTexture("assets/tiles/player.png");
            default -> throw new IllegalStateException("Unexpected value: " + entity.name);
        };
        Sprite sprite = new Sprite(texture);
        sprite.setWidth(tileWidth);
        sprite.setHeight(tileHeight);
        GameObject go = new GameObject(scene);
        go.tags.add(Entity.TAG);
        go.tags.add(EditorComponent.SELECTABLE_TAG);
        go.addComponent("Renderer", new SpriteRenderer(sprite, 50));
        go.addComponent("Controller", new EntityEditorController(entity));
        return go;
    }
}

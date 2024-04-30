package com.webler.untitledgame.prefabs.editor;

import com.webler.goliath.core.GameObject;
import com.webler.goliath.core.Scene;
import com.webler.goliath.graphics.Sprite;
import com.webler.goliath.graphics.components.SpriteRenderer;
import com.webler.goliath.prefabs.Prefab;
import com.webler.goliath.utils.AssetPool;
import com.webler.untitledgame.editor.EditorComponent;
import com.webler.untitledgame.editor.controllers.DoorEditorController;
import com.webler.untitledgame.level.levelmap.Door;

public class DoorPrefab implements Prefab {

    private Door door;
    private int tileWidth, tileHeight;

    public DoorPrefab(Door door, int tileWidth, int tileHeight) {
        this.door = door;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
    }

    @Override
    public GameObject create(Scene scene) {
        Sprite sprite = new Sprite(AssetPool.getTexture("assets/tiles/door.png"));
        sprite.setWidth(tileWidth);
        sprite.setHeight(tileHeight);
        GameObject gameObject = new GameObject(scene);
        gameObject.tags.add(Door.TAG);
        gameObject.tags.add(EditorComponent.SELECTABLE_TAG);
        gameObject.addComponent("Controller", new DoorEditorController(door));
        SpriteRenderer renderer = new SpriteRenderer(sprite, 30);
        renderer.offset.set(sprite.getWidth() * 0.5, sprite.getHeight() * 0.5, 0);
        gameObject.addComponent("Renderer", renderer);
        return gameObject;
    }
}

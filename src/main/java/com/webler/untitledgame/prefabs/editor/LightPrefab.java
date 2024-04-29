package com.webler.untitledgame.prefabs.editor;

import com.webler.goliath.core.GameObject;
import com.webler.goliath.core.Scene;
import com.webler.goliath.graphics.Sprite;
import com.webler.goliath.graphics.components.SpriteRenderer;
import com.webler.goliath.prefabs.Prefab;
import com.webler.goliath.utils.AssetPool;
import com.webler.untitledgame.editor.EditorComponent;
import com.webler.untitledgame.editor.controllers.LightEditorController;
import com.webler.untitledgame.level.levelmap.Light;

public class LightPrefab implements Prefab {
    private Light light;
    private int tileWidth, tileHeight;

    public LightPrefab(Light light, int tileWidth, int tileHeight) {
        this.light = light;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
    }

    @Override
    public GameObject create(Scene scene) {
        Sprite sprite = new Sprite(AssetPool.getTexture("assets/tiles/torch.png"));
        sprite.setWidth(tileWidth);
        sprite.setHeight(tileHeight);
        GameObject go = new GameObject(scene);
        go.transform.scale.set(0.5);
        go.tags.add(Light.TAG);
        go.tags.add(EditorComponent.SELECTABLE_TAG);
        go.addComponent("Renderer", new SpriteRenderer(sprite, 100));
        go.addComponent("Controller", new LightEditorController(light));
        return go;
    }
}

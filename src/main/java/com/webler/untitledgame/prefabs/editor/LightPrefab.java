package com.webler.untitledgame.prefabs.editor;

import com.webler.goliath.core.GameObject;
import com.webler.goliath.core.Scene;
import com.webler.goliath.graphics.Sprite;
import com.webler.goliath.graphics.Spritesheet;
import com.webler.goliath.graphics.components.SpriteRenderer;
import com.webler.goliath.prefabs.Prefab;
import com.webler.goliath.utils.AssetPool;
import com.webler.untitledgame.editor.EditorComponent;
import com.webler.untitledgame.editor.controllers.LightEditorController;
import com.webler.untitledgame.level.levelmap.Light;

public class LightPrefab implements Prefab {
    private Light light;
    private EditorComponent editorComponent;

    public LightPrefab(EditorComponent editorComponent, Light light) {
        this.editorComponent = editorComponent;
        this.light = light;
    }

    @Override
    public GameObject create(Scene scene) {
        Sprite sprite = editorComponent.getLevel().getSprite("light");
        sprite.setWidth(editorComponent.getConfig().gridWidth());
        sprite.setHeight(editorComponent.getConfig().gridHeight());
        GameObject go = new GameObject(scene);
        go.transform.scale.set(0.5);
        go.tags.add(Light.TAG);
        go.tags.add(EditorComponent.SELECTABLE_TAG);
        go.addComponent("Renderer", new SpriteRenderer(sprite, 100));
        go.addComponent("Controller", new LightEditorController(editorComponent, light));
        return go;
    }
}

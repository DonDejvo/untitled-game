package com.webler.untitledgame.prefabs.editor;

import com.webler.goliath.core.GameObject;
import com.webler.goliath.core.Scene;
import com.webler.goliath.graphics.Sprite;
import com.webler.goliath.graphics.components.SpriteRenderer;
import com.webler.goliath.prefabs.Prefab;
import com.webler.goliath.utils.AssetPool;
import com.webler.untitledgame.editor.EditorComponent;
import com.webler.untitledgame.editor.controllers.PlatformEditorController;
import com.webler.untitledgame.level.levelmap.Platform;

public class PlatformPrefab implements Prefab {
    private Platform platform;
    private EditorComponent editorComponent;

    public PlatformPrefab(EditorComponent editorComponent, Platform platform) {
        this.editorComponent = editorComponent;
        this.platform = platform;
    }

    @Override
    public GameObject create(Scene scene) {
        Sprite sprite = new Sprite(AssetPool.getTexture("assets/tiles/ceiling.png"));
        sprite.setWidth(editorComponent.getConfig().gridWidth());
        sprite.setHeight(editorComponent.getConfig().gridHeight());
        GameObject gameObject = new GameObject(scene);
        gameObject.tags.add(Platform.TAG);
        gameObject.tags.add(EditorComponent.SELECTABLE_TAG);
        gameObject.addComponent("Controller", new PlatformEditorController(editorComponent, platform));
        SpriteRenderer renderer = new SpriteRenderer(sprite, 0);
        renderer.offset.set(sprite.getWidth() * 0.5, sprite.getHeight() * 0.5, 0);
        gameObject.addComponent("Renderer", renderer);
        return gameObject;
    }
}

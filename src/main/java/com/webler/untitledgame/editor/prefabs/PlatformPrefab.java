package com.webler.untitledgame.editor.prefabs;

import com.webler.goliath.core.GameObject;
import com.webler.goliath.core.Scene;
import com.webler.goliath.graphics.Sprite;
import com.webler.goliath.graphics.components.SpriteRenderer;
import com.webler.goliath.prefabs.Prefab;
import com.webler.untitledgame.editor.EditorComponent;
import com.webler.untitledgame.editor.controllers.PlatformEditorController;
import com.webler.untitledgame.level.levelmap.Platform;

public class PlatformPrefab implements Prefab {
    private final Platform platform;
    private final EditorComponent editorComponent;

    public PlatformPrefab(EditorComponent editorComponent, Platform platform) {
        this.editorComponent = editorComponent;
        this.platform = platform;
    }

    /**
    * Creates the game object. This is called by the editor when it is created. It will create a Sprite and add it to the scene
    * 
    * @param scene - The scene to add the GameObject to
    * 
    * @return The GameObject that was created for the editor's game object and the sprite renderer to be added
    */
    @Override
    public GameObject create(Scene scene) {
        Sprite sprite = new Sprite();
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

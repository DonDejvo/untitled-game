package com.webler.untitledgame.editor.prefabs;

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
    private final Light light;
    private final EditorComponent editorComponent;

    public LightPrefab(EditorComponent editorComponent, Light light) {
        this.editorComponent = editorComponent;
        this.light = light;
    }

    /**
    * Creates a GameObject that can be used to interact with the game. You can use this to create an instance of your game object without needing to create a new instance of the Scene you are using.
    * 
    * @param scene - The Scene to create the game object in.
    * 
    * @return The game object that can be used to interact with the game object without needing to create a new Scene
    */
    @Override
    public GameObject create(Scene scene) {
        Sprite sprite = AssetPool.getSpritesheet("untitled-game/spritesheets/tileset.png").getSprite(37);
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

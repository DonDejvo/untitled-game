package com.webler.untitledgame.prefabs.editor;

import com.webler.goliath.core.GameObject;
import com.webler.goliath.core.Scene;
import com.webler.goliath.graphics.Sprite;
import com.webler.goliath.graphics.components.SpriteRenderer;
import com.webler.goliath.prefabs.Prefab;
import com.webler.untitledgame.components.LevelObject;
import com.webler.untitledgame.editor.EditorComponent;
import com.webler.untitledgame.editor.controllers.EntityEditorController;
import com.webler.untitledgame.level.levelmap.Entity;

public class EntityPrefab implements Prefab {
    private final Entity entity;
    private final EditorComponent editorComponent;

    public EntityPrefab(EditorComponent editorComponent, Entity entity) {
        this.editorComponent = editorComponent;
        this.entity = entity;
    }

    /**
    * Creates a GameObject that can be used to interact with the entity. This is called by the Scene#create ( Scene ) method.
    * 
    * @param scene - The scene to create the game object in.
    * 
    * @return The game object that can be interacted with the entity in the Scene#create ( Scene ) method
    */
    @Override
    public GameObject create(Scene scene) {
        LevelObject levelObject = editorComponent.getLevel().getRegisteredObject(entity.getName());
        int tileWidth = editorComponent.getConfig().gridWidth();
        int tileHeight = editorComponent.getConfig().gridHeight();
        GameObject go = new GameObject(scene);
        go.tags.add(Entity.TAG);
        go.tags.add(EditorComponent.SELECTABLE_TAG);
        Sprite sprite = new Sprite(levelObject.getSprite());
        sprite.setWidth((int)(tileWidth * levelObject.getScale().x));
        sprite.setHeight((int)(tileHeight * levelObject.getScale().y));
        go.addComponent("Renderer", new SpriteRenderer(sprite, levelObject.getZIndex()));
        go.addComponent("Controller", new EntityEditorController(editorComponent, entity));
        return go;
    }
}

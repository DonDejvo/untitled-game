package com.webler.untitledgame.prefabs.editor;

import com.webler.goliath.core.GameObject;
import com.webler.goliath.core.Scene;
import com.webler.goliath.graphics.Sprite;
import com.webler.goliath.graphics.Spritesheet;
import com.webler.goliath.graphics.components.SpriteRenderer;
import com.webler.goliath.prefabs.Prefab;
import com.webler.goliath.utils.AssetPool;
import com.webler.untitledgame.components.LevelObject;
import com.webler.untitledgame.editor.EditorComponent;
import com.webler.untitledgame.editor.controllers.EntityEditorController;
import com.webler.untitledgame.level.levelmap.Entity;

public class EntityPrefab implements Prefab {
    private Entity entity;
    private EditorComponent editorComponent;

    public EntityPrefab(EditorComponent editorComponent, Entity entity) {
        this.editorComponent = editorComponent;
        this.entity = entity;
    }

    @Override
    public GameObject create(Scene scene) {
        LevelObject levelObject = editorComponent.getLevel().getRegisteredObject(entity.name);
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

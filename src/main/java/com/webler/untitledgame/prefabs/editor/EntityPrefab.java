package com.webler.untitledgame.prefabs.editor;

import com.webler.goliath.core.GameObject;
import com.webler.goliath.core.Scene;
import com.webler.goliath.graphics.Sprite;
import com.webler.goliath.graphics.Spritesheet;
import com.webler.goliath.graphics.components.SpriteRenderer;
import com.webler.goliath.prefabs.Prefab;
import com.webler.goliath.utils.AssetPool;
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
        int tileWidth = editorComponent.getConfig().gridWidth();
        int tileHeight = editorComponent.getConfig().gridHeight();
        GameObject go = new GameObject(scene);
        go.tags.add(Entity.TAG);
        go.tags.add(EditorComponent.SELECTABLE_TAG);
        Sprite sprite;
        switch (entity.name) {
            case "player": {
                sprite = editorComponent.getLevel().getSprite(entity.name);
                sprite.setWidth(tileWidth);
                sprite.setHeight(tileHeight);
                go.addComponent("Renderer", new SpriteRenderer(sprite, 20));
                break;
            }
            case "cat_girl_1", "cat_girl_2", "cat_girl_3": {
                sprite = editorComponent.getLevel().getSprite(entity.name);
                sprite.setWidth(tileWidth / 3 * 2);
                sprite.setHeight(tileHeight);
                go.addComponent("Renderer", new SpriteRenderer(sprite, 20));
                break;
            }
            case "vending_machine": {
                sprite = new Sprite(AssetPool.getTexture("untitled-game/images/vending_machine.png"));
                sprite.setWidth(tileWidth / 3 * 2);
                sprite.setHeight(tileHeight);
                go.addComponent("Renderer", new SpriteRenderer(sprite, 20));
                break;
            }
            case "key", "caffe_latte", "espresso", "americano": {
                sprite = editorComponent.getLevel().getSprite(entity.name);
                sprite.setWidth(tileWidth);
                sprite.setHeight(tileHeight);
                go.transform.scale.set(0.5);
                go.addComponent("Renderer", new SpriteRenderer(sprite, 50));
                break;
            }
            default:
                throw new IllegalStateException("Unexpected value: " + entity.name);
        }
        go.addComponent("Controller", new EntityEditorController(editorComponent, entity));
        return go;
    }
}

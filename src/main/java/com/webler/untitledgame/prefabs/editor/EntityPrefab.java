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
                sprite = new Sprite(AssetPool.getTexture("assets/tiles/player.png"));
                sprite.setWidth(tileWidth);
                sprite.setHeight(tileHeight);
                go.addComponent("Renderer", new SpriteRenderer(sprite, 20));
                break;
            }
            case "catgirl": {
                sprite = new Sprite(AssetPool.getTexture("assets/images/4-3.png"));
                sprite.setWidth((int)((double)tileHeight * sprite.getTexture().getWidth() / sprite.getTexture().getHeight()));
                sprite.setHeight(tileHeight);
                go.addComponent("Renderer", new SpriteRenderer(sprite, 20));
                break;
            }
            case "vendingmachine": {
                sprite = new Sprite(AssetPool.getTexture("assets/images/Vending_Machine_21.png"));
                sprite.setWidth((int)((double)tileHeight * sprite.getTexture().getWidth() / sprite.getTexture().getHeight()));
                sprite.setHeight(tileHeight);
                go.addComponent("Renderer", new SpriteRenderer(sprite, 20));
                break;
            }
            case "key": {
                sprite = new Sprite(AssetPool.getTexture("assets/tiles/key.png"));
                sprite.setWidth(tileWidth);
                sprite.setHeight(tileHeight);
                go.transform.scale.set(0.5);
                go.addComponent("Renderer", new SpriteRenderer(sprite, 50));
                break;
            }
            case "caffelatte": {
                sprite = AssetPool.getSpritesheet("assets/tiles/potions.png").getSprite(0);
                sprite.setWidth(tileWidth);
                sprite.setHeight(tileHeight);
                go.transform.scale.set(0.5);
                go.addComponent("Renderer", new SpriteRenderer(sprite, 50));
                break;
            }
            case "espresso": {
                sprite = AssetPool.getSpritesheet("assets/tiles/potions.png").getSprite(1);
                sprite.setWidth(tileWidth);
                sprite.setHeight(tileHeight);
                go.transform.scale.set(0.5);
                go.addComponent("Renderer", new SpriteRenderer(sprite, 50));
                break;
            }
            case "americano": {
                sprite = AssetPool.getSpritesheet("assets/tiles/potions.png").getSprite(2);
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

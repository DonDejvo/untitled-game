package com.webler.untitledgame.level.controllers;

import com.webler.goliath.colliders.BoxCollider3D;
import com.webler.goliath.core.GameObject;
import com.webler.goliath.graphics.Color;
import com.webler.goliath.graphics.components.SpriteRenderer;
import com.webler.untitledgame.components.Level;
import com.webler.untitledgame.level.inventory.InventoryItem;
import org.joml.Vector3d;

public class ItemController extends Controller {
    private String itemName;

    public ItemController(Level level, String itemName) {
        super(level, new BoxCollider3D(new Vector3d()));
        this.itemName = itemName;
    }

    @Override
    public void start() {
        level.addObjectToGroup(gameObject, "focusable");
    }

    @Override
    public void update(double dt) {
        SpriteRenderer renderer = getComponent(SpriteRenderer.class, "Renderer");
        renderer.setColor(isFocused() ? new Color(1.00, 1.00, 1.00) : new Color(0.5, 0.5, 0.5));
    }

    @Override
    public void destroy() {
        level.removeObjectFromGroup(gameObject, "focusable");
    }

    @Override
    protected void interact() {
        GameObject player = level.getPlayer();
        player.getComponent(PlayerController.class, "Controller").collect(itemName);
        getEntity().getScene().remove(gameObject);
    }
}

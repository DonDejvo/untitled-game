package com.webler.untitledgame.level.controllers.entity;

import com.webler.goliath.colliders.BoxCollider3D;
import com.webler.goliath.core.GameObject;
import com.webler.goliath.graphics.Color;
import com.webler.goliath.graphics.components.SpriteRenderer;
import com.webler.untitledgame.level.Level;
import com.webler.untitledgame.level.controllers.Controller;
import org.joml.Vector3d;

public class ItemController extends EntityController {
    private final String itemName;

    public ItemController(Level level, String itemName, BoxCollider3D boxCollider3D) {
        super(level, boxCollider3D, new String[]{}, null, 0, 0);
        this.itemName = itemName;
    }

    /**
    * Called when the player starts. This is where we add the GameObject to the level's group
    */
    @Override
    public void start() {
        level.addObjectToGroup(gameObject, "focusable");
    }

    /**
    * Updates the color of the SpriteRenderer. This is called every frame to indicate whether or not the user has focus
    * 
    * @param dt - time since the last
    */
    @Override
    public void update(double dt) {
        SpriteRenderer renderer = getComponent(SpriteRenderer.class, "Renderer");
        renderer.setColor(isFocused() ? new Color(1.00, 1.00, 1.00) : new Color(0.5, 0.5, 0.5));

        updatePhysics(dt);
    }

    /**
    * Removes focusable from the level. This is called when the GameObject is no longer in use and should not be used
    */
    @Override
    public void destroy() {
        level.removeObjectFromGroup(gameObject, "focusable");
    }

    /**
    * Called when the player interacts with the game. This is where the logic is executed. It should return true if the player is able to interact with the game and false otherwise.
    * 
    * 
    * @return true if the player is able to interact with the game and false otherwise. In this case the player should return
    */
    @Override
    public boolean interact() {
        GameObject player = level.getPlayer();
        player.getComponent(PlayerController.class, "Controller").collect(itemName);
        getGameObject().getScene().remove(gameObject);
        return true;
    }

    /**
    * Returns the name of the item. This is used to distinguish items that have been added to the Level and are no longer part of the hierarchy.
    * 
    * 
    * @return the name of the item or null if there is no such item in the Level's hierarchy or if the level doesn't have a
    */
    @Override
    public String getName() {
        return level.getRegisteredObject(itemName).getName();
    }
}

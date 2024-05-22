package com.webler.untitledgame.level.controllers;

import com.webler.goliath.colliders.BoxCollider3D;
import com.webler.goliath.core.Component;
import com.webler.goliath.core.GameObject;
import com.webler.untitledgame.components.Level;
import org.joml.Vector2d;
import org.joml.Vector3d;

public abstract class Controller extends Component {
    protected double yaw, pitch;
    protected Level level;
    protected BoxCollider3D collider;

    public Controller(Level level, BoxCollider3D collider) {
        this.level = level;
        this.collider = collider;
        yaw = 0.0;
        pitch = 0.0;
    }

    /**
    * Returns the center of the object. This is used to determine where the object is in the world and when moving to a new position.
    * 
    * 
    * @return The center of the object in world coordinates ( Vector3d ). Note that it is a reference to the GameObject
    */
    protected Vector3d getCenter() {
        return gameObject.transform.position;
    }

    /**
    * Checks if the player is in front of the player. This is used to prevent attacking a player when they're out of the game.
    * 
    * 
    * @return true if the player is in front of the player false otherwise. Note that it's a good idea to call this in a synchronized
    */
    protected boolean isInFrontOfPlayer() {
        GameObject player = level.getPlayer();

        Vector3d playerDirection = new Vector3d(1, 0, 0);
        playerDirection.rotateY(player.getComponent(PlayerController.class, "Controller").yaw);
        playerDirection.normalize();

        Vector3d center = getCenter();
        Vector2d directionToObject = new Vector2d(center.x, center.z).sub(player.transform.position.x, player.transform.position.z).normalize();
        double distance = new Vector2d(player.transform.position.x, player.transform.position.z).sub(center.x, center.z).lengthSquared();
        return distance > 0.1 && distance < 9 * 9 && Math.abs(player.transform.position.y - center.y) < 8 &&
                new Vector2d(playerDirection.x, playerDirection.z).dot(directionToObject) > 0.9;
    }

    /**
    * Called when the user interacts with the dialog. This is the default implementation of the AbstractDialog#interact ( java. lang. Object ) method but subclasses may override this method to provide custom behavior.
    * 
    * 
    * @return true if the dialog should be interacted with false otherwise ( usually by returning false ). Subclasses should override this method in order to return true
    */
    protected boolean interact() {
        return false;
    }

    /**
    * Checks if the player is focused. This is used to prevent accidental flickering when switching to a different player.
    * 
    * 
    * @return true if the player is focused false otherwise ( player is null or not in the gameObject's controller
    */
    protected boolean isFocused() {
        GameObject player = level.getPlayer();
        // Returns true if the player is currently focused.
        if(player != null) {
            PlayerController playerController = player.getComponent(PlayerController.class, "Controller");
            return playerController.getFocusedObject() == gameObject;
        }
        return false;
    }

    /**
    * Gets the position of the focus. It is used to determine where the focus is in the scene.
    * 
    * 
    * @return The position of the focus in world coordinates or Vector3d. NaN if there is no focus on the
    */
    public Vector3d getFocusPosition() {
        return gameObject.transform.position;
    }

    /**
    * Returns the name of this entity. This is used to distinguish entities from other entities that are in the same entity group.
    * 
    * 
    * @return the name of this entity or null if there is no name ( for example if this entity is a group
    */
    public abstract String getName();
}

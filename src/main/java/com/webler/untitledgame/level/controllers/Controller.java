package com.webler.untitledgame.level.controllers;

import com.webler.goliath.colliders.BoxCollider3D;
import com.webler.goliath.core.Component;
import com.webler.goliath.core.GameObject;
import com.webler.untitledgame.components.Level;
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

    protected Vector3d getCenter() {
        return gameObject.transform.position;
    }

    protected boolean isInFrontOfPlayer() {
        GameObject player = level.getPlayer();

        Vector3d playerDirection = new Vector3d(1, 0, 0);
        playerDirection.rotateY(player.getComponent(PlayerController.class, "Controller").yaw);
        playerDirection.normalize();

        Vector3d center = getCenter();
        Vector3d directionToObject = new Vector3d(center).sub(player.transform.position).normalize();
        double distance = player.transform.position.distance(center);
        return distance > 0.1 && distance < 5 &&
                playerDirection.dot(directionToObject) > 0.9;
    }

    protected void interact() {}

    protected boolean isFocused() {
        GameObject player = level.getPlayer();
        if(player != null) {
            PlayerController playerController = player.getComponent(PlayerController.class, "Controller");
            return playerController.getFocusedObject() == gameObject;
        }
        return false;
    }

    public Vector3d getFocusPosition() {
        return gameObject.transform.position;
    }
}

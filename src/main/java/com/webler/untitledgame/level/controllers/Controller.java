package com.webler.untitledgame.level.controllers;

import com.webler.goliath.colliders.BoxCollider3D;
import com.webler.goliath.core.Component;
import com.webler.goliath.core.GameObject;
import com.webler.untitledgame.components.Level;
import org.joml.Vector3d;

public abstract class Controller extends Component {
    protected Level level;
    protected BoxCollider3D collider;

    public Controller(Level level, BoxCollider3D collider) {
        this.level = level;
        this.collider = collider;
    }

    protected abstract boolean isInFrontOfPlayer();

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

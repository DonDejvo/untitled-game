package com.webler.untitledgame.level.controllers;

import com.webler.goliath.colliders.BoxCollider3D;
import com.webler.untitledgame.components.Level;
import org.joml.Vector3d;

public class NailProjectileController extends ShellProjectileController {
    public NailProjectileController(Level level, BoxCollider3D collider, Vector3d direction) {
        super(level, collider, direction);
        speed = 50;
    }
}

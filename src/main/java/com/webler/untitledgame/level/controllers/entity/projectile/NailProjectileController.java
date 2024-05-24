package com.webler.untitledgame.level.controllers.entity.projectile;

import com.webler.goliath.colliders.BoxCollider3D;
import com.webler.untitledgame.level.Level;
import org.joml.Vector3d;

public class NailProjectileController extends ShellProjectileController {
    public NailProjectileController(Level level, BoxCollider3D collider, Vector3d direction) {
        super(level, collider, direction);
        speed = 100;
        damage = 1;
    }
}

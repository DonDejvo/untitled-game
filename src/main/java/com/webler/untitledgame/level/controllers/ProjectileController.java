package com.webler.untitledgame.level.controllers;

import com.webler.goliath.colliders.BoxCollider3D;
import com.webler.untitledgame.components.Level;
import org.joml.Vector3d;

public class ProjectileController extends EntityController {
    protected Vector3d direction;

    public ProjectileController(Level level, BoxCollider3D collider, double speed, Vector3d direction) {
        super(level, collider, new String[]{ "fixed", "npc" }, null, speed);
        this.direction = direction;
    }

    @Override
    public void start() {

    }

    @Override
    public void update(double dt) {
        velocity.set(new Vector3d(direction).mul(speed));
        updatePhysics(dt);
    }

    @Override
    public void destroy() {

    }
}

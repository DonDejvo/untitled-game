package com.webler.untitledgame.level.controllers;

import com.webler.goliath.colliders.BoxCollider3D;
import com.webler.untitledgame.components.Level;
import org.joml.Vector3d;

public class ParticleController extends EntityController{
    private double lifetime;

    public ParticleController(Level level, BoxCollider3D collider, double speed, double lifetime, Vector3d direction) {
        super(level, collider, new String[]{ "fixed" }, null, speed);
        this.lifetime = lifetime;
        velocity.set(new Vector3d(direction).mul(speed));
        bounciness = 0.5;
        friction = 5;
    }

    @Override
    public void start() {

    }

    @Override
    public void update(double dt) {
        lifetime -= dt;
        if (lifetime <= 0) {
            gameObject.remove();
        }

        updatePhysics(dt);
    }

    @Override
    public void destroy() {

    }
}

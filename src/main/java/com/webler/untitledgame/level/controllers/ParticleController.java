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

    /**
    * Called when the server is started. This is where we start the web server and the server's state is maintained
    */
    @Override
    public void start() {

    }

    /**
    * Updates the physics state. This is called every frame to update the physics state. If the lifetime is less than zero the object is removed from the game.
    * 
    * @param dt - Time since the last update in seconds ( ignored
    */
    @Override
    public void update(double dt) {
        lifetime -= dt;
        // Removes the game object from the game object if it is not already in the lifetime.
        if (lifetime <= 0) {
            gameObject.remove();
        }

        updatePhysics(dt);
    }

    /**
    * Called when the component is no longer needed. This is the place to do any cleanup that needs to be done
    */
    @Override
    public void destroy() {

    }

    /**
    * Returns the name of this entity. This is used to identify the entity in error messages. If the entity does not have a name an empty string is returned.
    * 
    * 
    * @return the name of this entity or an empty string if there is no name in the entity's name
    */
    @Override
    public String getName() {
        return "";
    }
}

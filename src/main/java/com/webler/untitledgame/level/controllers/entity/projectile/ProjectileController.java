package com.webler.untitledgame.level.controllers.entity.projectile;

import com.webler.goliath.colliders.BoxCollider3D;
import com.webler.untitledgame.level.Level;
import com.webler.untitledgame.level.controllers.entity.EntityController;
import org.joml.Vector3d;

public class ProjectileController extends EntityController {
    protected Vector3d direction;
    protected int damage;

    public ProjectileController(Level level, BoxCollider3D collider, double speed, Vector3d direction, int damage) {
        super(level, collider, new String[]{ "fixed", "enemy", "npc" }, null, 0, speed);
        this.direction = direction;
        this.damage = damage;
    }

    /**
    * Called when the server is started. This is where we start the web server and the server's state is maintained
    */
    @Override
    public void start() {

    }

    /**
    * Updates physics for this object. This is called every frame by the game engine. You don't need to call this yourself.
    * 
    * @param dt - Time since the last update in seconds ( ignored
    */
    @Override
    public void update(double dt) {
        velocity.set(new Vector3d(direction).mul(speed));
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

package com.webler.untitledgame.level.controllers;

import com.webler.goliath.colliders.BoxCollider3D;
import com.webler.goliath.core.GameObject;
import com.webler.untitledgame.components.Level;
import org.joml.Vector3d;

import java.util.List;

public abstract class EntityController extends Controller {
    private Level level;
    protected BoxCollider3D collider;
    protected Vector3d velocity;
    protected Vector3d acceleration;
    protected double friction;
    protected double gravity;
    protected double bounciness;
    protected double yaw, pitch;
    protected boolean onGround;

    public EntityController(Level level, BoxCollider3D collider) {
        this.level = level;
        velocity = new Vector3d();
        acceleration = new Vector3d();
        this.collider = collider;
        friction = 1;
        gravity = 25;
        bounciness = 0;
        yaw = 0.0;
        pitch = 0.0;
        onGround = false;
    }

    protected void updatePhysics(double dt) {
        acceleration.y = -gravity;

        double frameFriction = Math.min(friction * dt, 1);
        Vector3d frameAcceleration = new Vector3d(acceleration).mul(dt);
        Vector3d decceleration = new Vector3d(velocity).mul(frameFriction, 0, frameFriction);
        velocity.add(frameAcceleration.sub(decceleration));

        Vector3d moveVec = new Vector3d(velocity).mul(dt);
        int steps = (int)Math.ceil(moveVec.length() / 4);
        Vector3d moveStep = moveVec.div(steps);

        for (int i = 0; i < steps; ++i) {
            Vector3d lastPosition = new Vector3d(gameObject.transform.position);

            gameObject.transform.position.x += moveStep.x;

            if(collides()) {

                gameObject.transform.position.x = lastPosition.x;
                velocity.x *= -bounciness;

                i = steps;
            }

            gameObject.transform.position.z += moveStep.z;

            if(collides()) {
                gameObject.transform.position.z = lastPosition.z;
                velocity.z *= -bounciness;

                i = steps;
            }

            gameObject.transform.position.y += moveStep.y;

            if(collides()) {
                gameObject.transform.position.y = lastPosition.y;
                onGround = velocity.y < 0;
                velocity.y = 0;

                i = steps;
            }
        }
    }

    private boolean collides() {
        List<GameObject> doorObjects = getEntity().getScene().getEntitiesByTag("door");
        for(GameObject doorObject : doorObjects) {
            BoxCollider3D otherCollider = doorObject.getComponent(BoxCollider3D.class, "Collider");
            if(collider.collidesWith(otherCollider)) {
                return true;
            }
        }

        return level.isBlockAtBox(collider.getMin(), collider.getMax());
    }
}

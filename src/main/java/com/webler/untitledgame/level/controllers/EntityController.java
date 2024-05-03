package com.webler.untitledgame.level.controllers;

import com.webler.goliath.colliders.BoxCollider3D;
import com.webler.goliath.core.GameObject;
import com.webler.untitledgame.components.Level;
import org.joml.Vector2d;
import org.joml.Vector3d;

import java.util.List;

public abstract class EntityController extends Controller {
    protected Vector3d velocity;
    protected Vector3d acceleration;
    protected double friction;
    protected double gravity;
    protected double bounciness;
    protected double yaw, pitch;
    protected boolean onGround;
    protected boolean isEnemy;

    public EntityController(Level level, BoxCollider3D collider, boolean isEnemy) {
        super(level, collider);
        velocity = new Vector3d();
        acceleration = new Vector3d();
        friction = 1;
        gravity = 25;
        bounciness = 0;
        yaw = 0.0;
        pitch = 0.0;
        onGround = false;
        this.isEnemy = isEnemy;
    }

    @Override
    protected boolean isInFrontOfPlayer() {
        GameObject player = level.getPlayer();
        if(player == null) {
            return false;
        }

        Vector3d playerDirection = new Vector3d(1, 0, 0);
        playerDirection.rotateY(player.getComponent(PlayerController.class, "Controller").yaw);
        playerDirection.normalize();

        Vector3d directionToObject = new Vector3d(gameObject.transform.position);
        directionToObject.sub(player.transform.position);
        directionToObject.normalize();
        return player.transform.position.distance(gameObject.transform.position) <= 6.000 &&
                playerDirection.dot(directionToObject) > 0.875;
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

//        List<GameObject> doorObjects = level.getFixedObjects();
//        for(GameObject doorObject : doorObjects) {
//            BoxCollider3D otherCollider = doorObject.getComponent(BoxCollider3D.class, "Collider");
//            if(collider.collidesWith(otherCollider)) {
//                Vector3d center = collider.getCenter();
//                Vector3d otherCenter = otherCollider.getCenter();
//
//                double deltaX = Math.max((collider.getSize().x + otherCollider.getSize().x) * 0.5
//                        - Math.abs(center.x - otherCenter.x), 0);
//                double deltaZ = Math.max((collider.getSize().z + otherCollider.getSize().z) * 0.5
//                        - Math.abs(center.z - otherCenter.z), 0);
//                if(deltaX > deltaZ) {
//                    gameObject.transform.position.x += Math.signum(center.x - otherCenter.x) * deltaX;
//                } else {
//                    gameObject.transform.position.z += Math.signum(center.z - otherCenter.z) * deltaZ;
//                }
//            }
//        }
    }

    private boolean collides() {

        List<GameObject> doorObjects = level.getFixedObjects();
        for(GameObject doorObject : doorObjects) {
            BoxCollider3D otherCollider = doorObject.getComponent(BoxCollider3D.class, "Collider");
            if(collider.collidesWith(otherCollider)) {
                return true;
            }
        }

        List<GameObject> checkedObjects = isEnemy ?
                level.getFriendEntityObjects() : level.getEnemyEntityObjects();
        Vector2d positionXY = new Vector2d(gameObject.transform.position.x, gameObject.transform.position.z);
        for(GameObject checkedObject : checkedObjects) {
            BoxCollider3D otherCollider = checkedObject.getComponent(BoxCollider3D.class, "Collider");
            Vector2d otherPositionXY = new Vector2d(checkedObject.transform.position.x, checkedObject.transform.position.z);
            if(positionXY.distance(otherPositionXY) < (collider.getSize().x + otherCollider.getSize().x) * 0.5) {
                return true;
            }
        }

        return level.isBlockAtBox(collider.getMin(), collider.getMax());
    }
}

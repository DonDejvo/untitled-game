package com.webler.untitledgame.level.controllers;

import com.webler.goliath.algorithm.Vertex;
import com.webler.goliath.colliders.BoxCollider3D;
import com.webler.goliath.core.GameObject;
import com.webler.untitledgame.components.Level;
import com.webler.untitledgame.components.PathFinder;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector3i;

import java.util.List;

public abstract class EntityController extends Controller {
    protected Vector3d velocity;
    protected Vector3d acceleration;
    protected double friction;
    protected double gravity;
    protected double bounciness;
    protected double yaw, pitch;
    protected boolean onGround;
    protected double speed;
    private String[] collisionGroups;
    protected PathFinder pathFinder;
    private int currentPathIdx;
    private Vector3d followTargetPos;
    protected double followTargetDistance;

    public EntityController(Level level, BoxCollider3D collider, String[] collisionGroups, PathFinder pathFinder, double speed) {
        super(level, collider);
        velocity = new Vector3d();
        acceleration = new Vector3d();
        friction = 1;
        gravity = 25;
        bounciness = 0;
        yaw = 0.0;
        pitch = 0.0;
        onGround = false;
        this.speed = speed;
        this.collisionGroups = collisionGroups;
        this.pathFinder = pathFinder;
        currentPathIdx = 0;
        followTargetPos = new Vector3d();
        followTargetDistance = 4;
    }

    protected void findPath(Vector3d targetPos) {
        pathFinder.calculatePath(targetPos);
        currentPathIdx = 0;
        followTargetPos.set(targetPos);
    }

    protected void followPath() {
        Vertex[] path = pathFinder.getPath();
        if(path == null || currentPathIdx >= path.length ||
                gameObject.transform.position.distance(followTargetPos) < followTargetDistance) {
            acceleration.x = 0;
            acceleration.z = 0;
            return;
        }

        int levelWidth = level.getLevelMap().getWidth();

        Vertex currentVertex = pathFinder.getPath()[currentPathIdx];
        int vertexId = currentVertex.getId();
        int coordX = vertexId % levelWidth;
        int coordY = vertexId / levelWidth;

        Vector3i blockCoords = level.getBlockCoords(gameObject.transform.position);

        if(coordX == blockCoords.x && coordY == blockCoords.z) {
            ++currentPathIdx;
        }

        Vector3d targetPos = new Vector3d(coordX + 0.5, 0, coordY + 0.5).mul(Level.TILE_SIZE);
        Vector3d direction = new Vector3d(targetPos.x, 0, targetPos.z)
                .sub(gameObject.transform.position)
                .normalize();

        acceleration.x = direction.x * speed;
        acceleration.z = direction.z * speed;
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

        for (String collisionGroup : collisionGroups) {
            List<GameObject> objects = level.getObjectsByGroup(collisionGroup);
            if (objects != null) {
                if (collisionGroup.equals("fixed")) {
                    for (GameObject doorObject : objects) {
                        BoxCollider3D otherCollider = doorObject.getComponent(BoxCollider3D.class, "Collider");
                        if (collider.collidesWith(otherCollider)) {
                            return true;
                        }
                    }
                } else {
                    Vector2d positionXY = new Vector2d(gameObject.transform.position.x, gameObject.transform.position.z);
                    for (GameObject checkedObject : objects) {
                        BoxCollider3D otherCollider = checkedObject.getComponent(BoxCollider3D.class, "Collider");
                        Vector2d otherPositionXY = new Vector2d(checkedObject.transform.position.x, checkedObject.transform.position.z);
                        if (positionXY.distance(otherPositionXY) < (collider.getSize().x + otherCollider.getSize().x) * 0.5) {
                            return true;
                        }
                    }
                }
            }
        }

        return level.isBlockAtBox(collider.getMin(), collider.getMax());
    }
}

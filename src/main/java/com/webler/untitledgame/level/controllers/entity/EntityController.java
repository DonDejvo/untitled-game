package com.webler.untitledgame.level.controllers.entity;

import com.webler.goliath.algorithm.Vertex;
import com.webler.goliath.colliders.BoxCollider3D;
import com.webler.goliath.core.GameObject;
import com.webler.goliath.graphics.Color;
import com.webler.goliath.graphics.DebugDraw;
import com.webler.untitledgame.level.Level;
import com.webler.untitledgame.level.ai.PathFinder;
import com.webler.untitledgame.level.controllers.CollisionInfo;
import com.webler.untitledgame.level.controllers.Controller;
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
    protected boolean onGround;
    protected int maxHp, hp;
    protected double speed;
    private final String[] collisionGroups;
    protected PathFinder pathFinder;
    private int currentPathIdx;
    private final Vector3d followTargetPos;
    protected double followTargetDistance;
    protected boolean keepOffLedges;

    public EntityController(Level level, BoxCollider3D collider, String[] collisionGroups, PathFinder pathFinder, int hp, double speed) {
        super(level, collider);
        velocity = new Vector3d();
        acceleration = new Vector3d();
        friction = 1;
        gravity = 40;
        bounciness = 0;
        onGround = false;
        this.maxHp = hp;
        this.hp = hp;
        this.speed = speed;
        this.collisionGroups = collisionGroups;
        this.pathFinder = pathFinder;
        currentPathIdx = 0;
        followTargetPos = new Vector3d();
        followTargetDistance = 8;
        keepOffLedges = false;
    }

    /**
    * Finds the path from the current position to the target position. This is called by the follow () method to get the path to the target
    * 
    * @param targetPos - The position to follow
    */
    protected void findPath(Vector3d targetPos) {
        pathFinder.calculatePath(targetPos);
        currentPathIdx = 0;
        followTargetPos.set(targetPos);
    }

    /**
    * Follow path and update acceleration based on it. This is called from followPath () and should be overridden if you want to change the acceleration
    */
    protected void followPath() {
        Vertex[] path = pathFinder.getPath();
        // If the path is not the same platform and the path is not the same platform.
        if((!pathFinder.isSamePlatform() && (path == null || currentPathIdx >= path.length)) ||
                gameObject.transform.position.distance(followTargetPos) < followTargetDistance) {
            acceleration.x = 0;
            acceleration.z = 0;
            return;
        }

        // If the path finder is the same platform then the acceleration is set to the speed of the game object.
        if(pathFinder.isSamePlatform()) {
            Vector3d direction = new Vector3d(followTargetPos.x, 0, followTargetPos.z)
                    .sub(new Vector3d(gameObject.transform.position.x, 0, gameObject.transform.position.z))
                    .normalize();

            acceleration.x = direction.x * speed;
            acceleration.z = direction.z * speed;
            return;
        }

        int levelWidth = level.getLevelMap().getWidth();

        Vertex currentVertex = pathFinder.getPath()[currentPathIdx];
        int vertexId = currentVertex.getId();
        int coordX = vertexId % levelWidth;
        int coordY = vertexId / levelWidth;

        Vector3d targetPos = new Vector3d(coordX + 0.5, level.getBlockTop(coordX, coordY), coordY + 0.5).mul(Level.TILE_SIZE);

        if(Math.pow(targetPos.x - gameObject.transform.position.x, 2) + Math.pow(targetPos.z - gameObject.transform.position.z, 2) < 0.25 * 0.25) {
            ++currentPathIdx;
        }

        Vector3d direction = new Vector3d(targetPos.x, 0, targetPos.z)
                .sub(new Vector3d(gameObject.transform.position.x, 0, gameObject.transform.position.z))
                .normalize();

        acceleration.x = direction.x * speed;
        acceleration.z = direction.z * speed;

        if(level.isDebug() && path.length != 0) {
            // Draws a line between two path.
            int firstVid = path[0].getId();
            int firstCx = firstVid % levelWidth;
            int firstCy = firstVid / levelWidth;
            Vector3d firstLineFrom = new Vector3d(gameObject.transform.position.x, targetPos.y, gameObject.transform.position.z);
            Vector3d firstLineTo = new Vector3d(firstCx + 0.5, level.getBlockTop(firstCx, firstCy), firstCy + 0.5).mul(Level.TILE_SIZE);
            DebugDraw.get().addLine(firstLineFrom, firstLineTo, Color.GREEN);
            for(int i = 1; i < path.length; ++i) {
                int vid1 = path[i - 1].getId();
                int cx1 = vid1 % levelWidth;
                int cy1 = vid1 / levelWidth;
                int vid2 = path[i].getId();
                int cx2 = vid2 % levelWidth;
                int cy2 = vid2 / levelWidth;
                Vector3d lineFrom = new Vector3d(cx1 + 0.5, level.getBlockTop(cx1, cy1), cy1 + 0.5).mul(Level.TILE_SIZE);
                Vector3d lineTo = new Vector3d(cx2 + 0.5, level.getBlockTop(cx2, cy2), cy2 + 0.5).mul(Level.TILE_SIZE);
                DebugDraw.get().addLine(lineFrom, lineTo, Color.GREEN);
            }
        }
    }

    /**
    * Updates physics for this object. This is called every frame to update the position of the object based on the time since the last frame.
    * 
    * @param dt - The amount of time in seconds since the last
    */
    protected void updatePhysics(double dt) {

        acceleration.y = -gravity;

        double frameFriction = friction * dt;
        Vector3d frameAcceleration = new Vector3d(acceleration).mul(dt);
        Vector3d decceleration = new Vector3d(velocity).mul(frameFriction, 0, frameFriction);
        velocity.add(frameAcceleration.sub(decceleration));

        Vector3d moveVec = new Vector3d(velocity).mul(dt);
        double stepDistance = 1.0;
        int steps = (int)Math.ceil(moveVec.length() / stepDistance);
        Vector3d moveStep = new Vector3d(moveVec).mul(1.0 / steps);

        // Moves the game object to the next step.
        for (int i = 0; i < steps; ++i) {
            Vector3d lastPosition = new Vector3d(gameObject.transform.position);

            gameObject.transform.position.x += moveStep.x;

            // If the game object collides with a collision this will move the game to the last position.
            if(collides(new Vector3d(1, 0, 0))) {

                gameObject.transform.position.x = lastPosition.x;
                velocity.x *= -bounciness;

                i = steps;
            }

            gameObject.transform.position.z += moveStep.z;

            // If the game object collides with a collision this will move the game to the last position.
            if(collides(new Vector3d(0, 0, 1))) {

                gameObject.transform.position.z = lastPosition.z;
                velocity.z *= -bounciness;

                i = steps;
            }

            gameObject.transform.position.y += moveStep.y;

            // If the game object collides with a collision this method will move the game object to the last position.
            if(collides(new Vector3d(0, 1, 0))) {

                gameObject.transform.position.y = lastPosition.y;
                onGround = velocity.y < 0;
                // Move the velocity to the right of the robot.
                if(velocity.y < -20) {
                    velocity.y *= -bounciness;
                } else {
                    velocity.y = 0;
                }

                i = steps;
            }
        }

        if(level.isDebug()) {
            DebugDraw.get().addBox(collider.getCenter(), collider.getSize(), Color.RED);
        }
    }

    /**
    * Checks to see if the collider collides with any doors. This is called by #didCollidesWithEntity ( Entity ) and should not be called directly by user code.
    * 
    * @param axis - The axis to check for collisions with. Used to determine if collision groups are fixed or non - fixed.
    * 
    * @return True if there is a collision false otherwise. Note that this returns true if the collision group is fixed
    */
    private boolean collides(Vector3d axis) {

        for (String collisionGroup : collisionGroups) {
            List<GameObject> objects = level.getObjectsByGroup(collisionGroup);
            // Checks if all the objects collides with this entity.
            if (objects != null) {
                // Returns true if the collision group is fixed or fixed.
                if (collisionGroup.equals("fixed")) {
                    for (GameObject doorObject : objects) {
                        BoxCollider3D otherCollider = doorObject.getComponent(BoxCollider3D.class, "Collider");
                        // Returns true if this entity collides with another entity.
                        if (collider.collidesWith(otherCollider)) {
                            didCollidesWithEntity(doorObject);
                            return true;
                        }
                    }
                } else {
                    Vector2d positionXY = new Vector2d(gameObject.transform.position.x, gameObject.transform.position.z);
                    for (GameObject checkedObject : objects) {
                        BoxCollider3D otherCollider = checkedObject.getComponent(BoxCollider3D.class, "Collider");
                        Vector2d otherPositionXY = new Vector2d(checkedObject.transform.position.x, checkedObject.transform.position.z);
                        // Check if the collision is within the collision s size.
                        if (positionXY.distance(otherPositionXY) < (collider.getSize().x + otherCollider.getSize().x) * 0.5 &&
                                Math.abs(gameObject.transform.position.y - checkedObject.transform.position.y) < (collider.getSize().y + otherCollider.getSize().y) * 0.5) {
                            didCollidesWithEntity(checkedObject);
                            return true;
                        }
                    }
                }
            }
        }

        // If the game object is a block at the box.
        if(onGround && keepOffLedges) {
            Vector3d vec = new Vector3d(gameObject.transform.position.x, collider.getMin().y - 2, gameObject.transform.position.z);
            // Check if the given vector is a block at the box.
            if(!level.isBlockAtBox(vec, vec)) {
                return true;
            }
        }

        boolean collidesWithBlock = level.isBlockAtBox(collider.getMin(), collider.getMax());

        // Called when the axis collides with a block.
        if (collidesWithBlock) {
            didCollides(axis);
        }

        return collidesWithBlock;
    }

    /**
    * Raycast from a position to a direction until it hits maxDist. This is useful for determining where to start a ray from
    * 
    * @param position - The position to start the raycast from
    * @param direction - The direction to raycast from ( must be normalized )
    * @param maxDist - The maximum distance to raycast from the position
    * @param step - The step between rays in each direction ( must be positive )
    * 
    * @return The position of the hit or null if no hit was found within maxDist or if the ray hits
    */
    protected CollisionInfo raycast(Vector3d position, Vector3d direction, double maxDist, double step, String[] collisionGroups) {
        double dist = 0;
        Vector3d vec = new Vector3d(direction).normalize().mul(step);
        Vector3d rayPosition = new Vector3d(position);
        GameObject[] collidingGameObject = new GameObject[1];
        // Find the next point in the ray.
        while(dist < maxDist) {
            rayPosition = rayPosition.add(vec);
            // Returns the ray position of the ray.
            if(collidesPoint(rayPosition, collidingGameObject, collisionGroups)) {
                return new CollisionInfo(rayPosition, direction, collidingGameObject[0]);
            }
            dist += step;
        }
        return null;
    }

    /**
    * Checks if the point collides with any door. This is used to determine if there is a collision or not
    * 
    * @param point - The point to check for collision
    * 
    * @return True if the point collides with any door false otherwise ( in this case we don't know
    */
    private boolean collidesPoint(Vector3d point, GameObject[] collidingGameObject, String[] collisionGroups) {
        for (String collisionGroup : collisionGroups) {
            List<GameObject> objects = level.getObjectsByGroup(collisionGroup);
            // Returns true if the collision group is fixed or fixed.
            if (objects != null) {
                // Returns true if the collision group is fixed or fixed.
                if (collisionGroup.equals("fixed")) {
                    for (GameObject doorObject : objects) {
                        BoxCollider3D otherCollider = doorObject.getComponent(BoxCollider3D.class, "Collider");
                        // Check if the point is in the other collider.
                        if (otherCollider.contains(point)) {
                            collidingGameObject[0] = doorObject;
                            return true;
                        }
                    }
                } else {
                    Vector2d positionXY = new Vector2d(point.x, point.z);
                    for (GameObject checkedObject : objects) {
                        BoxCollider3D otherCollider = checkedObject.getComponent(BoxCollider3D.class, "Collider");
                        Vector2d otherPositionXY = new Vector2d(checkedObject.transform.position.x, checkedObject.transform.position.z);
                        // Check if the positionXY distance between the otherCollider and the otherCollider is within the size of the transform.
                        if (positionXY.distance(otherPositionXY) < (otherCollider.getSize().x) * 0.5 &&
                                Math.abs(point.y - checkedObject.transform.position.y) < (otherCollider.getSize().y) * 0.5) {
                            collidingGameObject[0] = checkedObject;
                            return true;
                        }
                    }
                }
            }
        }
        collidingGameObject[0] = null;
        return level.isBlockAtBox(point, point);
    }

    /**
    * Called when collision is detected. This is a no - op if this entity does not collide with the axis.
    * 
    * @param axis - the axis that collides with this entity in 3
    */
    protected void didCollides(Vector3d axis) {}

    /**
    * Called when an entity collides with this entity. Subclasses may override this to provide behavior. By default this does nothing.
    * 
    * @param entity - GameObject whose collision has occurred. This is passed to the Collider
    */
    protected void didCollidesWithEntity(GameObject entity) {}
}

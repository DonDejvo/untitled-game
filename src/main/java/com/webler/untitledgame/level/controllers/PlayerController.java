package com.webler.untitledgame.level.controllers;

import com.webler.goliath.colliders.BoxCollider3D;
import com.webler.goliath.graphics.components.Camera;
import com.webler.goliath.input.Input;
import com.webler.untitledgame.components.Level;
import org.joml.Vector3d;

import static org.lwjgl.glfw.GLFW.*;

public class PlayerController extends EntityController {
    private Camera camera;
    private double speed;
    private double rotationSpeed;
    private boolean canJump;

    public PlayerController(Level level, Camera camera, BoxCollider3D collider) {
        super(level, collider);
        this.camera = camera;
        bounciness = 0;
        speed = 100;
        rotationSpeed = 2.5;
        canJump = true;
    }

    @Override
    public void start() {

    }

    @Override
    public void update(double dt) {
        Vector3d moveVec = new Vector3d();

        if(Input.keyPressed(GLFW_KEY_W)) {
            moveVec.x += 1;
        }
        if(Input.keyPressed(GLFW_KEY_S)) {
            moveVec.x -= 1;
        }
        if(Input.keyPressed(GLFW_KEY_Q)) {
            moveVec.z -= 1;
        }
        if(Input.keyPressed(GLFW_KEY_E)) {
            moveVec.z += 1;
        }
        moveVec.mul(speed * (onGround ? 1 : 0.3));

        if(Input.keyPressed(GLFW_KEY_LEFT_SHIFT) && onGround && moveVec.x > 0) {
            moveVec.x *= 1.5;
        }

        if(Input.keyPressed(GLFW_KEY_A)) {
            yaw += rotationSpeed * dt;
        }
        if(Input.keyPressed(GLFW_KEY_D)) {
            yaw -= rotationSpeed * dt;
        }

        moveVec.rotateY(yaw);
        acceleration.set(moveVec);

        if(Input.keyPressed(GLFW_KEY_SPACE) && onGround && canJump) {
            velocity.y = 15;
            onGround = false;
            canJump = false;
        }
        if(!Input.keyPressed(GLFW_KEY_SPACE)) {
            canJump = true;
        }

        Vector3d direction = new Vector3d(1, 0, 0);

        direction.rotateY(yaw);

        camera.getEntity().transform.position.set(new Vector3d(gameObject.transform.position).add(0, 1, 0));
        camera.direction.set(direction);

        friction = onGround ? 10 : 2.5;
        updatePhysics(dt);
    }

    @Override
    public void destroy() {

    }
}

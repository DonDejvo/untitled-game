package com.webler.untitledgame.level.controllers;

import com.webler.goliath.colliders.BoxCollider3D;
import com.webler.goliath.core.GameObject;
import com.webler.goliath.dialogs.events.DialogEnded;
import com.webler.goliath.eventsystem.listeners.EventHandler;
import com.webler.goliath.graphics.components.Camera;
import com.webler.goliath.input.Input;
import com.webler.untitledgame.components.Level;
import com.webler.untitledgame.level.events.DoorOpened;
import org.joml.Vector3d;

import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class PlayerController extends EntityController {
    private Camera camera;
    private double speed;
    private double rotationSpeed;
    private boolean canJump;
    private GameObject focusedObject;
    private State state;

    public PlayerController(Level level, Camera camera, BoxCollider3D collider) {
        super(level, collider, false);
        this.camera = camera;
        bounciness = 0;
        speed = 100;
        rotationSpeed = 2.5;
        canJump = true;
        focusedObject = null;
        state = State.IDLE;
    }

    public GameObject getFocusedObject() {
        return focusedObject;
    }

    @Override
    public void start() {
        level.getFriendEntityObjects().add(gameObject);
    }

    @Override
    public void update(double dt) {
        switch (state) {
            case IDLE:
                updateIdle(dt);
                break;
            case INTERACTING:
                updateInteracting(dt);
                break;
        }

        friction = onGround ? 10 : 2.5;
        updatePhysics(dt);
    }

    @Override
    public void destroy() {
        level.getFriendEntityObjects().remove(gameObject);
    }

    @EventHandler
    public void onDialogEnded(DialogEnded event) {
        stopInteraction();
    }

    @EventHandler
    public void onDoorOpened(DoorOpened event) {
        if(event.getDoor() == focusedObject) {
            stopInteraction();
        }
    }

    private void startInteraction() {
        if(focusedObject != null) {
            Controller controller = focusedObject.getComponent(Controller.class, "Controller");
            controller.interact();
            state = State.INTERACTING;
        }
    }

    private void stopInteraction() {
        state = State.IDLE;
    }

    private void updateIdle(double dt) {
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

        if(Input.keyBeginPress(GLFW_KEY_F)) {
            startInteraction();
        }

        Vector3d direction = new Vector3d(1, 0, 0);
        direction.rotateY(yaw);

        camera.getEntity().transform.position.set(new Vector3d(gameObject.transform.position).add(0, 1, 0));
        camera.direction.lerp(direction, Math.min(dt * 10, 1));

        updateFocusedObject();
    }

    private void updateInteracting(double dt) {

        Vector3d focusPosition = focusedObject.getComponent(Controller.class, "Controller").getFocusPosition();

        camera.getEntity().transform.position.set(new Vector3d(gameObject.transform.position).add(0, 1, 0));
        camera.direction.lerp(new Vector3d(focusPosition).sub(camera.getEntity().transform.position), Math.min(dt * 10, 1));
    }

    private void updateFocusedObject() {
        List<GameObject> focusableObjects = level.getFocusableObjects();
        GameObject newFocusedObject = null;

        for(GameObject object : focusableObjects) {
            Controller controller = object.getComponent(Controller.class, "Controller");
            if(controller.isInFrontOfPlayer()) {
                newFocusedObject = object;
            }
        }
        focusedObject = newFocusedObject;
    }

    private enum State {
        INTERACTING, IDLE
    }
}

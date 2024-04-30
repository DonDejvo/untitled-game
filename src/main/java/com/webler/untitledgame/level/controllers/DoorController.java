package com.webler.untitledgame.level.controllers;

import com.webler.goliath.colliders.BoxCollider3D;
import com.webler.goliath.input.Input;
import com.webler.goliath.math.MathUtils;
import com.webler.untitledgame.components.Level;
import com.webler.untitledgame.level.levelmap.Direction;
import org.joml.Vector3d;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_K;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_L;

public class DoorController extends EntityController {
    private Direction direction;
    private State state;
    private double openProgress;
    private double delay;
    private double angleOpened, angleClosed;

    public DoorController(Level level, BoxCollider3D collider, Direction direction) {
        super(level, collider);
        this.direction = direction;
        state = State.CLOSED;
        delay = 0.5;

        angleClosed = switch (direction) {
            case LEFT -> Math.PI / 2;
            case RIGHT -> -Math.PI / 2;
            case UP -> Math.PI;
            default -> 0;
        };
        angleOpened = angleClosed + Math.PI / 2;
        openProgress = 0;
    }

    @Override
    public void start() {
        updateRenderer(angleClosed);
    }

    @Override
    public void update(double dt) {
        if(Input.keyBeginPress(GLFW_KEY_K)) {
            open();
        }
        if(Input.keyBeginPress(GLFW_KEY_L)) {
            close();
        }

        if(state == State.OPENED) {
            collider.setSize(new Vector3d(0, 0, 0));
        } else {
            collider.setSize(new Vector3d(4, 4, 4));
        }

        if (state == State.OPENING) {
            openProgress += dt;
            if (openProgress >= delay) {
                state = State.OPENED;
                openProgress = delay;
            }
        } else if (state == State.CLOSING) {
            openProgress -= dt;
            if (openProgress <= 0) {
                state = State.CLOSED;
                openProgress = 0;
            }
        }
        updateRenderer(MathUtils.lerp(angleClosed, angleOpened, MathUtils.clamp(openProgress / delay, 0, 1)));
    }

    @Override
    public void destroy() {

    }

    public void open() {
        if(state != State.OPENED) {
            state = State.OPENING;
        }
    }

    public void close() {
        if(state != State.CLOSED) {
            state = State.CLOSING;
        }
    }

    private void updateRenderer(double angle) {
        gameObject.transform.rotation.setAngleAxis(angle, new Vector3d(0, 1, 0));
    }

    private enum State {
        OPENED, CLOSED, OPENING, CLOSING
    }
}

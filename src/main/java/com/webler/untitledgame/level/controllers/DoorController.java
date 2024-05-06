package com.webler.untitledgame.level.controllers;

import com.webler.goliath.colliders.BoxCollider3D;
import com.webler.goliath.core.Component;
import com.webler.goliath.core.GameObject;
import com.webler.goliath.dialogs.nodes.DialogTextNode;
import com.webler.goliath.dialogs.components.DialogManager;
import com.webler.goliath.eventsystem.EventManager;
import com.webler.goliath.graphics.Color;
import com.webler.goliath.graphics.components.MeshRenderer;
import com.webler.goliath.input.Input;
import com.webler.goliath.math.MathUtils;
import com.webler.untitledgame.components.Level;
import com.webler.untitledgame.level.events.DoorOpened;
import com.webler.untitledgame.level.inventory.Inventory;
import com.webler.untitledgame.level.levelmap.Direction;
import org.joml.Vector3d;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_K;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_L;

public class DoorController extends Controller {
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
        updateAngle(angleClosed);
        level.addObjectToGroup(gameObject, "fixed");
        level.addObjectToGroup(gameObject, "focusable");
    }

    @Override
    protected void interact() {
        if(state == State.CLOSED) {
            GameObject player = level.getPlayer();
            Inventory inventory = player.getComponent(Inventory.class, "Inventory");
            if(inventory.getItemCount("key") > 0) {
                inventory.remove("key");
                open();
            } else {
                level.getComponent(DialogManager.class, "DialogManager").showDialog(new DialogTextNode("door__no_key", null));
            }
        }
    }

    @Override
    public Vector3d getFocusPosition() {
        MeshRenderer renderer = getComponent(MeshRenderer.class, "Renderer");
        return renderer.getOffsetPosition();
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
            EventManager.dispatchEvent(new DoorOpened(gameObject));
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
        updateAngle(MathUtils.lerp(angleClosed, angleOpened, MathUtils.clamp(openProgress / delay, 0, 1)));

        MeshRenderer renderer = getComponent(MeshRenderer.class, "Renderer");
        renderer.setColor(isFocused() ? new Color(1, 1, 1) : new Color(0.5, 0.5, 0.5));

//        Vector3d offsetPosition = renderer.getOffsetPosition();
//        DebugDraw.get().addLine(new Vector3d(0, -0.5, 0).add(offsetPosition),
//                new Vector3d(0, 0.5, 0).add(offsetPosition), Color.YELLOW);
//        DebugDraw.get().addLine(new Vector3d(-0.5, 0, 0).add(offsetPosition),
//                new Vector3d(0.5, 0, 0).add(offsetPosition), Color.YELLOW);
//        DebugDraw.get().addLine(new Vector3d(0, 0, -0.5).add(offsetPosition),
//                new Vector3d(0, 0, 0.5).add(offsetPosition), Color.YELLOW);
    }

    @Override
    public void destroy() {
        level.removeObjectFromGroup(gameObject, "fixed");
        level.removeObjectFromGroup(gameObject, "focusable");
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

    @Override
    protected Vector3d getCenter() {
        Component renderer = getComponent(Component.class, "Renderer");
        return renderer.getOffsetPosition();
    }

    public Direction getDirection() {
        return direction;
    }

    private void updateAngle(double angle) {
        gameObject.transform.rotation.setAngleAxis(angle, new Vector3d(0, 1, 0));
    }

    private enum State {
        OPENED, CLOSED, OPENING, CLOSING
    }
}

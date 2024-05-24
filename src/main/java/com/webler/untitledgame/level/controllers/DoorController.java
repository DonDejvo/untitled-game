package com.webler.untitledgame.level.controllers;

import com.webler.goliath.colliders.BoxCollider3D;
import com.webler.goliath.core.Component;
import com.webler.goliath.core.GameObject;
import com.webler.goliath.dialogs.nodes.DialogTextNode;
import com.webler.goliath.dialogs.components.DialogManager;
import com.webler.goliath.eventsystem.EventManager;
import com.webler.goliath.graphics.Color;
import com.webler.goliath.graphics.DebugDraw;
import com.webler.goliath.graphics.components.MeshRenderer;
import com.webler.goliath.input.Input;
import com.webler.goliath.math.MathUtils;
import com.webler.untitledgame.level.Level;
import com.webler.untitledgame.level.events.DoorOpenedEvent;
import com.webler.untitledgame.level.inventory.Inventory;
import com.webler.untitledgame.level.enums.Direction;
import lombok.Getter;
import org.joml.Vector3d;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_K;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_L;

public class DoorController extends Controller {
    @Getter
    private Direction direction;
    private State state;
    private double openProgress;
    private final double delay;
    private final double angleOpened;
    private final double angleClosed;

    public DoorController(Level level, BoxCollider3D collider, Direction direction) {
        super(level, collider);
        this.direction = direction;
        state = State.CLOSED;
        delay = 0.5;

        // The direction of the direction.
        angleClosed = switch (direction) {
            case LEFT -> Math.PI / 2;
            case RIGHT -> -Math.PI / 2;
            case UP -> Math.PI;
            default -> 0;
        };
        angleOpened = angleClosed + Math.PI / 2;
        openProgress = 0;
    }

    /**
    * Called when the player enters the game. This is where we add the objects to the level and update the
    */
    @Override
    public void start() {
        updateAngle(angleClosed);
        level.addObjectToGroup(gameObject, "fixed");
        level.addObjectToGroup(gameObject, "focusable");
    }

    /**
    * Called when the door interacts with the player. This is the method that will be called in order to determine if the player has an inventory key or not.
    * 
    * 
    * @return true if the player has an inventory key false otherwise ( in which case the player will be closed )
    */
    @Override
    public boolean interact() {
        // Close the inventory and open the dialog if it is closed.
        if(state == State.CLOSED) {
            GameObject player = level.getPlayer();
            Inventory inventory = player.getComponent(Inventory.class, "Inventory");
            // Remove the key from inventory and open the dialog if it is empty
            if(inventory.getItemCount("key") > 0) {
                inventory.remove("key");
                open();
            } else {
                level.getComponent(DialogManager.class, "DialogManager").showDialog(new DialogTextNode("door__no_key", null));
            }
            return true;
        }
        return false;
    }

    /**
    * Gets the position of the focus. This is used to determine where the mouse is in the scene.
    * 
    * 
    * @return The position of the focus in world coordinates relative to the center of the scene ( 0 0 ) of the
    */
    @Override
    public Vector3d getFocusPosition() {
        MeshRenderer renderer = getComponent(MeshRenderer.class, "Renderer");
        return renderer.getOffsetPosition();
    }

    /**
    * Updates the door. This is called every frame. If you override this method be sure to call super. update ( dt ) in order to do something that's different from the default behavior
    * 
    * @param dt - Time since the last
    */
    @Override
    public void update(double dt) {
        // Open the input box if the key is pressed.
        if(Input.keyBeginPress(GLFW_KEY_K)) {
            open();
        }
        // Close the input stream if the key is pressed.
        if(Input.keyBeginPress(GLFW_KEY_L)) {
            close();
        }

        // Called when the game is opened.
        if(state == State.OPENED) {
            collider.setSize(new Vector3d(0, 0, 0));
            EventManager.dispatchEvent(new DoorOpenedEvent(gameObject));
        } else {
            collider.setSize(new Vector3d(4, 12, 4));
        }

        // This method updates the open state to open or closed.
        if (state == State.OPENING) {
            openProgress += dt;
            // Set the open progress to open.
            if (openProgress >= delay) {
                state = State.OPENED;
                openProgress = delay;
            }
        // This method is called when the state is CLOSED.
        } else if (state == State.CLOSING) {
            openProgress -= dt;
            // Set the state to CLOSED.
            if (openProgress <= 0) {
                state = State.CLOSED;
                openProgress = 0;
            }
        }
        updateAngle(MathUtils.lerp(angleClosed, angleOpened, MathUtils.clamp(openProgress / delay, 0, 1)));

        MeshRenderer renderer = getComponent(MeshRenderer.class, "Renderer");
        renderer.setColor(isFocused() ? new Color(1, 1, 1) : new Color(0.5, 0.5, 0.5));

        if(level.isDebug()) {
            DebugDraw.get().addBox(collider.getCenter(), collider.getSize(), Color.RED);
        }
//        Vector3d offsetPosition = renderer.getOffsetPosition();
//        DebugDraw.get().addLine(new Vector3d(0, -0.5, 0).add(offsetPosition),
//                new Vector3d(0, 0.5, 0).add(offsetPosition), Color.YELLOW);
//        DebugDraw.get().addLine(new Vector3d(-0.5, 0, 0).add(offsetPosition),
//                new Vector3d(0.5, 0, 0).add(offsetPosition), Color.YELLOW);
//        DebugDraw.get().addLine(new Vector3d(0, 0, -0.5).add(offsetPosition),
//                new Vector3d(0, 0, 0.5).add(offsetPosition), Color.YELLOW);
    }

    /**
    * Removes the fixed and focusable objects from the level. This is called when the GameObject is destroyed
    */
    @Override
    public void destroy() {
        level.removeObjectFromGroup(gameObject, "fixed");
        level.removeObjectFromGroup(gameObject, "focusable");
    }

    /**
    * Opens or resumes the connection. This is a no - op if the connection is already open. Calling this method has no effect
    */
    public void open() {
        // Sets the state of the state to OPENING.
        if(state != State.OPENED) {
            state = State.OPENING;
        }
    }

    /**
    * Closes the connection. After this call no further operations will be allowed on the connection. Calling this method has no effect
    */
    public void close() {
        // Set the state to CLOSING.
        if(state != State.CLOSED) {
            state = State.CLOSING;
        }
    }

    /**
    * Returns the center of the component. This is used to position the view when it is dragged to the center of the screen.
    * 
    * 
    * @return the center of the component in world coordinates ( relative to the view's top - left corner )
    */
    @Override
    public Vector3d getCenter() {
        Component renderer = getComponent(Component.class, "Renderer");
        return renderer.getOffsetPosition();
    }

    /**
    * Updates the angle axis of the game object. This is called from the update () method. It should be used to update the rotation axis of the game object.
    * 
    * @param angle - The angle to update the axis by ( 0 to 360
    */
    private void updateAngle(double angle) {
        gameObject.transform.rotation.setAngleAxis(angle, new Vector3d(0, 1, 0));
    }

    private enum State {
        OPENED, CLOSED, OPENING, CLOSING
    }

    /**
    * Returns the name of the Door. This is used in error messages to inform the user of the problem.
    * 
    * 
    * @return the name of the Door as a String ( never null ). May be empty but may not be null
    */
    @Override
    public String getName() {
        return "Door";
    }
}

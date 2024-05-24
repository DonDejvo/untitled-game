package com.webler.untitledgame.level.controllers.entity;

import com.webler.goliath.colliders.BoxCollider3D;
import com.webler.goliath.core.GameObject;
import com.webler.goliath.core.Scene;
import com.webler.goliath.dialogs.DialogOption;
import com.webler.goliath.dialogs.components.DialogManager;
import com.webler.goliath.dialogs.events.DialogEndedEvent;
import com.webler.goliath.dialogs.events.DialogNextEvent;
import com.webler.goliath.dialogs.nodes.DialogOptionsNode;
import com.webler.goliath.dialogs.nodes.DialogTextNode;
import com.webler.goliath.eventsystem.listeners.EventHandler;
import com.webler.goliath.graphics.Color;
import com.webler.goliath.graphics.DebugDraw;
import com.webler.goliath.graphics.canvas.Canvas;
import com.webler.goliath.graphics.canvas.TextAlign;
import com.webler.goliath.graphics.components.Camera;
import com.webler.goliath.graphics.components.CameraMovement;
import com.webler.goliath.input.Input;
import com.webler.goliath.math.MathUtils;
import com.webler.untitledgame.level.Level;
import com.webler.untitledgame.level.ai.PathFinder;
import com.webler.untitledgame.level.controllers.CollisionInfo;
import com.webler.untitledgame.level.controllers.Controller;
import com.webler.untitledgame.level.controllers.gun.GunController;
import com.webler.untitledgame.level.events.ItemUnselectedEvent;
import com.webler.untitledgame.level.prefabs.ItemPrefab;
import com.webler.untitledgame.level.widgets.HPBar;
import com.webler.untitledgame.level.events.DoorOpenedEvent;
import com.webler.untitledgame.level.events.ItemSelectedEvent;
import com.webler.untitledgame.level.inventory.Inventory;
import com.webler.untitledgame.level.prefabs.GunPrefab;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3d;

import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class PlayerController extends EntityController {
    private final Vector3d cameraOffset;
    private final Camera camera;
    private boolean canJump;
    @Getter
    private GameObject focusedObject;
    private State state;
    private final Inventory inventory;
    @Setter
    @Getter
    private GameObject companion;
    private GameObject gun;
    private boolean shouldStopInteraction;
    private final double jumpSpeed;
    private CameraMovement cameraMovement;
    private boolean isRunning;
    private GameObject gunTarget;
    private String lastGun;

    public PlayerController(Level level, Camera camera, BoxCollider3D collider, Inventory inventory, PathFinder pathFinder) {
        super(level, collider, new String[]{ "fixed", "enemy" }, pathFinder, 100, 100);
        this.cameraOffset = new Vector3d(0, 1, 0);
        this.camera = camera;
        this.inventory = inventory;
        bounciness = 0;
        canJump = true;
        focusedObject = null;
        state = State.PLAYING;
        companion = null;
        gun = null;
        shouldStopInteraction = false;
        jumpSpeed = 20;
        isRunning = false;
        gunTarget = null;
        lastGun = null;
    }

    /**
    * Collects an item into the inventory. Stops interacting with the player and adds it to the inventory
    * 
    * @param itemName - the name of the
    */
    public void collect(String itemName) {
        inventory.add(itemName);
        stopInteraction();
    }

    /**
    * Buy an item from the inventory. This will remove all gold items if the price is greater than the amount of gold
    * 
    * @param itemName - The name of the item to buy
    * @param price - The amount of the item to buy ( should be positive
    */
    public void buy(String itemName, int price) {
        // Add price items to the inventory if price is greater than the current price.
        if(inventory.getItemCount("gold") >= price) {
            // Removes all gold items from the inventory
            for (int i = 0; i < price; i++) {
                inventory.remove("gold");
            }
            inventory.add(itemName);
        }
    }

    /**
    * Equips a gun with the given item name. Does nothing if the gun already exists. This is called by GunManager. equipGun
    * 
    * @param itemName - Name of the item to equ
    */
    public void equipGun(String itemName) {
        Scene scene = gameObject.getScene();
        boolean isSame = false;
        // Removes the gun from the gun.
        if(gun != null) {
            GunController gunController = gun.getComponent(GunController.class, "Controller");
            isSame = gunController.getItemName().equals(itemName);
            gun.remove();
        }
        // Create a gun prefab and add it to the scene.
        if(!isSame) {
            gun = new GunPrefab(level, itemName).create(scene);
            scene.add(gun);
            lastGun = itemName;
        } else {
            gun = null;
        }
    }

    /**
    * Called when the player is started. This is where you can set up your game object and interact with
    */
    @Override
    public void start() {
        level.addObjectToGroup(gameObject, "player");

        startInteraction(true);
        level.getComponent(DialogManager.class, "DialogManager")
                .showDialog(new DialogTextNode("system__learn_controls",
                        new DialogOptionsNode(new DialogOption[]{
                                new DialogOption("player__no", false, null),
                                new DialogOption("player__yes", false,
                                        new DialogTextNode("system__controls_move",
                                                new DialogTextNode("system__controls_interact",
                                                        new DialogTextNode("system__controls_inventory",
                                                                new DialogTextNode("system__controls_gun", null)))))
                        })));

        cameraMovement = gameObject.getScene().getCamera().getComponent(CameraMovement.class, "CameraMovement");
        cameraMovement.setEnabled(false);
    }

    /**
    * Updates the state of the game. This is called every frame to ensure that the game is in a state that can be used for interaction with other game objects.
    * 
    * @param dt - Time since the last update in seconds ( ignored if already in that state
    */
    @Override
    public void update(double dt) {
        // Updates the state of the camera.
        switch (state) {
            case PLAYING:
                updatePlaying(dt);
                break;
            case INTERACTING:
                updateInteracting(dt);
                break;
            case LOOKING_INVENTORY:
                updateLookingInventory(dt);
                break;
            case DEBUG:
                // This method is called when the user presses the key press.
                if(Input.keyBeginPress(GLFW_KEY_C)) {
                    state = State.PLAYING;
                    cameraMovement.setEnabled(false);
                }
                break;
        }
        updateGun();

        // Stop the interaction if the interaction is paused.
        if(shouldStopInteraction) {
            state = State.PLAYING;
            shouldStopInteraction = false;
        }

        Input.setCursorLocked(state == State.PLAYING);

        friction = onGround ? 10 : 2;
        updatePhysics(dt);

        Canvas canvas = getGameObject().getGame().getCanvas();
        HPBar hpBar = getHPBar();
        hpBar.draw(canvas, canvas.getHeight() * 0.02f, canvas.getHeight() * 0.96f, canvas.getHeight() * 0.36f);
    }

    @Override
    public HPBar getHPBar() {
        return new HPBar(hp, maxHp);
    }

    /**
    * Returns the name of the player. Used to display the player in the GUI. Note that this is a string that may be different from the player's name in the case of a player playing a game.
    * 
    * 
    * @return a string that is the name of the player to be displayed in the GUI. If the player is playing a game it will be " Player "
    */
    @Override
    public String getName() {
        return "Player";
    }

    /**
    * Removes the player from the level. This is called when the player is no longer connected to the game
    */
    @Override
    public void destroy() {
        level.removeObjectFromGroup(gameObject, "player");
    }

    /**
    * Called when the user closes the dialog. This is a no - op for Dialog implementations that don't support it.
    * 
    * @param event - Details about the event that triggered the call to this
    */
    @EventHandler
    @SuppressWarnings("unused")
    public void onDialogEnded(DialogEndedEvent event) {
        stopInteraction();
    }

    /**
    * Removes caffe latte from inventory when dialog is clicked. This is a no - op in Android 1. 4 and above
    * 
    * @param event - The event to process
    */
    @EventHandler
    @SuppressWarnings("unused")
    public void onDialogNext(DialogNextEvent event) {
        // Coffee de la dialog.
        if (event.getDialogName().equals("player__give_coffee")) {
            inventory.remove("caffe_latte");
        }
    }

    /**
    * Called when door is opened. This is a no - op if the focused object is not the focused object.
    * 
    * @param event - Details about the event. Not used in this implementation
    */
    @EventHandler
    @SuppressWarnings("unused")
    public void onDoorOpened(DoorOpenedEvent event) {
        // Stop the interaction if the door is focused.
        if(event.getDoor() == focusedObject) {
            stopInteraction();
        }
    }

    /**
    * Called when an item is selected. This is the event handler for ItemSelectedEvents. The name of the item is passed to the event.
    * 
    * @param event - Details about the selected item ( name of the item
    */
    @EventHandler
    @SuppressWarnings("unused")
    public void onItemSelected(ItemSelectedEvent event) {
        // The equipGun method is used to set the equipGun attribute of the event.
        switch (event.getItemName()) {
            case "ak47", "shotgun": {
                equipGun(event.getItemName());
                break;
            }
        }
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onItemUnselected(ItemUnselectedEvent event) {
        String itemName = event.getItemName();
        inventory.remove(itemName);
        if(itemName.equals(lastGun)) {
            if(gun != null) {
                equipGun(lastGun);
            }
            lastGun = null;
        }
        Scene scene = gameObject.getScene();
        GameObject itemObject = new ItemPrefab(level, level.getRegisteredObject(itemName)).create(scene);
        itemObject.transform.position.set(gameObject.transform.position);
        itemObject.transform.position.add(new Vector3d(4, 0, 0).rotateY(yaw));
        //itemObject.getComponent(ItemController.class, "Controller").velocity.set(new Vector3d(1, 0, 0).rotateY(yaw).mul(200));
        scene.add(itemObject);
    }

    /**
    * Starts interaction with the currently focused object. If there is no object to interact with this is a no - op
    * 
    * @param forceWithoutObject - if true the interaction will be forced regardless of the focused
    */
    private void startInteraction(boolean forceWithoutObject) {
        // Stops the interaction if the user has focus.
        if(forceWithoutObject) {
            state = State.INTERACTING;
        // Stops the interaction if the focused object is currently focused.
        } else if(focusedObject != null) {
            state = State.INTERACTING;
            Controller controller = focusedObject.getComponent(Controller.class, "Controller");
            // Stop the interaction if the controller is interacting.
            if(!controller.interact()) {
                stopInteraction();
            }
        }
    }

    /**
    * Starts interaction with the user. This is a no - op if there is already a user interacting with the
    */
    private void startInteraction() {
        startInteraction(false);
    }

    /**
    * Stops interacting with the UI. This is called when the user presses CTRL - C or Ctrl
    */
    private void stopInteraction() {
        shouldStopInteraction = true;
    }

    /**
    * Updates the Playing state. This is called every frame to update the game's state based on user input
    * 
    * @param dt - Time since last update in
    */
    private void updatePlaying(double dt) {

        // Draws the gun to the canvas.
        if(gun != null) {
            Canvas canvas = gameObject.getGame().getCanvas();
            canvas.setColor(Color.WHITE);
            canvas.rect((float) canvas.getWidth() / 2 - 16, (float) canvas.getHeight() / 2 - 1, 32, 2);
            canvas.rect((float) canvas.getWidth() / 2 - 1, (float) canvas.getHeight() / 2 - 16, 2, 32);
        }

        Vector3d moveVec = new Vector3d();

        // MoveVec. x to the left of the moveVec. x
        if(Input.keyPressed(GLFW_KEY_W)) {
            moveVec.x += 1;
        }
        // MoveVec. x to the left of the moveVec. x
        if(Input.keyPressed(GLFW_KEY_S)) {
            moveVec.x -= 1;
        }
        // MoveVec. z by 1.
        if(Input.keyPressed(GLFW_KEY_A)) {
            moveVec.z -= 1;
        }
        // MoveVec z by 1. z
        if(Input.keyPressed(GLFW_KEY_D)) {
            moveVec.z += 1;
        }
        // Normalize the moveVec to the nearest square of the moveVec.
        if(moveVec.lengthSquared() > 0) {
            moveVec.normalize();
        }
        moveVec.mul(speed * (onGround ? 1 : 0.3));

        isRunning = Input.keyPressed(GLFW_KEY_LEFT_SHIFT) && onGround && moveVec.x > 0;
        // Move vector to the current position
        if(isRunning) moveVec.x *= 2;

        yaw += Input.mouseDeltaX() * -0.005;
        pitch = MathUtils.clamp(pitch + Input.mouseDeltaY() * 0.005, -1.5, 1.5);

        moveVec.rotateY(yaw);
        acceleration.set(moveVec);

        // If the user presses a keypress on the GLFW_KEY_SPACE key pressed and can jump.
        if(Input.keyPressed(GLFW_KEY_LEFT_ALT) && onGround && canJump) {
            velocity.y = jumpSpeed;
            onGround = false;
            canJump = false;
        }
        // If the user presses a keypress or key space
        if(!Input.keyPressed(GLFW_KEY_LEFT_ALT)) {
            canJump = true;
        }

        // This method is called when the user presses the key press.
        if(gun == null && Input.keyBeginPress(GLFW_KEY_E)) {
            startInteraction();
        }

        if(lastGun != null && Input.keyBeginPress(GLFW_KEY_SPACE)) {
            equipGun(lastGun);
        }

        // Keypress for the inventory.
        if(Input.keyBeginPress(GLFW_KEY_TAB) || Input.keyBeginPress(GLFW_KEY_I)) {
            state = State.LOOKING_INVENTORY;
            inventory.setOpened(true);
        }

        // This method is called when the user presses the key press.
        if(Input.keyBeginPress(GLFW_KEY_C)) {
            state = State.DEBUG;
            cameraMovement.setEnabled(true);
        }

        updateCamera(dt);

        updateFocusedObject();
        // This method is called when the focused object is focused.
        if(focusedObject != null) {
            Controller focusedObjectController = focusedObject.getComponent(Controller.class, "Controller");

            Canvas canvas = gameObject.getGame().getCanvas();
            canvas.setColor(Color.WHITE);
            canvas.setFontSize(canvas.getHeight() * 0.02f);
            TextAlign align = canvas.getTextAlign();
            canvas.setTextAlign(TextAlign.CENTER);
            canvas.text(focusedObjectController.getName(), canvas.getWidth() * 0.5f, canvas.getHeight() * 0.02f);
            canvas.setTextAlign(align);

            HPBar hpBar = focusedObjectController.getHPBar();
            if(hpBar != null) {
                hpBar.draw(canvas, canvas.getWidth() * 0.5f - canvas.getHeight() * 0.18f, canvas.getHeight() * 0.06f,canvas.getHeight() * 0.36f);
            }
        }

    }

    /**
    * Updates the camera's position based on the focus. This is called every frame to ensure that the camera is in the correct position and also to make sure that the accelerometer is set to zero.
    * 
    * @param dt - Time since the last update in seconds ( ignored for backwards compatibility
    */
    private void updateInteracting(double dt) {
        acceleration.x = 0;
        acceleration.z = 0;

        // Updates camera position and direction of the camera.
        if(focusedObject != null && gameObject != focusedObject) {
            Vector3d focusPosition = focusedObject.getComponent(Controller.class, "Controller").getFocusPosition();

            camera.getGameObject().transform.position.set(new Vector3d(gameObject.transform.position).add(cameraOffset));

            camera.direction.lerp(new Vector3d(focusPosition).sub(camera.getGameObject().transform.position), Math.min(dt * 10, 1));
        } else {
            updateCamera(dt);
        }
    }

    /**
    * Updates the looking inventory. This is called every frame to check for key presses. If tab or escape is pressed the inventory is closed and the camera is reloaded
    * 
    * @param dt - Time since the last
    */
    private void updateLookingInventory(double dt) {
        acceleration.x = 0;
        acceleration.z = 0;

        // This method is called when the user presses the key presses the inventory.
        if(Input.keyBeginPress(GLFW_KEY_TAB) || Input.keyBeginPress(GLFW_KEY_ESCAPE) || Input.keyBeginPress(GLFW_KEY_I)) {
            inventory.setOpened(false);
            state = State.PLAYING;
        }

        updateCamera(dt);
    }

    /**
    * Updates the camera's direction. This is called every frame to update the viewing frustum.
    * 
    * @param dt - Time since last frame in seconds ( 0 to 1
    */
    private void updateCamera(double dt) {
        Vector3d direction = new Vector3d(0, 0, 1);
        direction.rotateX(pitch);
        direction.rotateY(yaw + Math.PI / 2);

        camera.getGameObject().transform.position.set(new Vector3d(gameObject.transform.position).add(cameraOffset));
//        camera.direction.lerp(direction, Math.min(dt * 10, 1));
        camera.direction.set(direction);
    }

    /**
    * Updates the gun based on the mouse input. This is called every frame to make sure it doesn't collide with the player
    */
    private void updateGun() {
        // This method is called by the game object when the user clicks on the gun.
        if(gun != null) {

            GunController gunController = gun.getComponent(GunController.class, "Controller");
            // If the mouse button is pressed or released or released by the user pressing the mouse button.
            if(Input.mouseButtonBeginPress(GLFW_MOUSE_BUTTON_LEFT)  && state == State.PLAYING) {
                gunController.setShooting(true);
            // If the user presses the mouse button press or if the mouse button is pressed or if the mouse button is pressed or if the mouse button is not pressed or if the mouse button is pressed or if the mouse button is pressed or if the mouse button is pressed or if the mouse button is pressed or if the mouse button is pressed or if the mouse button is pressed or if the mouse button is pressed or if the mouse button is pressed or if the mouse button is pressed or if the mouse button is pressed or if the mouse button is pressed or if the mouse button is pressed or if the mouse button is pressed or if the mouse is pressed
            } else if(!Input.mouseButtonPress() || state != State.PLAYING) {
                gunController.setShooting(false);
            }

            Vector3d direction = new Vector3d(0, 0, 1);
            direction.rotateX(pitch);
            direction.rotateY(yaw + Math.PI / 2);

            gun.transform.position.set(gameObject.transform.position);
            gun.transform.position.add(new Vector3d(0, 0.5, 1).rotateY(yaw));

            CollisionInfo rayCollisionInfo = raycast(camera.getGameObject().transform.position, direction, 100, 1, new String[] { "fixed", "npc", "enemy" });

            // This method is called when the ray hit is hit.
            if(rayCollisionInfo != null) {
                double rayLength = rayCollisionInfo.position().distance(camera.getGameObject().transform.position);
                double minRayLength = 10;

                Vector3d gunDirection = new Vector3d(rayCollisionInfo.position()).sub(gunController.getProjectilePosition().sub(new Vector3d(direction).mul(Math.max(minRayLength - rayLength, 0)))).normalize();
                gunController.yaw = Math.atan2(-gunDirection.z, gunDirection.x);
                gunController.pitch = Math.asin(-gunDirection.y);

                gunTarget = rayCollisionInfo.collidingObject();

                if(level.isDebug()) {
                    DebugDraw.get().addCross(rayCollisionInfo.position(), 1, Color.YELLOW);
                }
            } else {
                gunController.yaw = yaw;
                gunController.pitch = pitch;

                gunTarget = null;
            }
        }
    }

    /**
    * Updates the focused object based on the focusable objects in the level. Focus is determined by looking at the distance between the camera and the focus
    */
    private void updateFocusedObject() {
        if(gun != null) {
            focusedObject = gunTarget;
            return;
        }

        List<GameObject> focusableObjects = level.getObjectsByGroup("focusable");
        GameObject newFocusedObject = null;
        double currentDistance = 0;

        for(GameObject object : focusableObjects) {
            Controller controller = object.getComponent(Controller.class, "Controller");
            double distance = controller.getCenter().distance(camera.getGameObject().transform.position);
            // Sets the current distance to the new object.
            if(controller.isInFrontOfPlayer() && (newFocusedObject == null || distance < currentDistance)) {
                newFocusedObject = object;
                currentDistance = distance;
            }
        }
        focusedObject = newFocusedObject;
    }

    private enum State {
        INTERACTING, PLAYING, LOOKING_INVENTORY, DEBUG
    }
}
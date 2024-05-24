package com.webler.untitledgame.level.controllers.entity.npc;

import com.webler.goliath.animation.components.Animator;
import com.webler.goliath.colliders.BoxCollider3D;
import com.webler.goliath.dialogs.DialogOption;
import com.webler.goliath.dialogs.components.DialogComponent;
import com.webler.goliath.dialogs.events.DialogNextEvent;
import com.webler.goliath.dialogs.nodes.DialogTextNode;
import com.webler.goliath.eventsystem.listeners.EventHandler;
import com.webler.goliath.graphics.Color;
import com.webler.goliath.graphics.components.SpriteRenderer;
import com.webler.goliath.utils.AssetPool;
import com.webler.untitledgame.level.Level;
import com.webler.untitledgame.level.ai.PathFinder;
import com.webler.untitledgame.level.controllers.entity.PlayerController;
import com.webler.untitledgame.level.widgets.HPBar;
import com.webler.untitledgame.level.inventory.Inventory;

public class KnightController extends NpcController {
    private State state;
    private double counter;
    private final double delay;
    private int dialogIdx;

    public KnightController(Level level, BoxCollider3D collider, DialogComponent dialogComponent, PathFinder pathFinder) {
        super(level, collider, dialogComponent, pathFinder, 90);
        counter = 0;
        delay = 0.5;
        state = State.IDLE;
        dialogIdx = 0;
    }

    /**
    * Starts the activity. This is called by the Activity#onStart () method and should be overridden if you want to do something before the activity is started
    */
    @Override
    public void start() {
        super.start();
    }

    /**
    * Updates physics friction and state. Called every frame. This is the method that should be called by the GameObject.
    * 
    * @param dt - Time since last frame in seconds ( ignored if onGround
    */
    @Override
    public void update(double dt) {

        state = isPlayerCompanion() ? State.FOLLOWING : State.IDLE;

        // Updates the state of the current state.
        switch (state) {
            case IDLE:
                updateIdle(dt);
                break;
            case FOLLOWING:
                updateFollowing(dt);
                break;
        }

        friction = onGround ? 10 : 2.5;
        updatePhysics(dt);

        Animator animator = getComponent(Animator.class, "Animator");
        // If the acceleration is negative or not onGround then play the game animation.
        if(Math.abs(acceleration.x) + Math.abs(acceleration.z) == 0 || !onGround) {
            animator.playAnimIfNotPlaying(AssetPool.getAnimation("untitled-game/animations/knight__idle"), true);
        } else {
            animator.playAnimIfNotPlaying(AssetPool.getAnimation("untitled-game/animations/knight__run"), true);
        }

        SpriteRenderer renderer = getComponent(SpriteRenderer.class, "Renderer");
        renderer.setColor(isFocused() ? new Color(1.00, 1.00, 1.00) : new Color(0.5, 0.5, 0.5));
    }

    /**
    * Initializes options and dialogs. Called by init () when the dialog is created or re - created in order to set the options
    */
    @Override
    protected void initDialogs() {

        dialogComponent.getOptions().clear();

        // If the player is companion then the player is followed by the player.
        if(isPlayerCompanion()) {

            dialogComponent.addOption(new DialogOption("player__stop_follow", false,
                    new DialogTextNode("npc__stop_follow", null)));
        } else {
            dialogComponent.addOption(new DialogOption("player__follow", false,
                    new DialogTextNode("npc__follow", null)));
        }

        boolean hasCoffee = level.getPlayer()
                .getComponent(Inventory.class, "Inventory")
                .getItemCount("caffe_latte") > 0;

        dialogComponent.addOption(new DialogOption("player__whats_up", false,
                new DialogTextNode("knight__whats_up_" + (dialogIdx + 1), null)));
        // If the player has coffee set to true then the player will take the coffee
        if(hasCoffee) {
            dialogComponent.addOption(new DialogOption("player__give_coffee", true,
                    new DialogTextNode("npc__take_coffee", null)));
        }
    }

    /**
    * Called when a dialog is clicked. This is the event handler for DialogNextEvents that are fired by the player's GameObject.
    * 
    * @param event - The event that triggered this method call. Must contain gameObject
    */
    @EventHandler
    @SuppressWarnings("unused")
    public void onDialogNext(DialogNextEvent event) {
        // This method is called when the event is triggered by the game object.
        if(event.getGameObject() != gameObject) return;

        // This method is called when the user clicks on the follow button.
        switch (event.getDialogName()) {
            case "npc__stop_follow":
                level.getPlayer().getComponent(PlayerController.class, "Controller").setCompanion(null);
                break;
            case "npc__follow":
                level.getPlayer().getComponent(PlayerController.class, "Controller").setCompanion(gameObject);
                break;
        }
        // This method is called when the user clicks on the dialog.
        if(event.getDialogName().startsWith("knight__whats_up")) {
            dialogIdx = (dialogIdx + 1) % 2;
        }

        initDialogs();
    }

    /**
    * Updates the accelerometer to zero. This is called every frame during idle time. We don't need to worry about this as it's a no - op.
    * 
    * @param dt - The time since the last update in seconds ( ignored
    */
    private void updateIdle(double dt) {
        acceleration.x = 0;
        acceleration.z = 0;
    }

    /**
    * Updates the follow path. This is called every frame to find the next path if it's time to follow
    * 
    * @param dt - time since last update in
    */
    private void updateFollowing(double dt) {
        counter += dt;

        // Move the path to the next position.
        if(counter >= delay) {
            findPath(level.getPlayer().transform.position);
            counter = 0;
        }

        followPath();
    }

    /**
    * Checks if the player is companion. This is used to prevent gameplay of player that are not played in game.
    * 
    * 
    * @return true if the player is companion false otherwise ( player is not played in game ). Note : PlayerController#getCompanion () is null
    */
    private boolean isPlayerCompanion() {
        return level.getPlayer().getComponent(PlayerController.class, "Controller").getCompanion() == gameObject;
    }

    /**
    * Returns the name of Knight. This is used to display the information in the UI. Note that the name may change over time depending on the user's preferences.
    * 
    * 
    * @return a String representation of the Knight in human readable form e. g. " Knight " or
    */
    @Override
    public String getName() {
        return "Knight";
    }

    @Override
    public HPBar getHPBar() {
        return new HPBar(hp, maxHp);
    }

    @Override
    public boolean receiveDamage(int damage) {
        //hp = Math.max(hp - damage, 0);
        return true;
    }

    private enum State {
        IDLE, FOLLOWING
    }
}

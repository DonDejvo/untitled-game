package com.webler.untitledgame.level.controllers;

import com.webler.goliath.colliders.BoxCollider3D;
import com.webler.goliath.dialogs.DialogOption;
import com.webler.goliath.dialogs.components.DialogComponent;
import com.webler.goliath.dialogs.events.DialogNextEvent;
import com.webler.goliath.dialogs.nodes.DialogTextNode;
import com.webler.goliath.eventsystem.listeners.EventHandler;
import com.webler.goliath.graphics.Color;
import com.webler.goliath.graphics.components.SpriteRenderer;
import com.webler.untitledgame.components.Level;
import com.webler.untitledgame.components.PathFinder;
import com.webler.untitledgame.level.inventory.Inventory;

public class CatGirlController extends  NpcController{
    private State state;
    private double counter;
    private double delay;
    private int dialogIdx;

    public CatGirlController(Level level, BoxCollider3D collider, DialogComponent dialogComponent, PathFinder pathFinder) {
        super(level, collider, dialogComponent, pathFinder, 60);
        counter = 0;
        delay = 0.5;
        state = State.IDLE;
        dialogIdx = 0;
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void update(double dt) {
        state = isPlayerCompanion() ? State.FOLLOWING : State.IDLE;

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

        SpriteRenderer renderer = getComponent(SpriteRenderer.class, "Renderer");
        renderer.setColor(isFocused() ? new Color(1.00, 1.00, 1.00) : new Color(0.5, 0.5, 0.5));
    }

    @Override
    protected void initDialogs() {

        dialogComponent.getOptions().clear();

        if(isPlayerCompanion()) {

            boolean hasCoffee = level.getPlayer()
                    .getComponent(Inventory.class, "Inventory")
                    .getItemCount("caffe_latte") > 0;

            dialogComponent.addOption(new DialogOption("player__whats_up", false,
                    new DialogTextNode("cat_girl__whats_up_" + (dialogIdx + 1), null)));
            if(hasCoffee) {
                dialogComponent.addOption(new DialogOption("player__give_coffee", true,
                        new DialogTextNode("cat_girl__take_coffee", null)));
            }
            dialogComponent.addOption(new DialogOption("player__stop_follow", false,
                    new DialogTextNode("cat_girl__stop_follow", null)));
        } else {
            dialogComponent.addOption(new DialogOption("player__follow", false,
                    new DialogTextNode("cat_girl__follow", null)));
        }
    }

    @EventHandler
    public void onDialogNext(DialogNextEvent event) {
        if(event.getGameObject() != gameObject) return;

        switch (event.getDialogName()) {
            case "cat_girl__stop_follow":
                level.getPlayer().getComponent(PlayerController.class, "Controller").setCompanion(null);
                break;
            case "cat_girl__follow":
                level.getPlayer().getComponent(PlayerController.class, "Controller").setCompanion(gameObject);
                break;
        }
        if(event.getDialogName().startsWith("cat_girl__whats_up")) {
            dialogIdx = (dialogIdx + 1) % 4;
        }

        initDialogs();
    }

    private void updateIdle(double dt) {
        acceleration.x = 0;
        acceleration.z = 0;
    }

    private void updateFollowing(double dt) {
        counter += dt;

        if(counter >= delay) {
            findPath(level.getPlayer().transform.position);
            counter = 0;
        }

        followPath();
    }

    private boolean isPlayerCompanion() {
        return level.getPlayer().getComponent(PlayerController.class, "Controller").getCompanion() == gameObject;
    }

    private enum State {
        IDLE, FOLLOWING
    }
}

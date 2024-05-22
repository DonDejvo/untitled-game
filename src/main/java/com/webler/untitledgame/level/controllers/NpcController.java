package com.webler.untitledgame.level.controllers;

import com.webler.goliath.colliders.BoxCollider3D;
import com.webler.goliath.dialogs.components.DialogComponent;
import com.webler.goliath.graphics.Color;
import com.webler.goliath.graphics.components.SpriteRenderer;
import com.webler.untitledgame.components.Level;
import com.webler.untitledgame.components.PathFinder;
import org.joml.Vector3d;

public abstract class NpcController extends EntityController{
    protected DialogComponent dialogComponent;

    public NpcController(Level level, BoxCollider3D collider, DialogComponent dialogComponent, PathFinder pathFinder, double speed) {
        super(level, collider, new String[]{ "fixed" }, pathFinder, speed);
        keepOffLedges = true;
        this.dialogComponent = dialogComponent;
    }

    /**
    * Called when the dialog is created. Subclasses may override this method to initialize dialogs that are specific to the dialog
    */
    protected void initDialogs() {}

    /**
    * Called when the user interacts with the dialog. This is a blocking call so you don't have to call it yourself.
    * 
    * 
    * @return true if the user interacts with the dialog false otherwise. In this case the dialog will be dismissed
    */
    @Override
    protected boolean interact() {
        initDialogs();
        dialogComponent.play();
        return true;
    }

    /**
    * Returns the position where the focus should be. It is used to determine where the player should be fighting when it is in the middle of the game object.
    * 
    * 
    * @return The position where the player should be fighting when it is in the middle of the game object
    */
    @Override
    public Vector3d getFocusPosition() {
        return new Vector3d(gameObject.transform.position).add(0, 0.5, 0);
    }

    /**
    * Called when the player starts. This is where we add npc and focusable objects to the level
    */
    @Override
    public void start() {
        level.addObjectToGroup(gameObject, "npc");
        level.addObjectToGroup(gameObject, "focusable");
    }

    /**
    * Updates physics and sets focus. Called every frame. Should be overridden by subclasses to do something different.
    * 
    * @param dt - time since last update in seconds ( ignored if this is a frame
    */
    @Override
    public void update(double dt) {
        updatePhysics(dt);

        SpriteRenderer renderer = getComponent(SpriteRenderer.class, "Renderer");
        renderer.setColor(isFocused() ? new Color(1.00, 1.00, 1.00) : new Color(0.5, 0.5, 0.5));
    }

    /**
    * Removes npc and focusable objects from the level. This is called when the player is no longer in the
    */
    @Override
    public void destroy() {
        level.removeObjectFromGroup(gameObject, "npc");
        level.removeObjectFromGroup(gameObject, "focusable");
    }
}

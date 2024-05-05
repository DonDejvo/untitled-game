package com.webler.untitledgame.level.controllers;

import com.webler.goliath.colliders.BoxCollider3D;
import com.webler.goliath.dialogs.components.DialogComponent;
import com.webler.goliath.graphics.Color;
import com.webler.goliath.graphics.components.SpriteRenderer;
import com.webler.untitledgame.components.Level;
import org.joml.Vector3d;

public class NpcController extends EntityController{
    protected DialogComponent dialogComponent;

    public NpcController(Level level, BoxCollider3D collider, DialogComponent dialogComponent) {
        super(level, collider, new String[]{ "player", "fixed" });
        this.dialogComponent = dialogComponent;
    }

    protected void initDialogs() {}

    @Override
    protected void interact() {
        initDialogs();
        dialogComponent.play();
    }

    @Override
    public Vector3d getFocusPosition() {
        return new Vector3d(gameObject.transform.position).add(0, 0.5, 0);
    }

    @Override
    public void start() {
        level.addObjectToGroup(gameObject, "npc");
        level.addObjectToGroup(gameObject, "focusable");
    }

    @Override
    public void update(double dt) {
        updatePhysics(dt);

        SpriteRenderer renderer = getComponent(SpriteRenderer.class, "Renderer");
        renderer.setColor(isFocused() ? new Color(1.00, 1.00, 1.00) : new Color(0.5, 0.5, 0.5));
    }

    @Override
    public void destroy() {
        level.removeObjectFromGroup(gameObject, "npc");
        level.removeObjectFromGroup(gameObject, "focusable");
    }
}

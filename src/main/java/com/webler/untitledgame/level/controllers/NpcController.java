package com.webler.untitledgame.level.controllers;

import com.webler.goliath.colliders.BoxCollider3D;
import com.webler.goliath.dialogs.components.DialogComponent;
import com.webler.goliath.graphics.Color;
import com.webler.goliath.graphics.components.SpriteRenderer;
import com.webler.untitledgame.components.Level;
import org.joml.Vector3d;

public class NpcController extends EntityController{
    public NpcController(Level level, BoxCollider3D collider) {
        super(level, collider, true);
    }

    @Override
    protected void interact() {
        getComponent(DialogComponent.class, "Dialog").play();
    }

    @Override
    public Vector3d getFocusPosition() {
        return new Vector3d(gameObject.transform.position).add(0, 0.5, 0);
    }

    @Override
    public void start() {
        level.getEnemyEntityObjects().add(gameObject);
        level.getFocusableObjects().add(gameObject);
    }

    @Override
    public void update(double dt) {
        updatePhysics(dt);

        SpriteRenderer renderer = getComponent(SpriteRenderer.class, "Renderer");
        renderer.setColor(isFocused() ? new Color(1.00, 1.00, 1.00) : new Color(0.5, 0.5, 0.5));
    }

    @Override
    public void destroy() {
        level.getEnemyEntityObjects().remove(gameObject);
        level.getFocusableObjects().remove(gameObject);
    }
}

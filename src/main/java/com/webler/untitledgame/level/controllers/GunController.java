package com.webler.untitledgame.level.controllers;

import com.webler.goliath.graphics.components.SpriteRenderer;
import com.webler.goliath.math.MathUtils;
import com.webler.untitledgame.components.Level;
import com.webler.untitledgame.level.Projectile;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector2d;
import org.joml.Vector3d;

public abstract class GunController extends Controller {
    @Getter
    private String itemName;
    private double reloadTime;
    private double reloadCounter;
    @Getter
    private Projectile projectileType;
    private State state;
    @Setter
    private boolean shooting;
    private Vector2d centerOffset;
    @Getter
    private Vector2d projectileOffset;

    public GunController(Level level, String itemName, double reloadTime, Projectile projectileType, Vector2d centerOffset, Vector2d projectileOffset) {
        super(level, null);
        this.itemName = itemName;
        this.reloadTime = reloadTime;
        this.reloadCounter = reloadTime;
        this.projectileType = projectileType;
        this.state = State.READY;
        this.shooting = false;
        this.centerOffset = centerOffset;
        this.projectileOffset = projectileOffset;
    }

    @Override
    public void start() {

    }

    @Override
    public void update(double dt) {

        if(state == State.RELOADING) {
            reloadCounter += dt;
            if(reloadCounter >= reloadTime) {
                state = State.READY;
            }
        }
        if(shooting && state == State.READY) {
            reloadCounter = 0;
            state = State.RELOADING;
            shoot();
        }

        gameObject.transform.rotation.identity().rotateXYZ(0, yaw, -pitch);

        updateRenderer();
    }

    @Override
    public void destroy() {

    }

    abstract protected void shoot();

    protected Vector3d getProjectilePosition() {
        return new Vector3d(gameObject.transform.position)
                .add(new Vector3d(0, projectileOffset.y, projectileOffset.x)
                        .rotateX(pitch)
                        .rotateY(yaw + Math.PI / 2));
    }

    private void updateRenderer() {
        SpriteRenderer renderer = getComponent(SpriteRenderer.class, "Renderer");
        renderer.offset.set(centerOffset.x + MathUtils.clamp(1 - reloadCounter / reloadTime, 0, 1) * -0.5, centerOffset.y, 0);
    }

    private enum State {
        READY, RELOADING
    }
}

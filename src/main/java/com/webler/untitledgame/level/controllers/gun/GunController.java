package com.webler.untitledgame.level.controllers.gun;

import com.webler.goliath.graphics.components.SpriteRenderer;
import com.webler.goliath.math.MathUtils;
import com.webler.untitledgame.level.Level;
import com.webler.untitledgame.level.controllers.Controller;
import com.webler.untitledgame.level.enums.Projectile;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector2d;
import org.joml.Vector3d;

public abstract class GunController extends Controller {
    @Getter
    private String itemName;
    private final double reloadTime;
    private double reloadCounter;
    @Getter
    private Projectile projectileType;
    private State state;
    @Setter
    private boolean shooting;
    private final Vector2d centerOffset;
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

    /**
    * Called when the server is started. This is where we start the web server and the server's state is maintained
    */
    @Override
    public void start() {

    }

    /**
    * Updates the state of the game object. This is called every frame to update the position of the game object based on the time it takes to load the data.
    * 
    * @param dt - The amount of time since the last update in
    */
    @Override
    public void update(double dt) {

        // Increments the reload counter if the reloading is in the state.
        if(state == State.RELOADING) {
            reloadCounter += dt;
            // Reload the state of the state.
            if(reloadCounter >= reloadTime) {
                state = State.READY;
            }
        }
        // Shoots the reloading state if the reloading state is ready.
        if(shooting && state == State.READY) {
            reloadCounter = 0;
            state = State.RELOADING;
            shoot();
        }

        gameObject.transform.rotation.identity().rotateXYZ(0, yaw, -pitch);

        updateRenderer();
    }

    /**
    * Called when the component is no longer needed. This is the place to do any cleanup that needs to be done
    */
    @Override
    public void destroy() {

    }

    /**
    * Returns the name of the item. This is used to generate the name of the menu item when it is added to the menu or removed from the menu.
    * 
    * 
    * @return the name of the item as a String or null if there is no name associated with the item ( in which case the name will be the same as the item
    */
    @Override
    public String getName() {
        return level.getRegisteredObject(getItemName()).getName();
    }

    /**
    * Shoot the scene. Subclasses should override this if they want to do something other than shoot
    */
    abstract protected void shoot();

    /**
    * Calculates the position of the projectile. This is used to determine where the object should be positioned in the game object's coordinate system.
    * 
    * 
    * @return Vector3d A vector that contains the position of the projectile relative to the game object's
    */
    public Vector3d getProjectilePosition() {
        return new Vector3d(gameObject.transform.position)
                .add(new Vector3d(0, projectileOffset.y, projectileOffset.x)
                        .rotateX(pitch)
                        .rotateY(yaw + Math.PI / 2));
    }

    /**
    * Updates the SpriteRenderer to reflect the change in reload time. This is used to make the sprite appear at the center of the screen
    */
    private void updateRenderer() {
        SpriteRenderer renderer = getComponent(SpriteRenderer.class, "Renderer");
        renderer.offset.set(centerOffset.x + MathUtils.clamp(1 - reloadCounter / reloadTime, 0, 1) * -0.5, centerOffset.y, 0);
    }

    private enum State {
        READY, RELOADING
    }
}

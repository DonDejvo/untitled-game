package com.webler.untitledgame.level.controllers;

import com.webler.goliath.colliders.BoxCollider3D;
import com.webler.goliath.core.GameObject;
import com.webler.goliath.core.Scene;
import com.webler.goliath.graphics.Color;
import com.webler.goliath.graphics.Sprite;
import com.webler.goliath.graphics.components.SpriteRenderer;
import com.webler.goliath.utils.AssetPool;
import com.webler.untitledgame.components.Level;
import com.webler.untitledgame.prefabs.level.ExplosionLightPrefab;
import com.webler.untitledgame.prefabs.level.ParticlePrefab;
import org.joml.Vector3d;

public class ShellProjectileController extends ProjectileController{
    public ShellProjectileController(Level level, BoxCollider3D collider, Vector3d direction) {
        super(level, collider, 150, direction);
        gravity = 0;
        friction = 0;
    }

    /**
    * Called when collision is detected. Overridden to allow subclasses to override behavior. This is called by CollisionDetector. doCollides () if they are colliding with a collision axis
    * 
    * @param axis - the axis that the collision
    */
    @Override
    protected void didCollides(Vector3d axis) {
        Vector3d offset = new Vector3d(axis).mul(axis.dot(direction) > 0 ? 1 : -1);
        explode(true, false, offset);
    }

    /**
    * Called when an entity collides with this entity. This is the place where we're planning to explore the collider.
    * 
    * @param entity - The entity that collides with this entity. This can be different from the entity passed to #explode ( boolean Vector3d )
    */
    @Override
    protected void didCollidesWithEntity(GameObject entity) {
        explode(false, true, new Vector3d());
    }

    /**
    * Explode the level. If generateLight is true a light will be generated at the offset. If hit is true the explosion will be hit.
    * 
    * @param generateLight - Whether or not to generate a light.
    * @param hit - Whether or not to hit the level. This is used to determine if the player is in the middle of an explosion.
    * @param offset - The offset to explosion will be hit
    */
    private void explode(boolean generateLight, boolean hit, Vector3d offset) {
        Vector3d pos = new Vector3d(gameObject.transform.position).sub(offset);
        Scene scene = gameObject.getScene();

        // Creates a new explosion light prefab.
        if(generateLight) {
            GameObject explosionLight = new ExplosionLightPrefab().create(scene);
            explosionLight.transform.position.set(pos);
            scene.add(explosionLight);
        }

        // Creates a 2D Sprite prefab and adds it to the game objects.
        for(int i = 0; i < 2; ++i) {
            Sprite particleSprite = new Sprite(AssetPool.getTexture("goliath/images/square.png"));
            particleSprite.setWidth(1);
            particleSprite.setHeight(1);
            GameObject particle = new ParticlePrefab(level, 20, Math.random() * 0.4 + 0.4, particleSprite).create(scene);
            particle.transform.position.set(pos);
            // Set the color of the SpriteRenderer.
            if(hit) {
                particle.transform.scale.set(0.4);
                particle.getComponent(SpriteRenderer.class, "Renderer").setColor(new Color(0.3, 0.05, 0.15));
            } else {
                particle.transform.scale.set(0.2);
                particle.getComponent(SpriteRenderer.class, "Renderer").setColor(new Color(0.5, 0.35, 0.25));
            }
            scene.add(particle);
        }

        gameObject.remove();
    }
}

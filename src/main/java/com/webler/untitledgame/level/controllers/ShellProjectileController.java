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
        super(level, collider, 100, direction);
        gravity = 0;
        friction = 0;
    }

    @Override
    protected void didCollides(Vector3d axis) {
        Vector3d offset = new Vector3d(axis).mul(axis.dot(direction) > 0 ? 1 : -1);
        explode(true, false, offset);
    }

    @Override
    protected void didCollidesWithEntity(GameObject entity) {
        explode(false, true, new Vector3d());
    }

    private void explode(boolean generateLight, boolean hit, Vector3d offset) {
        Vector3d pos = new Vector3d(gameObject.transform.position).sub(offset);
        Scene scene = gameObject.getScene();

        if(generateLight) {
            GameObject explosionLight = new ExplosionLightPrefab().create(scene);
            explosionLight.transform.position.set(pos);
            scene.add(explosionLight);
        }

        for(int i = 0; i < 2; ++i) {
            Sprite particleSprite = new Sprite(AssetPool.getTexture("goliath/images/square.png"));
            particleSprite.setWidth(1);
            particleSprite.setHeight(1);
            GameObject particle = new ParticlePrefab(level, 20, Math.random() * 0.4 + 0.4, particleSprite).create(scene);
            particle.transform.position.set(pos);
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

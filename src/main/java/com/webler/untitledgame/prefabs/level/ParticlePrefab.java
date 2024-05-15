package com.webler.untitledgame.prefabs.level;

import com.webler.goliath.colliders.BoxCollider3D;
import com.webler.goliath.core.GameObject;
import com.webler.goliath.core.Scene;
import com.webler.goliath.graphics.Sprite;
import com.webler.goliath.graphics.components.Billboard;
import com.webler.goliath.graphics.components.SpriteRenderer;
import com.webler.goliath.prefabs.Prefab;
import com.webler.untitledgame.components.Level;
import com.webler.untitledgame.level.controllers.ParticleController;
import org.joml.Vector3d;

public class ParticlePrefab implements Prefab {
    private Level level;
    private double speed;
    private double lifetime;
    private Sprite sprite;

    public ParticlePrefab(Level level, double speed, double lifetime, Sprite sprite) {
        this.level = level;
        this.speed = speed;
        this.lifetime = lifetime;
        this.sprite = sprite;
    }

    @Override
    public GameObject create(Scene scene) {
        GameObject go = new GameObject(scene);
        Vector3d direction = new Vector3d(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5).normalize();
        BoxCollider3D collider = new BoxCollider3D(new Vector3d(0, 0, 0));
        go.addComponent("Collider", collider);
        go.addComponent("Controller", new ParticleController(level, collider, speed, lifetime, direction));
        go.addComponent("Renderer", new SpriteRenderer(sprite, -1));
        go.addComponent("Bilboard", new Billboard());
        return go;
    }
}

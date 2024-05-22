package com.webler.untitledgame.level.controllers;

import com.webler.goliath.audio.AudioManager;
import com.webler.goliath.core.GameObject;
import com.webler.goliath.core.Scene;
import com.webler.goliath.utils.AssetPool;
import com.webler.untitledgame.components.Level;
import com.webler.untitledgame.level.Projectile;
import com.webler.untitledgame.prefabs.level.ProjectilePrefab;
import org.joml.Vector2d;

public class AssaultRifleController extends GunController {
    public AssaultRifleController(Level level, String itemName) {
        super(level, itemName, 0.15, Projectile.NAIL, new Vector2d(1.5, -0.05), new Vector2d(2.5, 0.2));
    }

    /**
    * Shoots the projectile to the game object. This is called by the GameObject#shoot
    */
    @Override
    protected void shoot() {
        Scene scene = getGameObject().getScene();
        GameObject projectile = new ProjectilePrefab(level, getProjectileType(), yaw, pitch).create(scene);
        projectile.transform.position.set(getProjectilePosition());
        scene.add(projectile);
        AudioManager.play(AssetPool.getSound("untitled-game/sounds/gun.ogg").getBufferId());
    }
}

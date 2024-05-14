package com.webler.untitledgame.level.controllers;

import com.webler.goliath.audio.AudioManager;
import com.webler.goliath.core.GameObject;
import com.webler.goliath.core.Scene;
import com.webler.goliath.utils.AssetPool;
import com.webler.untitledgame.components.Level;
import com.webler.untitledgame.level.Projectile;
import com.webler.untitledgame.prefabs.level.ProjectilePrefab;
import org.joml.Vector2d;

public class ShotgunController extends GunController{
    public ShotgunController(Level level) {
        super(level, 1.0, Projectile.SHELL, new Vector2d(2, -0.4), new Vector2d(3, 0.0));
    }

    @Override
    protected void shoot() {
        for(int i = 0; i < 5; ++i) {
            double yawOffset = (Math.random() * 2 - 1) * 0.1;
            double pitchOffset = (Math.random() * 2 - 1) * 0.1;
            Scene scene = getGameObject().getScene();
            GameObject projectile = new ProjectilePrefab(level, getProjectileType(), yaw + yawOffset, pitch + pitchOffset).create(scene);
            projectile.transform.position.set(getProjectilePosition());
            scene.add(projectile);
            AudioManager.play(AssetPool.getSound("untitled-game/sounds/biggun.ogg").getBufferId());
        }
    }
}

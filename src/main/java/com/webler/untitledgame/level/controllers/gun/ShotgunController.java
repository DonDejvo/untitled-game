package com.webler.untitledgame.level.controllers.gun;

import com.webler.goliath.audio.AudioManager;
import com.webler.goliath.core.GameObject;
import com.webler.goliath.core.Scene;
import com.webler.goliath.utils.AssetPool;
import com.webler.untitledgame.level.Level;
import com.webler.untitledgame.level.enums.Projectile;
import com.webler.untitledgame.level.prefabs.ProjectilePrefab;
import org.joml.Vector2d;

public class ShotgunController extends GunController {
    public ShotgunController(Level level, String itemName) {
        super(level, itemName, 0.8, Projectile.SHELL, new Vector2d(2, -0.4), new Vector2d(3, 0.0));
    }

    /**
    * Shoots the projectile. This is called every time the player tries to spawn a new projectile
    */
    @Override
    protected void shoot() {
        // This method creates 5 projectiles and adds a projectile prefab to the game.
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

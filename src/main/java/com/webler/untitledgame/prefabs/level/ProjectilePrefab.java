package com.webler.untitledgame.prefabs.level;

import com.webler.goliath.colliders.BoxCollider3D;
import com.webler.goliath.core.GameObject;
import com.webler.goliath.core.Scene;
import com.webler.goliath.graphics.Color;
import com.webler.goliath.graphics.components.MeshRenderer;
import com.webler.goliath.graphics.geometry.Cube;
import com.webler.goliath.prefabs.Prefab;
import com.webler.goliath.utils.AssetPool;
import com.webler.untitledgame.components.Level;
import com.webler.untitledgame.level.Projectile;
import com.webler.untitledgame.level.controllers.NailProjectileController;
import com.webler.untitledgame.level.controllers.ShellProjectileController;
import org.joml.Vector3d;

public class ProjectilePrefab implements Prefab {
    private final Level level;
    private final Projectile type;
    private final double yaw;
    private final double pitch;

    public ProjectilePrefab(Level level, Projectile type, double yaw, double pitch) {
        this.type = type;
        this.level = level;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    /**
    * Creates a projectile GameObject. This is called by Scene#create ( java. util. Collection ) to create the projectile's game object.
    * 
    * @param scene - The scene to create the projectile in.
    * 
    * @return The created projectile GameObject for the scene or null if creation failed for some reason ( such as an invalid scene
    */
    @Override
    public GameObject create(Scene scene) {
        GameObject go = new GameObject(scene);
        Vector3d direction = new Vector3d(0, 0, 1).rotateX(pitch).rotateY(yaw + Math.PI / 2).normalize();
        MeshRenderer renderer = new MeshRenderer(new Cube(AssetPool.getTexture("goliath/images/square.png").getTexId()));
        go.addComponent("Renderer", renderer);
        BoxCollider3D collider = new BoxCollider3D(new Vector3d(0, 0, 0));
        go.addComponent("Collider", collider);
        renderer.setColor(Color.BLACK);
        go.transform.scale.set(0.1, 0.1, 0.1);
        // Add the controller to the game.
        switch(type) {
            case SHELL: {
                go.addComponent("Controller", new ShellProjectileController(level, collider, direction));
                break;
            }
            case NAIL: {
                go.addComponent("Controller", new NailProjectileController(level, collider, direction));
                break;
            }
        }
        return go;
    }
}

package com.webler.goliath.prefabs;

import com.webler.goliath.core.GameObject;
import com.webler.goliath.core.Scene;
import com.webler.goliath.graphics.components.Camera;
import com.webler.goliath.graphics.components.PerspectiveCamera;

public class PerspectiveCameraPrefab implements Prefab {
    private final double fov;
    private final double near;
    private final double far;

    public PerspectiveCameraPrefab(double fov, double near, double far) {
        this.fov = fov;
        this.near = near;
        this.far = far;
    }

    @Override
    public GameObject create(Scene scene) {
        GameObject cameraGameObject = new GameObject(scene, "Camera");
        Camera camera = new PerspectiveCamera(fov, 1920, 1080, near, far);
        cameraGameObject.addComponent("Camera", camera);
        scene.setCamera(camera);
        return cameraGameObject;
    }
}

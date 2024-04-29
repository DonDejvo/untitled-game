package com.webler.untitledgame.components;

import com.webler.goliath.core.Component;
import com.webler.goliath.core.GameObject;
import com.webler.goliath.graphics.components.Camera;
import org.joml.Vector3d;


// TODO: This class is only for learning purpose and won't be part of the final product. Don't forget to remove!

public class CameraFollower extends Component {
    public GameObject cameraGameObject;
    public Vector3d offset;

    public CameraFollower(GameObject cameraGameObject, Vector3d offset) {
        this.cameraGameObject = cameraGameObject;
        this.offset = offset;
    }

    @Override
    public void start() {

    }

    @Override
    public void update(double dt) {
        HeroController heroController = gameObject.getComponent(HeroController.class, "Controller");

        cameraGameObject.getComponent(Camera.class, "Camera").direction.set(heroController.direction);
        cameraGameObject.transform.position.set(gameObject.transform.position);
    }

    @Override
    public void destroy() {

    }
}

package com.webler.goliath.graphics.components;

import com.webler.goliath.core.Component;
import org.joml.Matrix3d;
import org.joml.Vector3d;

public class Billboard extends Component {

    @Override
    public void start() {

    }

    @Override
    public void update(double dt) {
        Camera camera = gameObject.getScene().getCamera();
        Vector3d cameraPosition = camera.getGameObject().transform.position;
        Vector3d targetPosition = new Vector3d(cameraPosition.x, gameObject.transform.position.y, cameraPosition.z);
        Matrix3d mat = new Matrix3d();
        mat.rotationTowards(targetPosition.sub(gameObject.transform.position), new Vector3d(0, 1, 0));
        gameObject.transform.rotation.setFromNormalized(mat);
    }

    @Override
    public void destroy() {

    }
}

package com.webler.goliath.graphics.components;

import com.webler.goliath.core.Component;
import org.joml.Matrix3d;
import org.joml.Vector3d;

public class Billboard extends Component {

    /**
    * Called when the server is started. This is where we start the web server and the server's state is maintained
    */
    @Override
    public void start() {

    }

    /**
    * Updates the rotation of the game object. This is called every frame to ensure that the object is up to date
    * 
    * @param dt - time since last update
    */
    @Override
    public void update(double dt) {
        Camera camera = gameObject.getScene().getCamera();
        Vector3d cameraPosition = camera.getGameObject().transform.position;
        Vector3d targetPosition = new Vector3d(cameraPosition.x, gameObject.transform.position.y, cameraPosition.z);
        Matrix3d mat = new Matrix3d();
        mat.rotationTowards(targetPosition.sub(gameObject.transform.position), new Vector3d(0, 1, 0));
        gameObject.transform.rotation.setFromNormalized(mat);
    }

    /**
    * Called when the component is no longer needed. This is the place to do any cleanup that needs to be done
    */
    @Override
    public void destroy() {

    }
}

package com.webler.goliath.core.components;

import com.webler.goliath.core.Component;
import lombok.Getter;
import org.joml.*;

public class Transform extends Component {
    public final Vector3d position;
    public final Quaterniond rotation;
    public final Vector3d scale;
    @Getter
    private final Matrix4d matrix;

    public Transform() {
        position = new Vector3d();
        rotation = new Quaterniond();
        scale = new Vector3d(1.0, 1.0, 1.0);
        matrix = new Matrix4d();
    }

    public Transform(Transform transform) {
        position = new Vector3d(transform.position);
        rotation = new Quaterniond(transform.rotation);
        scale = new Vector3d(transform.scale);
        matrix = new Matrix4d();
    }

    /**
    * Called when the game starts. This is where we update the matrix to reflect the changes in the world
    */
    @Override
    public void start() {
        updateMatrix();
    }

    /**
    * Updates the matrix. This is called every frame during the update loop. The matrix is updated by calling updateMatrix ()
    * 
    * @param dt - time since the last
    */
    @Override
    public void update(double dt) {
        updateMatrix();
    }

    /**
    * Called when the component is no longer needed. This is the place to do any cleanup that needs to be done
    */
    @Override
    public void destroy() {

    }

    /**
    * Updates the transformation matrix to reflect the current position rotation and scale. This is called by the constructor to ensure that the matrix is up to date
    */
    public void updateMatrix() {
        matrix.identity()
                .translate(position)
                .rotate(rotation)
                .scale(scale);
    }

}

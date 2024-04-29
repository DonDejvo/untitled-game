package com.webler.goliath.core.components;

import com.webler.goliath.core.Component;
import org.joml.*;

public class Transform extends Component {
    public final Vector3d position;
    public final Quaterniond rotation;
    public final Vector3d scale;
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

    @Override
    public void start() {
        updateMatrix();
    }

    @Override
    public void update(double dt) {
        updateMatrix();
    }

    @Override
    public void destroy() {

    }

    public void updateMatrix() {
        matrix.identity()
                .translate(position)
                .rotate(rotation)
                .scale(scale);
    }

    public Matrix4d getMatrix() {
        return matrix;
    }
}

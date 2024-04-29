package com.webler.goliath.graphics.components;

import com.webler.goliath.core.Component;
import com.webler.goliath.math.Rect;
import org.joml.*;

public abstract class Camera extends Component {
    protected final Matrix4d viewMatrix;
    protected final Matrix4d projectionMatrix;
    public final Vector3d direction;
    protected final Vector3d up;
    protected final Vector3d right;
    protected int viewportWidth;
    protected int viewportHeight;
    protected Matrix4d PVMatrix;
    protected Matrix4d inversePVMatrix;

    public Camera(int viewportWidth, int viewportHeight) {
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        viewMatrix = new Matrix4d();
        projectionMatrix = new Matrix4d();
        direction = new Vector3d(0, 0, -1);
        up = new Vector3d(0, 1, 0);
        right = new Vector3d();
        PVMatrix = new Matrix4d();
        inversePVMatrix = new Matrix4d();
    }

    public void updateView() {
        direction.normalize();
        right.set(new Vector3d(0, 1, 0).cross(direction));
        up.set(new Vector3d(direction).cross(right));

        Vector3d center = new Vector3d(gameObject.transform.position).add(direction);
        viewMatrix.identity().lookAt(gameObject.transform.position, center, up);

    }

    public abstract void updateProjection();

    public Matrix4d getProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4d getViewMatrix() {
        return viewMatrix;
    }

    public Matrix4d getPVMatrix() {
        return new Matrix4d(PVMatrix);
    }

    public Matrix4d getInversePVMatrix() {
        return inversePVMatrix;
    }

    public void setViewport(int width, int height) {
        viewportWidth = width;
        viewportHeight = height;
    }

    public Rect getViewport() {
        return new Rect(0, 0, viewportWidth, viewportHeight);
    }

    @Override
    public void start() {

    }

    @Override
    public void update(double dt) {
        viewportWidth = gameObject.getGame().getWidth();
        viewportHeight = gameObject.getGame().getHeight();
        updateProjection();
        updateView();
        PVMatrix.set(projectionMatrix).mul(viewMatrix);
        inversePVMatrix.set(PVMatrix).invert();
    }

    @Override
    public void destroy() {

    }

    public Vector2d getWorldPosFromScreenCoords(double screenX, double screenY) {
        return getWorldPosFromScreenCoords(screenX, screenY, 0, 0);
    }

    public Vector2d getWorldPosFromScreenCoords(double screenX, double screenY, double offsetX, double offsetY) {
        int screenWidth = gameObject.getGame().getWidth();
        int screenHeight = gameObject.getGame().getHeight();
        Vector4d v = new Vector4d((screenX - offsetX) / screenWidth * 2 - 1, (1 - (screenY - offsetY) / screenHeight) * 2 - 1, 0.0, 1.0);
        v.mul(inversePVMatrix);
        return new Vector2d(v.x, v.y);
    }
}

package com.webler.goliath.graphics.components;

import com.webler.goliath.core.Component;
import com.webler.goliath.graphics.Color;
import com.webler.goliath.math.Rect;
import lombok.Getter;
import lombok.Setter;
import org.joml.*;

public abstract class Camera extends Component {
    @Getter
    protected final Matrix4d viewMatrix;
    @Getter
    protected final Matrix4d projectionMatrix;
    public final Vector3d direction;
    protected final Vector3d up;
    protected final Vector3d right;
    protected int viewportWidth;
    protected int viewportHeight;
    protected Matrix4d PVMatrix;
    @Getter
    protected Matrix4d inversePVMatrix;
    @Setter
    @Getter
    private Color backgroundColor;

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
        backgroundColor = Color.BLACK;
    }

    /**
    * Updates the view matrix to reflect the direction of the game object. This is called every frame by the view
    */
    public void updateView() {
        direction.normalize();
        right.set(new Vector3d(0, 1, 0).cross(direction));
        up.set(new Vector3d(direction).cross(right));

        Vector3d center = new Vector3d(gameObject.transform.position).add(direction);
        viewMatrix.identity().lookAt(gameObject.transform.position, center, up);

    }

    /**
    * Updates the projection. This is called when the user clicks on the button to make changes to the project
    */
    public abstract void updateProjection();

    /**
    * Get the PV matrix. This is a view matrix that transforms points from the local coordinate system to the frame's local coordinate system.
    * 
    * 
    * @return the PV matrix for this transformation as a Matrix4d object. Never null. The returned matrix is thread - safe
    */
    public Matrix4d getPVMatrix() {
        return new Matrix4d(PVMatrix);
    }

    /**
    * Returns the viewport of the game object. Note that this will be a rectangle that has the same dimensions as the game object's viewport.
    * 
    * 
    * @return A rectangle that has the same dimensions as the game object's viewport and with the same x and y coordinates
    */
    public Rect getViewport() {
        return new Rect(gameObject.transform.position.x - viewportWidth * 0.5,
                gameObject.transform.position.y - viewportHeight * 0.5,
                viewportWidth, viewportHeight);
    }

    /**
    * Called when the server is started. This is where we start the web server and the server's state is maintained
    */
    @Override
    public void start() {

    }

    /**
    * Updates the view and projection matrices. This is called every frame to update the view and projection matrices.
    * 
    * @param dt - time since last frame ( in seconds ). Not used
    */
    @Override
    public void update(double dt) {
        viewportWidth = gameObject.getGame().getWidth();
        viewportHeight = gameObject.getGame().getHeight();
        updateProjection();
        updateView();
        PVMatrix.set(projectionMatrix).mul(viewMatrix);
        inversePVMatrix.set(PVMatrix).invert();
    }

    /**
    * Called when the component is no longer needed. This is the place to do any cleanup that needs to be done
    */
    @Override
    public void destroy() {

    }

    /**
    * Converts screen coordinates to world coordinates. This is equivalent to calling #getWorldPosFromScreenCoords ( double double double ) with 0 0 as the z component and 1 as the w component.
    * 
    * @param screenX - The x coordinate to convert. Must be less than or equal to 0.
    * @param screenY - The y coordinate to convert. Must be less than or equal to 0.
    * 
    * @return The world coordinates corresponding to the given screen coordinates. If the coordinates are out of range a Vector2d is returned
    */
    public Vector2d getWorldPosFromScreenCoords(double screenX, double screenY) {
        return getWorldPosFromScreenCoords(screenX, screenY, 0, 0);
    }

    /**
    * Converts screen coordinates to world coordinates. This is useful for transforming a point on the screen ( such as the mouse click ) to a point on the game object that is visible on the screen
    * 
    * @param screenX - X coordinate of the point on the screen
    * @param screenY - Y coordinate of the point on the screen
    * @param offsetX - Offset X coordinate of the point on the screen
    * @param offsetY - Offset Y coordinate of the point on the screen
    * 
    * @return World position of the point on the game object as a Vector2d object with x and y set to
    */
    public Vector2d getWorldPosFromScreenCoords(double screenX, double screenY, double offsetX, double offsetY) {
        int screenWidth = gameObject.getGame().getWidth();
        int screenHeight = gameObject.getGame().getHeight();
        Vector4d v = new Vector4d((screenX - offsetX) / screenWidth * 2 - 1, (1 - (screenY - offsetY) / screenHeight) * 2 - 1, 0.0, 1.0);
        v.mul(inversePVMatrix);
        return new Vector2d(v.x, v.y);
    }

}

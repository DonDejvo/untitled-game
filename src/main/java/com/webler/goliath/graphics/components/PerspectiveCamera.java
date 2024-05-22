package com.webler.goliath.graphics.components;

public class PerspectiveCamera extends Camera {
    public double fov;
    public double near;
    public double far;

    public PerspectiveCamera(double fov, int viewportWidth, int viewportHeight, double near, double far) {
        super(viewportWidth, viewportHeight);

        this.fov = fov;
        this.near = near;
        this.far = far;
    }

    /**
    * Updates the projection matrix to reflect the view frustum. This is called by Camera#setView ( Camera )
    */
    @Override
    public void updateProjection() {
        projectionMatrix.identity()
                .perspective(fov, (double) viewportWidth / viewportHeight, near, far);
    }
}

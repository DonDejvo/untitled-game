package com.webler.goliath.graphics.components;

public class OrthoCamera extends Camera{

    public OrthoCamera(int viewportWidth, int viewportHeight) {
        super(viewportWidth, viewportHeight);
    }

    /**
    * Updates the projection matrix to reflect the size of the game object. This is called when the viewport size changes
    */
    @Override
    public void updateProjection() {
        double halfWidth = viewportWidth * 0.5 / getGameObject().transform.scale.x;
        double halfHeight = viewportHeight * 0.5 * getGameObject().transform.scale.y;
        projectionMatrix.identity()
                .ortho(-halfWidth, halfWidth, halfHeight, -halfHeight, 0, 100);
    }
}

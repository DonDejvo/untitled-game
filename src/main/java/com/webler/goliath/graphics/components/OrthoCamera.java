package com.webler.goliath.graphics.components;

public class OrthoCamera extends Camera{

    public OrthoCamera(int viewportWidth, int viewportHeight) {
        super(viewportWidth, viewportHeight);
    }

    @Override
    public void updateProjection() {
        double halfWidth = viewportWidth * 0.5 / getGameObject().transform.scale.x;
        double halfHeight = viewportHeight * 0.5 * getGameObject().transform.scale.y;
        projectionMatrix.identity()
                .ortho(-halfWidth, halfWidth, halfHeight, -halfHeight, 0, 100);
    }
}

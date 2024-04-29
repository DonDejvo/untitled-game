package com.webler.goliath.graphics.light;

import com.webler.goliath.core.Component;
import com.webler.goliath.graphics.Color;

public class SpotLight extends Component {
    private double radiusMin;
    private double radiusMax;
    private Color color;
    private double intensity;

    public SpotLight(Color color, double radiusMin, double radiusMax) {
        this.color = color;
        this.radiusMin = radiusMin;
        this.radiusMax = radiusMax;
        this.intensity = 1;
    }

    @Override
    public void start() {
        getEntity().getGame().getRenderer().add(this);
    }

    @Override
    public void update(double dt) {

    }

    @Override
    public void destroy() {
        getEntity().getGame().getRenderer().remove(this);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public double getRadiusMax() {
        return radiusMax;
    }

    public void setRadiusMax(double radiusMax) {
        this.radiusMax = radiusMax;
    }

    public double getRadiusMin() {
        return radiusMin;
    }

    public void setRadiusMin(double radiusMin) {
        this.radiusMin = radiusMin;
    }

    public double getIntensity() {
        return intensity;
    }

    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }
}

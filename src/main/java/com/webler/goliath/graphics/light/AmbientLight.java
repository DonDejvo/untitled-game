package com.webler.goliath.graphics.light;

import com.webler.goliath.core.Component;
import com.webler.goliath.graphics.Color;

public class AmbientLight extends Component {
    private double intensity;
    private Color color;

    public AmbientLight(Color color) {
        this.color = color;
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

    public double getIntensity() {
        return intensity;
    }

    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}

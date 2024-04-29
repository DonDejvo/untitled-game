package com.webler.goliath.graphics.components;

import com.webler.goliath.core.Component;
import com.webler.goliath.graphics.Color;

public class Fog extends Component {
    private boolean enabled;
    private double fogNear;
    private double fogFar;
    private Color fogColor;

    public Fog(double fogNear, double fogFar, Color fogColor) {
        this.enabled = true;
        this.fogNear = fogNear;
        this.fogFar = fogFar;
        this.fogColor = fogColor;
    }

    @Override
    public void start() {
        getEntity().getGame().getRenderer().setFog(this);
    }

    @Override
    public void update(double dt) {

    }

    @Override
    public void destroy() {
        getEntity().getGame().getRenderer().setFog(null);
    }

    public Color getFogColor() {
        return fogColor;
    }

    public void setFogColor(Color fogColor) {
        this.fogColor = fogColor;
    }

    public double getFogFar() {
        return fogFar;
    }

    public void setFogFar(double fogFar) {
        this.fogFar = fogFar;
    }

    public double getFogNear() {
        return fogNear;
    }

    public void setFogNear(double fogNear) {
        this.fogNear = fogNear;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

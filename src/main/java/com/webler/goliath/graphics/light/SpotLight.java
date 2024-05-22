package com.webler.goliath.graphics.light;

import com.webler.goliath.core.Component;
import com.webler.goliath.graphics.Color;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
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

    /**
    * Called when the player starts. This is where we add the player to the renderer's list of
    */
    @Override
    public void start() {
        getGameObject().getGame().getRenderer().add(this);
    }

    /**
    * Updates the progress bar. This is called every frame to indicate the progress of the animation. The time in seconds since the last call to update () is given by dt
    * 
    * @param dt - the time since the last
    */
    @Override
    public void update(double dt) {

    }

    /**
    * Removes this renderer from the game's renderer list. This is called when the game is no longer in
    */
    @Override
    public void destroy() {
        getGameObject().getGame().getRenderer().remove(this);
    }

}

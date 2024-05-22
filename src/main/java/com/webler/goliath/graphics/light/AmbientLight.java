package com.webler.goliath.graphics.light;

import com.webler.goliath.core.Component;
import com.webler.goliath.graphics.Color;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AmbientLight extends Component {
    private double intensity;
    private Color color;

    public AmbientLight(Color color) {
        this.color = color;
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

package com.webler.goliath.utils;

import com.webler.goliath.core.Component;
import com.webler.goliath.graphics.Color;
import com.webler.goliath.graphics.canvas.Canvas;
import lombok.Getter;

public class FPSCounter extends Component {
    @Getter
    private int fps;
    private int frames;
    private double accumulatedTime;

    public FPSCounter() {
        fps = 0;
    }

    /**
    * Called when the server is started. This is where we start the web server and the server's state is maintained
    */
    @Override
    public void start() {

    }

    /**
    * Updates the animation. This is called every frame to update the animation. If you override this method be sure to call super. update ( deltaTime ) in order to ensure that the animation is updated properly
    * 
    * @param deltaTime - the time since the last
    */
    @Override
    public void update(double deltaTime) {
        accumulatedTime += deltaTime;
        ++frames;
        // Sets the accumulated time to 0. 0.
        if (accumulatedTime >= 1.0) {
            fps = frames;
            accumulatedTime = 0;
            frames = 0;
        }

        draw();
    }

    /**
    * Called when the component is no longer needed. This is the place to do any cleanup that needs to be done
    */
    @Override
    public void destroy() {

    }

    /**
    * Draws the FPS on the screen to indicate the size of the game. This is called by update
    */
    private void draw() {
        Canvas canvas = gameObject.getScene().getGame().getCanvas();
        canvas.setColor(Color.WHITE);
        canvas.setFontSize(16);
        canvas.text("FPS: " + fps, 8, 8);
    }
}

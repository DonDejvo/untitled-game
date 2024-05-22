package com.webler.goliath.graphics.components;

import com.webler.goliath.animation.Animable;
import com.webler.goliath.core.Component;
import com.webler.goliath.graphics.Color;
import com.webler.goliath.graphics.Sprite;
import com.webler.goliath.math.Rect;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3d;

public class SpriteRenderer extends Component implements Animable {
    @Setter
    @Getter
    private Sprite sprite;
    @Getter
    private int zIndex;
    @Setter
    @Getter
    private Color color;
    public double angle;

    public SpriteRenderer(Sprite sprite, int zIndex) {
        this.sprite = sprite;
        this.color = Color.WHITE;
        this.zIndex = zIndex;
        this.angle = 0;
    }

    /**
    * Called when the player starts. This is where we add the renderer to the game object's renderer
    */
    @Override
    public void start() {
        gameObject.getGame().getRenderer().add(this);
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
    * Removes this renderer from the game's renderer list. This is called when the renderer is no longer needed
    */
    @Override
    public void destroy() {
        gameObject.getGame().getRenderer().remove(this);
    }

    /**
    * Sets the Z - index of the player. This can be used to move the player to a different location in the Z - index when it is dragged.
    * 
    * @param zIndex - The Z - index to set the player
    */
    public void setzIndex(int zIndex) {
        // Sets the zIndex of this Game Object to the given zIndex.
        if(this.zIndex != zIndex) {
            this.zIndex = zIndex;
            gameObject.getGame().getRenderer().remove(this);
            gameObject.getGame().getRenderer().add(this);
        }
    }

    /**
    * Returns the bounding rect of the Sprite. It is assumed that the Sprite has been positioned at the center of the screen.
    * 
    * 
    * @return the bounding rect of the Sprite. It is assumed that the Sprite has been positioned at the center of the screen
    */
    public Rect getBoundingRect() {
        Vector3d offsetPosition = getOffsetPosition();
        double width = gameObject.transform.scale.x * sprite.getWidth();
        double height = gameObject.transform.scale.y * sprite.getHeight();
        return new Rect(offsetPosition.x - width / 2, offsetPosition.y - height / 2, width, height);
    }

    /**
    * Sets the frame at the specified coordinates. This is useful for drawing a frame of text on the screen.
    * 
    * @param x - The x coordinate of the frame. It is assumed that the top left corner of the text is at 0 0.
    * @param y - The y coordinate of the frame. It is assumed that the top left corner of the text is at 0 0.
    * @param frameWidth - The width of the frame. It is assumed that the top left corner of the text is at 0 0.
    * @param frameHeight - The height of the frame. It is assumed that the top left corner of the text is at 0 0
    */
    @Override
    public void setFrame(int x, int y, int frameWidth, int frameHeight) {
        sprite.setRegion(x * frameWidth, y * frameHeight, frameWidth, frameHeight);
    }
}

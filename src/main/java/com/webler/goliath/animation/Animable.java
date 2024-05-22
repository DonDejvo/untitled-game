package com.webler.goliath.animation;

public interface Animable {

    /**
    * Sets the position and size of a frame. This is used to draw the animation on the screen.
    * 
    * @param x - The x coordinate of the top left corner of the frame.
    * @param y - The y coordinate of the top left corner of the frame.
    * @param frameWidth - The width of the frame. If frameWidth is less than or equal to 0 the frame is drawn with no effect.
    * @param frameHeight - The height of the frame. If frameWidth is less than or equal to 0 the frame is drawn with no effect
    */
    public void setFrame(int x, int y, int frameWidth, int frameHeight);

}

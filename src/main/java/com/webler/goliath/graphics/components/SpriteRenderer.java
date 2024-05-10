package com.webler.goliath.graphics.components;

import com.webler.goliath.animation.Animable;
import com.webler.goliath.core.Component;
import com.webler.goliath.graphics.Color;
import com.webler.goliath.graphics.Sprite;
import com.webler.goliath.math.Rect;
import org.joml.Vector3d;

public class SpriteRenderer extends Component implements Animable {
    private Sprite sprite;
    private int zIndex;
    private Color color;
    public double angle;

    public SpriteRenderer(Sprite sprite, int zIndex) {
        this.sprite = sprite;
        this.color = Color.WHITE;
        this.zIndex = zIndex;
        this.angle = 0;
    }

    @Override
    public void start() {
        gameObject.getGame().getRenderer().add(this);
    }

    @Override
    public void update(double dt) {

    }

    @Override
    public void destroy() {
        gameObject.getGame().getRenderer().remove(this);
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public int getzIndex() {
        return zIndex;
    }

    public void setzIndex(int zIndex) {
        if(this.zIndex != zIndex) {
            this.zIndex = zIndex;
            gameObject.getGame().getRenderer().remove(this);
            gameObject.getGame().getRenderer().add(this);
        }
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Rect getBoundingRect() {
        Vector3d offsetPosition = getOffsetPosition();
        double width = gameObject.transform.scale.x * sprite.getWidth();
        double height = gameObject.transform.scale.y * sprite.getHeight();
        return new Rect(offsetPosition.x - width / 2, offsetPosition.y - height / 2, width, height);
    }

    @Override
    public void setFrame(int x, int y, int frameWidth, int frameHeight) {
        sprite.setRegion(x * frameWidth, y * frameHeight, frameWidth, frameHeight);
    }
}

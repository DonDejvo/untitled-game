package com.webler.untitledgame.level.levelmap;

import lombok.Getter;
import lombok.Setter;
import org.w3c.dom.Element;

@Setter
@Getter
public class Platform implements Serializable {
    public static final String TAG = "platform";
    private int x, y, width, height, top, ceiling;

    public Platform(int x, int y, int width, int height, int top, int ceiling) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.top = top;
        this.ceiling = ceiling;
    }

    public Platform() {
    }

    @Override
    public void deserialize(Element platformElement) {
        int width = Integer.parseInt(platformElement.getAttribute("width"));
        int height = Integer.parseInt(platformElement.getAttribute("height"));
        int top = Integer.parseInt(platformElement.getAttribute("top"));
        int x = Integer.parseInt(platformElement.getAttribute("x"));
        int y = Integer.parseInt(platformElement.getAttribute("y"));
        int ceiling = Integer.parseInt(platformElement.getAttribute("ceiling"));

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.top = top;
        this.ceiling = ceiling;
    }

    @Override
    public void serialize(Element platformElement) {
        platformElement.setAttribute("width", Integer.toString(width));
        platformElement.setAttribute("height", Integer.toString(height));
        platformElement.setAttribute("top", Integer.toString(top));
        platformElement.setAttribute("x", Integer.toString(x));
        platformElement.setAttribute("y", Integer.toString(y));
        platformElement.setAttribute("ceiling", Integer.toString(ceiling));
    }
}
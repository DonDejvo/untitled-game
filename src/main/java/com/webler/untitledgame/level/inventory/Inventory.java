package com.webler.untitledgame.level.inventory;

import com.webler.goliath.core.Component;
import com.webler.goliath.dialogs.Dialog;
import com.webler.goliath.graphics.Color;
import com.webler.goliath.graphics.canvas.Canvas;
import com.webler.goliath.graphics.canvas.TextAlign;
import com.webler.goliath.graphics.ui.UIElements;
import com.webler.goliath.input.Input;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_I;

public class Inventory extends Component {
    private boolean isOpened;

    @Override
    public void start() {

    }

    @Override
    public void update(double dt) {
        if(isOpened) {
            drawWindow();
        }
    }

    @Override
    public void destroy() {

    }

    private void drawWindow() {
        UIElements ui = getEntity().getGame().getUiElements();
        Canvas ctx = getEntity().getGame().getCanvas();
        int w = ctx.getWidth(), h = ctx.getHeight();

        ui.padding.set(h * 0.01f);
        ui.lineHeight = h * 0.04f;
        ui.begin((w - h * 1.2f) / 2, h * 0.1f, h * 1.2f, h * 0.8f);



        ui.end();
    }

    public boolean isOpened() {
        return isOpened;
    }

    public void setOpened(boolean opened) {
        isOpened = opened;
    }
}

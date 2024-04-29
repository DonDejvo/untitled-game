package com.webler.goliath.graphics.ui;

import com.webler.goliath.graphics.Color;
import com.webler.goliath.graphics.canvas.Canvas;
import com.webler.goliath.input.Input;
import com.webler.goliath.math.Rect;
import org.joml.Vector2d;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class UIElements {
    private Canvas canvas;
    private UITheme theme;

    public UIElements(Canvas canvas) {
        this.canvas = canvas;
        theme = new UITheme(new Color(0.25, 0.25, 0.25),
                Color.WHITE,
                Color.BLACK,
                Color.RED,
                Color.WHITE,
                new Vector2f(10, 10),
                32);
    }

    public void label(String text, float x, float y) {
        canvas.setFontSize(theme.fontSize());
        canvas.setColor(theme.textColor());
        canvas.text(text, x, y);
    }

    public void textBlock(String text, float x, float y, float width) {
        String[] words = text.split("( )+");
        canvas.setFontSize(theme.fontSize());
        List<String> lines = new ArrayList<>();
        StringBuilder line = new StringBuilder();
        int wordCount = 0;
        for (String word : words) {
            float lineWidth = canvas.computeTextWidth(line + " " + word);
            if (wordCount > 0 && lineWidth > width) {
                lines.add(line.toString());
                line = new StringBuilder();
                wordCount = 0;
            }
            if (wordCount > 0) {
                line.append(" ");
            }
            line.append(word);
            ++wordCount;
        }
        if (wordCount > 0) {
            lines.add(line.toString());
        }
        canvas.setColor(theme.textColor());
        for (int i = 0; i < lines.size(); i++) {
            canvas.text(lines.get(i), x, y + i * (theme.fontSize() + theme.padding().y));
        }
    }

    public boolean button(String text, float x, float y) {
        Vector2d pos = getTranslatedMousePosition();
        canvas.setFontSize(theme.fontSize() - theme.padding().x);
        float w = canvas.computeTextWidth(text) + theme.padding().y * 2;
        float h = theme.fontSize() + theme.padding().y;
        Rect buttonRect = new Rect(x, y, w, h);
        boolean hovered = buttonRect.contains(pos);
        boolean clicked = hovered && Input.mouseButtonBeginPress(GLFW_MOUSE_BUTTON_LEFT);

        canvas.pushTranslate(x, y);
        canvas.setColor(hovered ? theme.buttonHoverColor() : theme.buttonColor());
        canvas.rect(0 ,0, w, h);
        canvas.setColor(theme.buttonTextColor());
        canvas.text(text, theme.padding().x, theme.padding().y);
        canvas.popTranslate();

        return clicked;
    }

    public void begin(float x, float y, float w, float h) {
        canvas.pushTranslate(x, y);
        canvas.setColor(theme.backgroundColor());
        canvas.rect(0 ,0, w, h);
        canvas.pushTranslate(theme.padding().x, theme.padding().y);
    }

    public void end() {
        canvas.popTranslate();
        canvas.popTranslate();
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public UITheme getTheme() {
        return theme;
    }

    public void setTheme(UITheme theme) {
        this.theme = theme;
    }

    private Vector2d getTranslatedMousePosition() {
        Vector2f translate = canvas.getTranslate();
        return new Vector2d(Input.mouseX(), Input.mouseY()).sub(translate);
    }
}

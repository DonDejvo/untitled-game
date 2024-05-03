package com.webler.goliath.graphics.ui;

import com.webler.goliath.graphics.Color;
import com.webler.goliath.graphics.canvas.Canvas;
import com.webler.goliath.input.Input;
import com.webler.goliath.math.Rect;
import org.joml.Vector2d;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class UIElements {
    private Canvas canvas;
    public float fontSize;
    public Color textColor;
    public Color bgColor;
    public Color hoverTextColor;
    public Color hoverBgColor;
    public Vector2f padding;
    public float lineHeight;

    public UIElements(Canvas canvas) {
        this.canvas = canvas;
        fontSize = 32;
        textColor = Color.GRAY;
        bgColor = new Color(0, 0, 0, 0.5);
        hoverTextColor = Color.WHITE;
        hoverBgColor = Color.BLACK;
        lineHeight = 36;
        padding = new Vector2f(10, 10);
    }

    public void textBlock(String text, float x, float y, float width) {
        String[] words = text.split("( )+");
        canvas.setFontSize(fontSize);
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
        canvas.setColor(textColor);
        for (int i = 0; i < lines.size(); i++) {
            canvas.text(lines.get(i), x, y + i * (lineHeight));
        }
    }

    public void text(String text, float x, float y) {
        canvas.setFontSize(fontSize);
        canvas.setColor(textColor);
        canvas.text(text, x, y);
    }

    public boolean button(String text, float x, float y, float w, float h) {
        Vector2d pos = getTranslatedMousePosition();
        canvas.setFontSize(fontSize);
        Rect buttonRect = new Rect(x, y, w, h);
        boolean hovered = buttonRect.contains(pos);
        boolean clicked = hovered && Input.mouseButtonBeginPress(GLFW_MOUSE_BUTTON_LEFT);

        canvas.pushTranslate(x, y);
        canvas.setColor(hovered ? hoverBgColor : bgColor);
        canvas.rect(0 ,0, w, h);
        canvas.setColor(hovered ? hoverTextColor : textColor);
        canvas.text(text, padding.x, padding.y);
        canvas.popTranslate();

        return clicked;
    }

    public boolean button(String text, float x, float y) {
        canvas.setFontSize(fontSize);
        float w = canvas.computeTextWidth(text) + padding.x * 2;
        float h = fontSize + 2 * padding.y;
        return button(text, x, y, w, h);
    }

    public void begin(float x, float y, float w, float h) {
        canvas.pushTranslate(x, y);
        canvas.setColor(bgColor);
        canvas.rect(0 ,0, w, h);
        canvas.pushTranslate(padding.x, padding.y);
    }

    public void end() {
        canvas.popTranslate();
        canvas.popTranslate();
    }

    public Canvas getCanvas() {
        return canvas;
    }

    private Vector2d getTranslatedMousePosition() {
        Vector2f translate = canvas.getTranslate();
        return new Vector2d(Input.mouseX(), Input.mouseY()).sub(translate);
    }
}

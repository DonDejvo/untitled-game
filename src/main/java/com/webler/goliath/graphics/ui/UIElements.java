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
    private final Canvas canvas;
    public float fontSize;
    public Color textColor;
    public Color bgColor;
    public Color hoverTextColor;
    public Color hoverBgColor;
    public Vector2f padding;
    public float lineHeight;
    private boolean isNextButtonHovered;

    public UIElements(Canvas canvas) {
        this.canvas = canvas;
        fontSize = 32;
        textColor = Color.GRAY;
        bgColor = new Color(0, 0, 0, 0.5);
        hoverTextColor = Color.WHITE;
        hoverBgColor = Color.BLACK;
        lineHeight = 36;
        padding = new Vector2f(10, 10);
        isNextButtonHovered = false;
    }

    /**
    * Hover next button. Called when mouse enters next button or menu item is hovered over it. Note that this is a no - op
    */
    public void hoverNextButton() {
        isNextButtonHovered = true;
    }

    /**
    * Returns whether or not the next button is hovered. This method is called when the mouse enters the next button or when the mouse hovers over the last button.
    * 
    * 
    * @return whether or not the next button is hovered ( true ) or not ( false ). Note that this does not mean that the mouse has moved
    */
    private boolean popNextButtonHovered() {
        boolean hovered = isNextButtonHovered;
        isNextButtonHovered = false;
        return hovered;
    }

    /**
    * Draws a block of text on the canvas. The text will be split into lines based on the width of the text that is larger than the specified width
    * 
    * @param text - The text to be drawn
    * @param x - The x position of the text in canvas coordinates
    * @param y - The y position of the text in canvas coordinates
    * @param width - The maximum width of the text in canvas coordinates
    */
    public void textBlock(String text, float x, float y, float width) {
        String[] words = text.split("( )+");
        canvas.setFontSize(fontSize);
        List<String> lines = new ArrayList<>();
        StringBuilder line = new StringBuilder();
        int wordCount = 0;
        for (String word : words) {
            float lineWidth = canvas.computeTextWidth(line + " " + word);
            // Add a line to the line if the width is greater than the width.
            if (wordCount > 0 && lineWidth > width) {
                lines.add(line.toString());
                line = new StringBuilder();
                wordCount = 0;
            }
            // Append a new line to the end of the line if wordCount 0.
            if (wordCount > 0) {
                line.append(" ");
            }
            line.append(word);
            ++wordCount;
        }
        // Add a line to the list of lines.
        if (wordCount > 0) {
            lines.add(line.toString());
        }
        canvas.setColor(textColor);
        // Draw the text of all lines in the canvas.
        for (int i = 0; i < lines.size(); i++) {
            canvas.text(lines.get(i), x, y + i * (lineHeight));
        }
    }

    /**
    * The text to display. Must not be null. This is a convenience method for text ( String ).
    * 
    * @param x - The x coordinate of the upper left corner of the text.
    * @param y - The y coordinate of the upper left corner of the text
    */
    public void text(String text, float x, float y) {
        canvas.setFontSize(fontSize);
        canvas.setColor(textColor);
        canvas.text(text, x, y);
    }

    /**
    * Draws a button at the given coordinates. This method is called by GLFW to draw a button on the button bar.
    * 
    * @param text - the text to display. Can be null if you don't want to display anything.
    * @param x - the x coordinate of the button. This is relative to the left edge of the canvas.
    * @param y - the y coordinate of the button. This is relative to the top edge of the canvas.
    * @param w - the width of the button. This is the pixel width of the button.
    * @param h - the height of the button. This is the pixel height of the button.
    * 
    * @return true if the button was clicked false otherwise. Note that this will return true if the mouse is over the button
    */
    public boolean button(String text, float x, float y, float w, float h) {
        Vector2d pos = getTranslatedMousePosition();
        canvas.setFontSize(fontSize);
        Rect buttonRect = new Rect(x, y, w, h);
        boolean hovered = buttonRect.contains(pos);
        boolean clicked = hovered && Input.mouseButtonBeginPress(GLFW_MOUSE_BUTTON_LEFT);

        canvas.pushTranslate(x, y);
        boolean shouldHover = popNextButtonHovered();
        canvas.setColor(hovered || shouldHover ? hoverBgColor : bgColor);
        canvas.rect(0 ,0, w, h);
        canvas.setColor(hovered || shouldHover ? hoverTextColor : textColor);
        canvas.text(text, padding.x, padding.y);
        canvas.popTranslate();

        return clicked;
    }

    /**
    * Draws a button at the specified location. The button will be centered on x and y but will not be centered on the left or right of the button
    * 
    * @param text - the text to display in the button
    * @param x - the x coordinate of the button to start drawing
    * @param y - the y coordinate of the button to start drawing
    * 
    * @return true if the button was drawn false if it was not ( could be due to user interaction in which case the button will not be drawn
    */
    public boolean button(String text, float x, float y) {
        canvas.setFontSize(fontSize);
        float w = canvas.computeTextWidth(text) + padding.x * 2;
        float h = fontSize + 2 * padding.y;
        return button(text, x, y, w, h);
    }

    /**
    * Draw an image button. This is a convenience method for drawing images that are used to control the appearance of the button.
    * 
    * @param texId - Texture ID to use for drawing the image.
    * @param sx0 - X - coordinate of the top - left corner of the image.
    * @param sy0 - Y - coordinate of the top - left corner of the image.
    * @param sx1 - X - coordinate of the bottom - right corner of the image.
    * @param sy1 - Y - coordinate of the bottom - right corner of the image.
    * @param x - X - coordinate of the button's upper - left corner.
    * @param y - Y - coordinate of the button's upper - left corner.
    * @param w - Width of the button in pixels. Must be greater than 0.
    * @param h - Height of the button in pixels. Must be greater than 0.
    * 
    * @return true if the button was clicked false otherwise ( in which case the button is not drawn ). Note that this does not mean that the image has been drawn
    */
    public boolean imageButton(int texId, float sx0, float sy0, float sx1, float sy1, float x, float y, float w, float h) {
        Vector2d pos = getTranslatedMousePosition();
        Rect buttonRect = new Rect(x, y, w, h);
        boolean hovered = buttonRect.contains(pos);
        boolean clicked = hovered && Input.mouseButtonBeginPress(GLFW_MOUSE_BUTTON_LEFT);

        canvas.pushTranslate(x, y);
        boolean shouldHover = popNextButtonHovered();
        canvas.setColor(hovered || shouldHover ? hoverBgColor : bgColor);
        canvas.rect(0 ,0, w, h);
        canvas.image(texId, sx0, sy0, sx1, sy1, padding.x, padding.y, w - padding.x * 2, h - padding.y * 2);
        canvas.popTranslate();

        return clicked;
    }

    /**
    * Begins drawing a rectangle. The coordinates are relative to the top left corner of the canvas. The background color is set to #bgColor and the padding is set to #padding.
    * 
    * @param x - the x coordinate of the rectangle to begin drawing
    * @param y - the y coordinate of the rectangle to begin drawing
    * @param w - the width of the rectangle to begin drawing ( pixels )
    * @param h - the height of the rectangle to begin drawing ( pixels
    */
    public void begin(float x, float y, float w, float h) {
        canvas.pushTranslate(x, y);
        canvas.setColor(bgColor);
        canvas.rect(0 ,0, w, h);
        canvas.pushTranslate(padding.x, padding.y);
    }

    /**
    * Ends the animation. This is called at the end of the animation and can be used to restore the state
    */
    public void end() {
        canvas.popTranslate();
        canvas.popTranslate();
    }

    /**
    * Translates the mouse position to canvas coordinates. This is useful for dragging the canvas in order to get the position of the mouse without affecting the screen.
    * 
    * 
    * @return a Vector2d containing the mouse position in canvas coordinates relative to the canvas's translation ( 0 0
    */
    private Vector2d getTranslatedMousePosition() {
        Vector2f translate = canvas.getTranslate();
        return new Vector2d(Input.mouseX(), Input.mouseY()).sub(translate);
    }
}

package com.webler.untitledgame.level.inventory;

import com.webler.goliath.core.Component;
import com.webler.goliath.eventsystem.EventManager;
import com.webler.goliath.graphics.Color;
import com.webler.goliath.graphics.Sprite;
import com.webler.goliath.graphics.canvas.Canvas;
import com.webler.goliath.graphics.canvas.TextAlign;
import com.webler.goliath.graphics.ui.UIElements;
import com.webler.goliath.input.Input;
import com.webler.untitledgame.level.Level;
import com.webler.untitledgame.level.events.ItemUnselectedEvent;
import com.webler.untitledgame.level.objects.LevelItem;
import com.webler.untitledgame.level.events.ItemSelectedEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;

public class Inventory extends Component {
    private final Level level;
    private boolean isOpened;
    private final Map<String, Integer> itemCounts;
    private InventoryItem selectedItem;
    private int hoveredItemIdx;

    public Inventory(Level level) {
        this.level = level;
        isOpened = false;
        itemCounts = new HashMap<>();
        selectedItem = null;
        hoveredItemIdx = 0;
    }

    /**
    * Adds an item to the count. If the identifier is already in the count nothing happens. This is useful for tracking how many times a particular item has been used in the index
    * 
    * @param identifier - The identifier of the
    */
    public void add(String identifier) {
        // Add a new item count to the list of items in the list.
        if(!itemCounts.containsKey(identifier)) {
            itemCounts.put(identifier, 1);
        } else {
            itemCounts.put(identifier, itemCounts.get(identifier) + 1);
        }
    }

    /**
    * Returns the number of items in the store. This is used to determine how many items are stored in the store by looking at the identifier passed in
    * 
    * @param identifier - Identifier to look up.
    * 
    * @return Number of items in the store that match the identifier passed in or 0 if no match was found in the store
    */
    public int getItemCount(String identifier) {
        return itemCounts.getOrDefault(identifier, 0);
    }

    /**
    * Decrements the count of items with the given identifier. If the count is 0 then the identifier is removed
    * 
    * @param identifier - the identifier of the
    */
    public void remove(String identifier) {
        // Add a new item count to the list of items in the list.
        if(itemCounts.containsKey(identifier)) {
            itemCounts.put(identifier, itemCounts.get(identifier) - 1);
            // Remove the item count for this item.
            if(itemCounts.get(identifier) == 0) {
                itemCounts.remove(identifier);
            }
        }
    }

    /**
    * Called when the server is started. This is where we start the web server and the server's state is maintained
    */
    @Override
    public void start() {

    }

    /**
    * Updates the window. This is called every frame to update the window. If you override this method be sure to call super. update () in order to ensure that the window is redrawn.
    * 
    * @param dt - time since the last call to update ( in seconds
    */
    @Override
    public void update(double dt) {
        // Draw the window if the window is open.
        if(isOpened) {
            drawWindow();
        }
    }

    /**
    * Called when the component is no longer needed. This is the place to do any cleanup that needs to be done
    */
    @Override
    public void destroy() {

    }

    /**
    * Sets the opened state of the menu. This is called by the JInternalFrame when it is opened or closed.
    * 
    * @param opened - true if the menu is opened false if it is
    */
    public void setOpened(boolean opened) {
        selectedItem = null;
        hoveredItemIdx = 0;
        isOpened = opened;
    }

    /**
    * Draws the window to the screen. This is called every frame by GLFW's draw ()
    */
    private void drawWindow() {
        UIElements ui = getGameObject().getGame().getUiElements();
        Canvas ctx = getGameObject().getGame().getCanvas();
        int w = ctx.getWidth(), h = ctx.getHeight();

        ui.padding.set(h * 0.01f);
        ui.lineHeight = h * 0.04f;
        ui.begin((w - h * 0.6f) / 2, h * 0.1f, h * 0.6f, h * 0.8f);

        int numCols = (int)((h * 0.6f) / (h * 0.1f));
        Set<Map.Entry<String, Integer>> entries = itemCounts.entrySet();
        int x = 0;
        int y = 0;

        int entriesCount = entries.size();

        // This method is used to determine the index of the key that is pressed.
        if((Input.keyBeginPress(GLFW_KEY_UP) || Input.keyBeginPress(GLFW_KEY_W)) && hoveredItemIdx >= numCols)
            hoveredItemIdx -= numCols;
        // This method is used to determine the index of the item in the list of keys.
        else if((Input.keyBeginPress(GLFW_KEY_DOWN) || Input.keyBeginPress(GLFW_KEY_S)) && entriesCount != 0 && hoveredItemIdx % entriesCount < entriesCount - numCols)
            hoveredItemIdx += numCols;
        // This method is used to determine the index of the item in the hovered list.
        else if(Input.keyBeginPress(GLFW_KEY_LEFT) || Input.keyBeginPress(GLFW_KEY_A))
            hoveredItemIdx -= 1;
        // Check if the key is pressed or pressed
        else if(Input.keyBeginPress(GLFW_KEY_RIGHT) || Input.keyBeginPress(GLFW_KEY_D))
            hoveredItemIdx += 1;

        // Returns the index of the hovered item in the list.
        while(hoveredItemIdx < 0) {
            hoveredItemIdx += entriesCount;
        }

        for(Map.Entry<String, Integer> entry : entries) {
            InventoryItem item = new InventoryItem((LevelItem) level.getRegisteredObject(entry.getKey()));

            // hover next button if the item is hovered.
            if (hoveredItemIdx % entriesCount == y * numCols + x) {
                ui.hoverNextButton();
            }

            int count = entry.getValue();
            Sprite sprite = item.levelItem().getSprite();
            float[] texCoords = sprite.getTexCoords();
            Color prevHoverBgColor = ui.hoverBgColor;
            ui.hoverBgColor = new Color(0.5, 0, 0, 1);
            // This method is called when the image button is pressed.
            if(ui.imageButton(sprite.getTexture().getTexId(), texCoords[0], texCoords[1], texCoords[4], texCoords[5], x * h * 0.1f, y * h * 0.1f, h * 0.08f, h * 0.08f)) {
                hoveredItemIdx = y * numCols + x;
                EventManager.dispatchEvent(new ItemSelectedEvent(gameObject, item.levelItem().getIdentifier()));
            }
            ui.hoverBgColor = prevHoverBgColor;

            ui.textColor = Color.WHITE;
            ui.fontSize = h * 0.02f;
            TextAlign prevTextAlign = ctx.getTextAlign();
            ctx.setTextAlign(TextAlign.RIGHT);
            String text = count < 100 ? String.valueOf(count) : "99+";
            ui.text( text, x * h * 0.1f + h * 0.08f, y * h * 0.1f  + h * 0.08f - ui.fontSize);
            ctx.setTextAlign(prevTextAlign);

            // Select the item that is hovered by the item.
            if(hoveredItemIdx % entriesCount == y * numCols + x) {
                selectedItem = new InventoryItem((LevelItem) level.getRegisteredObject(entry.getKey()));
            }

            x += 1;
            // Move the position to the right of the current position.
            if(x == numCols) {
                x = 0;
                y += 1;
            }
        }

        // Updates the text and description of the selected item.
        if(selectedItem != null) {
            Sprite sprite = selectedItem.levelItem().getSprite();
            float[] texCoords = sprite.getTexCoords();

            ctx.image(sprite.getTexture().getTexId(), texCoords[0], texCoords[1], texCoords[4], texCoords[5], 0, h * 0.6f, h * 0.16f, h * 0.16f);

            ui.fontSize = h * 0.025f;
            ui.text(selectedItem.levelItem().getName(), h * 0.18f, h * 0.6f);
            ui.textBlock(selectedItem.levelItem().getDescription(), h * 0.18f, h * 0.6f + ui.lineHeight, h * 0.42f - ui.padding.x);

            // This method is called when the user presses the key ENTER.
            if(Input.keyBeginPress(GLFW_KEY_ENTER) || Input.keyBeginPress(GLFW_KEY_E)) {
                EventManager.dispatchEvent(new ItemSelectedEvent(gameObject, selectedItem.levelItem().getIdentifier()));
            } else if(Input.keyBeginPress(GLFW_KEY_Q)) {
                EventManager.dispatchEvent(new ItemUnselectedEvent(gameObject, selectedItem.levelItem().getIdentifier()));
            }
        }

        ui.end();
    }
}

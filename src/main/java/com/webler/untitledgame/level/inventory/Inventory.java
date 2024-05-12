package com.webler.untitledgame.level.inventory;

import com.webler.goliath.core.Component;
import com.webler.goliath.eventsystem.EventManager;
import com.webler.goliath.graphics.Color;
import com.webler.goliath.graphics.Sprite;
import com.webler.goliath.graphics.canvas.Canvas;
import com.webler.goliath.graphics.canvas.TextAlign;
import com.webler.goliath.graphics.ui.UIElements;
import com.webler.goliath.input.Input;
import com.webler.untitledgame.components.Level;
import com.webler.untitledgame.components.LevelItem;
import com.webler.untitledgame.level.events.ItemSelectedEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;

public class Inventory extends Component {
    private Level level;
    private boolean isOpened;
    private Map<String, Integer> itemCounts;
    private InventoryItem selectedItem;
    private int hoveredItemIdx;

    public Inventory(Level level) {
        this.level = level;
        isOpened = false;
        itemCounts = new HashMap<>();
        selectedItem = null;
        hoveredItemIdx = 0;
    }

    public void add(String identifier) {
        if(!itemCounts.containsKey(identifier)) {
            itemCounts.put(identifier, 1);
        } else {
            itemCounts.put(identifier, itemCounts.get(identifier) + 1);
        }
    }

    public int getItemCount(String identifier) {
        return itemCounts.getOrDefault(identifier, 0);
    }

    public void remove(String identifier) {
        if(itemCounts.containsKey(identifier)) {
            itemCounts.put(identifier, itemCounts.get(identifier) - 1);
            if(itemCounts.get(identifier) == 0) {
                itemCounts.remove(identifier);
            }
        }
    }

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

    public boolean isOpened() {
        return isOpened;
    }

    public void setOpened(boolean opened) {
        selectedItem = null;
        hoveredItemIdx = 0;
        isOpened = opened;
    }

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

        if((Input.keyBeginPress(GLFW_KEY_UP) || Input.keyBeginPress(GLFW_KEY_W)) && hoveredItemIdx >= numCols)
            hoveredItemIdx -= numCols;
        else if((Input.keyBeginPress(GLFW_KEY_DOWN) || Input.keyBeginPress(GLFW_KEY_S)) && hoveredItemIdx % entries.size() < entries.size() - numCols)
            hoveredItemIdx += numCols;
        else if(Input.keyBeginPress(GLFW_KEY_LEFT) || Input.keyBeginPress(GLFW_KEY_A))
            hoveredItemIdx -= 1;
        else if(Input.keyBeginPress(GLFW_KEY_RIGHT) || Input.keyBeginPress(GLFW_KEY_D))
            hoveredItemIdx += 1;

        while(hoveredItemIdx < 0) {
            hoveredItemIdx += entries.size();
        }

        for(Map.Entry<String, Integer> entry : entries) {
            InventoryItem item = new InventoryItem((LevelItem) level.getRegisteredObject(entry.getKey()));

            if (hoveredItemIdx % entries.size() == y * numCols + x) {
                ui.hoverNextButton();
            }

            int count = entry.getValue();
            Sprite sprite = item.getLevelItem().getSprite();
            float[] texCoords = sprite.getTexCoords();
            Color prevHoverBgColor = ui.hoverBgColor;
            ui.hoverBgColor = new Color(0.5, 0, 0, 1);
            if(ui.imageButton(sprite.getTexture().getTexId(), texCoords[0], texCoords[1], texCoords[4], texCoords[5], x * h * 0.1f, y * h * 0.1f, h * 0.08f, h * 0.08f)) {
                hoveredItemIdx = y * numCols + x;
                EventManager.dispatchEvent(new ItemSelectedEvent(gameObject, item.getLevelItem().getIdentifier()));
            }
            ui.hoverBgColor = prevHoverBgColor;

            ui.textColor = Color.WHITE;
            ui.fontSize = h * 0.02f;
            TextAlign prevTextAlign = ctx.getTextAlign();
            ctx.setTextAlign(TextAlign.RIGHT);
            String text = count < 100 ? String.valueOf(count) : "99+";
            ui.text( text, x * h * 0.1f + h * 0.08f, y * h * 0.1f  + h * 0.08f - ui.fontSize);
            ctx.setTextAlign(prevTextAlign);

            if(hoveredItemIdx % entries.size() == y * numCols + x) {
                selectedItem = new InventoryItem((LevelItem) level.getRegisteredObject(entry.getKey()));
            }

            x += 1;
            if(x == numCols) {
                x = 0;
                y += 1;
            }
        }

        if(selectedItem != null) {
            Sprite sprite = selectedItem.getLevelItem().getSprite();
            float[] texCoords = sprite.getTexCoords();

            ctx.image(sprite.getTexture().getTexId(), texCoords[0], texCoords[1], texCoords[4], texCoords[5], 0, h * 0.6f, h * 0.16f, h * 0.16f);

            ui.fontSize = h * 0.025f;
            ui.text(selectedItem.getLevelItem().getName(), h * 0.18f, h * 0.6f);
            ui.textBlock(selectedItem.getLevelItem().getDescription(), h * 0.18f, h * 0.6f + ui.lineHeight, h * 0.42f - ui.padding.x);

            if(Input.keyBeginPress(GLFW_KEY_ENTER)) {
                EventManager.dispatchEvent(new ItemSelectedEvent(gameObject, selectedItem.getLevelItem().getIdentifier()));
            }
        }

        ui.end();
    }
}

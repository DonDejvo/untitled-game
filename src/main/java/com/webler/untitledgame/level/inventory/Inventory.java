package com.webler.untitledgame.level.inventory;

import com.webler.goliath.core.Component;
import com.webler.goliath.graphics.Color;
import com.webler.goliath.graphics.Sprite;
import com.webler.goliath.graphics.canvas.Canvas;
import com.webler.goliath.graphics.canvas.TextAlign;
import com.webler.goliath.graphics.ui.UIElements;
import com.webler.goliath.input.Input;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;

public class Inventory extends Component {
    private boolean isOpened;
    private Map<String, Integer> itemCounts;
    private Map<String, InventoryItem> itemRegistry;
    private InventoryItem selectedItem;
    private int hoveredItemIdx;

    public Inventory() {
        isOpened = false;
        itemCounts = new HashMap<>();
        itemRegistry = new HashMap<>();
        selectedItem = null;
        hoveredItemIdx = 0;
    }

    public void registerItem(String identifier, InventoryItem item) {
        itemRegistry.put(identifier, item);
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
        UIElements ui = getEntity().getGame().getUiElements();
        Canvas ctx = getEntity().getGame().getCanvas();
        int w = ctx.getWidth(), h = ctx.getHeight();

        ui.padding.set(h * 0.01f);
        ui.lineHeight = h * 0.04f;
        ui.begin((w - h * 0.6f) / 2, h * 0.1f, h * 0.6f, h * 0.8f);

        int numCols = (int)((h * 0.6f - ui.padding.x) / (h * 0.1f));
        Set<Map.Entry<String, Integer>> entries = itemCounts.entrySet();
        int x = 0;
        int y = 0;

        if(Input.keyBeginPress(GLFW_KEY_W)) hoveredItemIdx -= numCols;
        if(Input.keyBeginPress(GLFW_KEY_S)) hoveredItemIdx += numCols;
        if(Input.keyBeginPress(GLFW_KEY_A)) hoveredItemIdx -= 1;
        if(Input.keyBeginPress(GLFW_KEY_D)) hoveredItemIdx += 1;

        for(Map.Entry<String, Integer> entry : entries) {
            InventoryItem item = itemRegistry.get(entry.getKey());

            if(item == null) continue;

            if(hoveredItemIdx % entries.size() == y * numCols + x) {
                ui.hoverNextButton();
            }

            int count = entry.getValue();
            Sprite sprite = item.getSprite();
            float[] texCoords = sprite.getTexCoords();
            if(ui.imageButton(sprite.getTexture().getTexId(), texCoords[0], texCoords[1], texCoords[4], texCoords[5], x * h * 0.1f, y * h * 0.1f, h * 0.08f, h * 0.08f)) {
                hoveredItemIdx = y * numCols + x;
            }

            ui.textColor = Color.WHITE;
            ui.fontSize = h * 0.02f;
            TextAlign prevTextAlign = ctx.getTextAlign();
            ctx.setTextAlign(TextAlign.RIGHT);
            ui.text( count + "x", x * h * 0.1f + h * 0.08f, y * h * 0.1f);
            ctx.setTextAlign(prevTextAlign);

            if(hoveredItemIdx % entries.size() == y * numCols + x) {
                selectedItem = itemRegistry.get(entry.getKey());
            }

            x += 1;
            if(x > numCols) {
                x = 0;
                y += 1;
            }
        }

        if(selectedItem != null) {
            Sprite sprite = selectedItem.getSprite();
            float[] texCoords = sprite.getTexCoords();

            ctx.image(sprite.getTexture().getTexId(), texCoords[0], texCoords[1], texCoords[4], texCoords[5], 0, h * 0.6f, h * 0.16f, h * 0.16f);

            ui.fontSize = h * 0.025f;
            ui.text(selectedItem.getTitle(), h * 0.18f, h * 0.6f);
            ui.textBlock(selectedItem.getDescription(), h * 0.18f, h * 0.6f + ui.lineHeight, h * 0.42f - ui.padding.x);
        }

        ui.end();
    }
}

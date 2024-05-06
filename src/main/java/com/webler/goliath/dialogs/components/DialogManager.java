package com.webler.goliath.dialogs.components;

import com.webler.goliath.core.Component;
import com.webler.goliath.dialogs.Dialog;
import com.webler.goliath.dialogs.nodes.DialogNode;
import com.webler.goliath.dialogs.nodes.DialogNodeType;
import com.webler.goliath.dialogs.nodes.DialogOptionsNode;
import com.webler.goliath.dialogs.nodes.DialogTextNode;
import com.webler.goliath.dialogs.DialogOption;
import com.webler.goliath.dialogs.events.DialogEndedEvent;
import com.webler.goliath.dialogs.events.DialogNextEvent;
import com.webler.goliath.eventsystem.EventManager;
import com.webler.goliath.graphics.Color;
import com.webler.goliath.graphics.canvas.Canvas;
import com.webler.goliath.graphics.canvas.TextAlign;
import com.webler.goliath.graphics.ui.UIElements;
import com.webler.goliath.input.Input;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public class DialogManager extends Component {
    private Map<String, Dialog> dialogs;
    private DialogComponent currentDialog;
    private DialogNode currentNode;
    private DialogOption selectedOption;
    private State state;
    private int hoveredOptionIdx;
    private boolean isNestedOption;

    public DialogManager() {
        dialogs = new HashMap<>();
        currentDialog = null;
        currentNode = null;
        selectedOption = null;
        state = State.ENDED;
        hoveredOptionIdx = 0;
        isNestedOption = false;
    }

    public void addDialog(String name, Dialog dialog) {
        if (!dialogs.containsKey(name)) {
            dialogs.put(name, dialog);
        }
    }

    public Dialog getDialog(String name) {
        if (!dialogs.containsKey(name)) {
            throw new RuntimeException("No such dialog: " + name);
        }
        return dialogs.get(name);
    }

    public void loadDialogs(String resourceName) {
        InputStream is = ClassLoader.getSystemResourceAsStream(resourceName);
        if(is == null) {
            throw new RuntimeException("Could not load resource path '" + resourceName + "'");
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        try {
            while(reader.ready()) {
                String line = reader.readLine();
                String[] parts = line.split(";");

                if(parts.length < 2) continue;

                String name = parts[0].trim();

                if(parts.length == 2) {
                    addDialog(name, new Dialog(parts[1].trim()));
                } else {
                    addDialog(name, new Dialog(parts[2].trim(), parts[1].trim()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void startDialog(DialogComponent dialog) {
        currentDialog = dialog;
        openOptionSelecting(false);
    }

    public void showDialog(DialogNode dialog) {
        nextDialog(dialog);
    }

    public void endDialog() {
        currentDialog = null;
        state = State.ENDED;
        EventManager.dispatchEvent(new DialogEndedEvent(gameObject));
    }

    @Override
    public void start() {

    }

    @Override
    public void update(double dt) {
        switch (state) {
            case OPTION_SELECTING: {
                drawOptionSelectMenu();
                break;
            }
            case TALKING: {
                drawDialog();
                break;
            }
        }
    }

    @Override
    public void destroy() {

    }

    private void drawOptionSelectMenu() {
        UIElements ui = getEntity().getGame().getUiElements();
        Canvas ctx = getEntity().getGame().getCanvas();
        int w = ctx.getWidth(), h = ctx.getHeight();

        ui.padding.set(0);
        ui.fontSize = h * 0.025f;
        ui.lineHeight = h * 0.045f;
        ui.begin((w - h * 1.2f) / 2, h * 0.76f, h * 1.2f, h * 0.24f);
        ui.padding.set(h * 0.01f);
        Color prevBgColor = ui.bgColor;
        Color prevTextColor = ui.textColor;
        Color prevHoverTextColor = ui.hoverTextColor;
        Color prevHoverBgColor = ui.hoverBgColor;
        DialogOption[] options = isNestedOption ? ((DialogOptionsNode)currentNode).getOptions() : currentDialog.getOptions().toArray(DialogOption[]::new);
        if(Input.keyBeginPress(GLFW_KEY_UP) || Input.keyBeginPress(GLFW_KEY_W)) {
            hoveredOptionIdx = Math.max(hoveredOptionIdx - 1, 0);
        } else if(Input.keyBeginPress(GLFW_KEY_DOWN) || Input.keyBeginPress(GLFW_KEY_S)) {
            hoveredOptionIdx = Math.min(hoveredOptionIdx + 1, isNestedOption ? options.length - 1 : options.length);
        } else if(Input.keyBeginPress(GLFW_KEY_ENTER)) {
            if(hoveredOptionIdx < options.length) {
                handleOptionSelect(options, hoveredOptionIdx);
            } else {
                endDialog();
            }
        }
        ui.bgColor = new Color(1, 1, 1, 0);
        ui.textColor = Color.GRAY;
        ui.hoverTextColor = Color.WHITE;
        ui.hoverBgColor = new Color(1, 1, 1, 0);
        for(int i = 0; i < options.length; i++) {
            DialogOption option = options[i];
            if(i == hoveredOptionIdx) {
                ui.hoverNextButton();
            }
            if(ui.button(getDialog(option.getDialogName()).getText(), 0, i * ui.lineHeight, h * 1.2f, ui.lineHeight)) {
                handleOptionSelect(options, i);
            }
        }
        if(!isNestedOption) {
            if(hoveredOptionIdx == options.length) {
                ui.hoverNextButton();
            }
            if(ui.button("END", 0, options.length * ui.lineHeight)) {
                endDialog();
            }
        }
        ui.bgColor = prevBgColor;
        ui.textColor = prevTextColor;
        ui.hoverTextColor = prevHoverTextColor;
        ui.hoverBgColor = prevHoverBgColor;
        ui.end();
    }

    private void drawDialog() {
        UIElements ui = getEntity().getGame().getUiElements();
        Canvas ctx = getEntity().getGame().getCanvas();
        int w = ctx.getWidth(), h = ctx.getHeight();

        Dialog dialog = getDialog(((DialogTextNode)currentNode).getDialogName());
        boolean hasTitle = !dialog.getTitle().isEmpty();

        ui.padding.set(h * 0.01f);
        ui.lineHeight = h * 0.04f;
        Color prevColor = ui.textColor;
        ui.begin((w - h * 1.2f) / 2, 0, h * 1.2f, h * 0.26f);
        TextAlign prevTextAlign = ctx.getTextAlign();
        ctx.setTextAlign(TextAlign.CENTER);
        if(hasTitle) {
            ui.textColor = Color.WHITE;
            ui.fontSize = h * 0.03f;
            ui.text(dialog.getTitle(), h * 0.6f, h * 0.03f);
        }
        ui.textColor = hasTitle ? Color.YELLOW : Color.WHITE;
        ui.fontSize = h * 0.025f;
        ui.textBlock(dialog.getText(), h * 0.6f, h * 0.08f, h * 1.2f - ui.padding.x * 2);
        ctx.setTextAlign(prevTextAlign);
        ui.textColor = prevColor;
        if(ui.button("Next", h * 1.04f - ui.padding.x * 2, h * 0.26f - ui.padding.y * 2 - ui.lineHeight, h * 0.16f, ui.lineHeight) ||
        Input.keyBeginPress(GLFW_KEY_ENTER) || Input.keyBeginPress(GLFW_KEY_ESCAPE)) {
            nextDialog(currentNode.getNext());
        }
        ui.end();
    }

    private void handleOptionSelect(DialogOption[] options, int selected) {
        EventManager.dispatchEvent(new DialogNextEvent(currentDialog != null ? currentDialog.getEntity() : gameObject, options[selected].getDialogName()));
        if(isNestedOption) {
            ((DialogOptionsNode)currentNode).selectOption(selected);
            nextDialog(currentNode.getNext());
        } else {
            selectedOption = options[selected];
            nextDialog(selectedOption.getNode());
        }
    }

    private void nextDialog(DialogNode node) {
        currentNode = node;
        if(currentNode != null) {
            if(currentNode.getType() == DialogNodeType.OPTIONS) {
                openOptionSelecting(true);
            } else {
                EventManager.dispatchEvent(new DialogNextEvent(currentDialog != null ? currentDialog.getEntity() : gameObject, ((DialogTextNode)currentNode).getDialogName()));
                state = State.TALKING;
            }
        } else {
            if(currentDialog == null) {
                endDialog();
                return;
            }
            if(!selectedOption.isRepeat()) {
                currentDialog.removeOption(selectedOption);
            }
            selectedOption = null;
            openOptionSelecting(false);
        }
    }

    private void openOptionSelecting(boolean isNestedOption) {
        this.isNestedOption = isNestedOption;
        state = State.OPTION_SELECTING;
        hoveredOptionIdx = 0;
    }

    private enum State {
        ENDED,
        OPTION_SELECTING,
        TALKING
    }
}

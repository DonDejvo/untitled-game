package com.webler.goliath.dialogs.components;

import com.webler.goliath.core.Component;
import com.webler.goliath.dialogs.Dialog;
import com.webler.goliath.dialogs.DialogNode;
import com.webler.goliath.dialogs.DialogOption;
import com.webler.goliath.dialogs.events.DialogEnded;
import com.webler.goliath.dialogs.events.DialogNext;
import com.webler.goliath.eventsystem.EventManager;
import com.webler.goliath.graphics.Color;
import com.webler.goliath.graphics.canvas.Canvas;
import com.webler.goliath.graphics.canvas.TextAlign;
import com.webler.goliath.graphics.ui.UIElements;
import com.webler.goliath.input.Input;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public class DialogManager extends Component {
    private Map<String, Dialog> dialogs;
    private DialogComponent currentDialog;
    private DialogNode currentNode;
    private DialogOption selectedOption;
    private State state;
    private int hoveredOptionIdx;

    public DialogManager() {
        dialogs = new HashMap<>();
        currentDialog = null;
        currentNode = null;
        selectedOption = null;
        state = State.ENDED;
        hoveredOptionIdx = 0;
    }

    public void addDialog(String name, Dialog dialog) {
        dialogs.put(name, dialog);
    }

    public void startDialog(DialogComponent dialog) {
        currentDialog = dialog;
        openOptionSelecting();
    }

    public void showDialog(DialogNode dialog) {
        nextDialog(dialog);
    }

    public void endDialog() {
        currentDialog = null;
        state = State.ENDED;
        EventManager.dispatchEvent(new DialogEnded());
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
        List<DialogOption> options = currentDialog.getOptions();
        if(Input.keyBeginPress(GLFW_KEY_W)) {
            hoveredOptionIdx = Math.max(hoveredOptionIdx - 1, 0);
        }
        if(Input.keyBeginPress(GLFW_KEY_S)) {
            hoveredOptionIdx = Math.min(hoveredOptionIdx + 1, options.size());
        }
        if(Input.keyBeginPress(GLFW_KEY_ENTER)) {
            if(hoveredOptionIdx < options.size()) {
                DialogOption option = options.get(hoveredOptionIdx);
                handleOptionSelect(option);
            } else {
                endDialog();
            }
        }
        ui.bgColor = new Color(1, 1, 1, 0);
        ui.textColor = Color.GRAY;
        ui.hoverTextColor = Color.WHITE;
        ui.hoverBgColor = new Color(1, 1, 1, 0);
        for(int i = 0; i < options.size(); i++) {
            DialogOption option = options.get(i);
            if(i == hoveredOptionIdx) {
                ui.hoverNextButton();
            }
            if(ui.button(option.getText(), 0, i * ui.lineHeight, h * 1.2f, ui.lineHeight)) {
                handleOptionSelect(option);
            }
        }
        if(hoveredOptionIdx == options.size()) {
            ui.hoverNextButton();
        }
        if(ui.button("END", 0, options.size() * ui.lineHeight)) {
            endDialog();
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

        Dialog dialog = dialogs.get(currentNode.getDialogName());
        boolean hasTitle = dialog.getTitle() != null;

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
        Input.keyBeginPress(GLFW_KEY_ENTER)) {
            nextDialog(currentNode.getNext());
        }
        ui.end();
    }

    private void handleOptionSelect(DialogOption option) {
        selectedOption = option;
        nextDialog(selectedOption.getNode());
    }

    private void nextDialog(DialogNode node) {
        currentNode = node;
        if(currentNode != null) {
            EventManager.dispatchEvent(new DialogNext(currentNode.getDialogName()));
            state = State.TALKING;
        } else {
            if(selectedOption == null) {
                endDialog();
                return;
            }
            if(!selectedOption.isRepeat()) {
                currentDialog.removeOption(selectedOption);
            }
            selectedOption = null;
            openOptionSelecting();
        }
    }

    private void openOptionSelecting() {
        state = State.OPTION_SELECTING;
        hoveredOptionIdx = 0;
    }

    private enum State {
        ENDED,
        OPTION_SELECTING,
        TALKING
    }
}

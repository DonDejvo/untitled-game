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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DialogManager extends Component {
    private Map<String, Dialog> dialogs;
    private DialogComponent currentDialog;
    private DialogNode currentNode;
    private DialogOption selectedOption;
    private State state;

    public DialogManager() {
        dialogs = new HashMap<>();
        currentDialog = null;
        currentNode = null;
        selectedOption = null;
        state = State.ENDED;
    }

    public void addDialog(String name, Dialog dialog) {
        dialogs.put(name, dialog);
    }

    public void startDialog(DialogComponent dialog) {
        currentDialog = dialog;
        state = State.OPTION_SELECTING;
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

        ui.padding.set(h * 0.01f);
        ui.fontSize = h * 0.025f;
        ui.lineHeight = h * 0.045f;
        ui.begin((w - h * 1.2f) / 2, h * 0.76f, h * 1.2f, h * 0.24f);
        List<DialogOption> options = currentDialog.getOptions();
        for(int i = 0; i < options.size(); i++) {
            DialogOption option = options.get(i);
            if(ui.button(option.getText(), 0, i * ui.lineHeight)) {
                handleOptionSelect(option);
            }
        }
        if(ui.button("END", 0, options.size() * ui.lineHeight)) {
            endDialog();
        }
        ui.end();
    }

    private void drawDialog() {
        UIElements ui = getEntity().getGame().getUiElements();
        Canvas ctx = getEntity().getGame().getCanvas();
        int w = ctx.getWidth(), h = ctx.getHeight();

        Dialog dialog = dialogs.get(currentNode.getDialogName());

        ui.padding.set(h * 0.01f);
        ui.lineHeight = h * 0.04f;
        ui.begin((w - h * 1.2f) / 2, 0, h * 1.2f, h * 0.32f);
        TextAlign prevTextAlign = ctx.getTextAlign();
        ctx.setTextAlign(TextAlign.CENTER);
        Color prevColor = ui.textColor;
        ui.textColor = Color.YELLOW;
        ui.fontSize = h * 0.03f;
        ui.text(dialog.getTitle(), h * 0.6f, h * 0.02f);
        ui.textColor = prevColor;
        ui.fontSize = h * 0.025f;
        ui.textBlock(dialog.getText(), h * 0.6f, h * 0.08f, h * 1.2f - ui.padding.x * 2);
        ctx.setTextAlign(prevTextAlign);
        if(ui.button("Next", h * 1.04f - ui.padding.x * 2, h * 0.32f - ui.padding.y * 2 - ui.lineHeight, h * 0.16f, ui.lineHeight)) {
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
            if(!selectedOption.isRepeat()) {
                currentDialog.removeOption(selectedOption);
            }
            selectedOption = null;
            state = State.OPTION_SELECTING;
        }
    }

    private enum State {
        ENDED,
        OPTION_SELECTING,
        TALKING
    }
}

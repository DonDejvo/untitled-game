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
import com.webler.goliath.exceptions.ResourceFormatException;
import com.webler.goliath.exceptions.ResourceNotFoundException;
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
    private final Map<String, Dialog> dialogs;
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

    /**
    * Adds a dialog to the list of dialogs. This is useful for debugging and to add custom dialogs that don't get created in the first place
    * 
    * @param name - The name of the dialog
    * @param dialog - The dialog to add to the list of dial
    */
    public void addDialog(String name, Dialog dialog) {
        // Add a dialog to the list of dialogs.
        if (!dialogs.containsKey(name)) {
            dialogs.put(name, dialog);
        }
    }

    /**
    * Gets the dialog with the specified name. If the dialog does not exist an exception is thrown. This method is thread safe
    * 
    * @param name - the name of the dialog
    * 
    * @return the dialog with the specified name or an exception if the dialog does not exist ( no exception is thrown
    */
    public Dialog getDialog(String name) {
        // Dialog with the name of the dialog.
        if (!dialogs.containsKey(name)) {
            return new Dialog("<Dialog " + name + " does not exist>", "System");
        }
        return dialogs.get(name);
    }

    /**
    * Loads dialogs from a resource. The format of the resource is name ; dialog_name ; dialog_description
    * 
    * @param resourceName - the name of the resource to
    */
    public void loadDialogs(String resourceName) {
        InputStream is = ClassLoader.getSystemResourceAsStream(resourceName);
        // Throws a ResourceNotFoundException if the resource is null.
        if(is == null) {
            throw new ResourceNotFoundException(resourceName);
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        try {
            // Reads a dialog from the reader and adds it to the dialog list.
            while(reader.ready()) {
                String line = reader.readLine();
                String[] parts = line.split(";");

                // Skips the first two parts.
                if(parts.length < 2) continue;

                String name = parts[0].trim();

                // Add a dialog to the dialog box.
                if(parts.length == 2) {
                    addDialog(name, new Dialog(parts[1].trim()));
                } else {
                    addDialog(name, new Dialog(parts[2].trim(), parts[1].trim()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace(System.err);
            throw new ResourceFormatException(resourceName, "Could not load dialogs.");
        }

    }

    /**
    * Starts the dialog. This is called by the JDialog when it is opened. The default implementation sets the currentDialog to the given dialog and calls openOptionSelecting ( false )
    * 
    * @param dialog - the dialog to start
    */
    public void startDialog(DialogComponent dialog) {
        currentDialog = dialog;
        openOptionSelecting(false);
    }

    /**
    * Shows the specified dialog. This method is called by the JDialogPane when it wants to display a dialog.
    * 
    * @param dialog - the dialog to be displayed in the JDialog
    */
    public void showDialog(DialogNode dialog) {
        nextDialog(dialog);
    }

    /**
    * Ends the dialog and dispatches DialogEndedEvent to event manager. This method is called when the user clicks the close button
    */
    public void endDialog() {
        currentDialog = null;
        state = State.ENDED;
        EventManager.dispatchEvent(new DialogEndedEvent(gameObject));
    }

    /**
    * Called when the server is started. This is where we start the web server and the server's state is maintained
    */
    @Override
    public void start() {

    }

    /**
    * Updates the state of the dialog. This is called every frame to update the UI. If the state is OPTION_SELECTING it will draw the option selection menu. If the state is TALKING it will draw the dialog
    * 
    * @param dt - time since the last
    */
    @Override
    public void update(double dt) {
        // Called when the state of the dialog is changed.
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

    /**
    * Called when the component is no longer needed. This is the place to do any cleanup that needs to be done
    */
    @Override
    public void destroy() {

    }

    /**
    * Draws the menu that allows selecting options from the options list. This is a bit different from the menu in that it draws the options on top of the menu
    */
    private void drawOptionSelectMenu() {
        UIElements ui = getGameObject().getGame().getUiElements();
        Canvas ctx = getGameObject().getGame().getCanvas();
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
        // if the key is pressed by the user pressing the key presses the hovered option.
        if(Input.keyBeginPress(GLFW_KEY_UP) || Input.keyBeginPress(GLFW_KEY_W)) {
            hoveredOptionIdx = Math.max(hoveredOptionIdx - 1, 0);
        // if the key is pressed or keyDown or keyS
        } else if(Input.keyBeginPress(GLFW_KEY_DOWN) || Input.keyBeginPress(GLFW_KEY_S)) {
            hoveredOptionIdx = Math.min(hoveredOptionIdx + 1, isNestedOption ? options.length - 1 : options.length);
        // If the key is ENTER or ENTER the user presses the key press the select button.
        } else if(Input.keyBeginPress(GLFW_KEY_ENTER)) {
            // if hoveredOptionIdx is greater than the number of options in the list
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
        // Selects all options in the dialog.
        for(int i = 0; i < options.length; i++) {
            DialogOption option = options[i];
            // If the next button is hovered.
            if(i == hoveredOptionIdx) {
                ui.hoverNextButton();
            }
            // if the option is selected.
            if(ui.button(getDialog(option.dialogName()).getText(), 0, i * ui.lineHeight, h * 1.2f, ui.lineHeight)) {
                handleOptionSelect(options, i);
            }
        }
        // If the current option is a nested option the button is not hovered.
        if(!isNestedOption) {
            // If the next button is hovered.
            if(hoveredOptionIdx == options.length) {
                ui.hoverNextButton();
            }
            // endDialog is called when the end button is pressed
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

    /**
    * Draws the dialog to the screen. This is called by draw () when the user clicks on the dialog
    */
    private void drawDialog() {
        UIElements ui = getGameObject().getGame().getUiElements();
        Canvas ctx = getGameObject().getGame().getCanvas();
        int w = ctx.getWidth(), h = ctx.getHeight();

        Dialog dialog = getDialog(((DialogTextNode)currentNode).getDialogName());
        boolean hasTitle = !dialog.getTitle().isEmpty();

        ui.padding.set(h * 0.01f);
        ui.lineHeight = h * 0.04f;
        Color prevColor = ui.textColor;
        ui.begin((w - h * 1.2f) / 2, 0, h * 1.2f, h * 0.26f);
        TextAlign prevTextAlign = ctx.getTextAlign();
        ctx.setTextAlign(TextAlign.CENTER);
        // Set the title of the dialog.
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
        // This method is called when the user presses the next button.
        if(ui.button("Next", h * 1.04f - ui.padding.x * 2, h * 0.26f - ui.padding.y * 2 - ui.lineHeight, h * 0.16f, ui.lineHeight) ||
        Input.keyBeginPress(GLFW_KEY_ENTER) || Input.keyBeginPress(GLFW_KEY_ESCAPE)) {
            nextDialog(currentNode.getNext());
        }
        ui.end();
    }

    /**
    * Handles the event generated by the user selecting an option. This is a bit tricky because we need to dispatch events to the event manager.
    * 
    * @param options - The array of DialogOption that was selected.
    * @param selected - The index of the selected option in the array
    */
    private void handleOptionSelect(DialogOption[] options, int selected) {
        EventManager.dispatchEvent(new DialogNextEvent(currentDialog != null ? currentDialog.getGameObject() : gameObject, options[selected].dialogName()));
        // selects the selected option or the next option if the selected option is a nested option.
        if(isNestedOption) {
            ((DialogOptionsNode)currentNode).selectOption(selected);
            nextDialog(currentNode.getNext());
        } else {
            selectedOption = options[selected];
            nextDialog(selectedOption.node());
        }
    }

    /**
    * Switch to the next dialog. This is called when the user clicks the next button in the dialog tree
    * 
    * @param node - The node that was
    */
    private void nextDialog(DialogNode node) {
        currentNode = node;
        // Selects the option selected by the current node.
        if(currentNode != null) {
            // Opens option selecting if the current node is a OPTIONS or a TALKING dialog.
            if(currentNode.getType() == DialogNodeType.OPTIONS) {
                openOptionSelecting(true);
            } else {
                EventManager.dispatchEvent(new DialogNextEvent(currentDialog != null ? currentDialog.getGameObject() : gameObject, ((DialogTextNode)currentNode).getDialogName()));
                state = State.TALKING;
            }
        } else {
            // End the dialog if there is no current dialog.
            if(currentDialog == null) {
                endDialog();
                return;
            }
            // Removes the selected option from the dialog.
            if(!selectedOption.repeat()) {
                currentDialog.removeOption(selectedOption);
            }
            selectedOption = null;
            openOptionSelecting(false);
        }
    }

    /**
    * Opens the option selecting. This is called when the user presses the Enter key in the menu.
    * 
    * @param isNestedOption - true if the option is nested false
    */
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

package com.webler.untitledgame.editor;

import com.webler.goliath.input.Input;
import com.webler.untitledgame.components.LevelObject;
import com.webler.untitledgame.components.LevelObjectType;
import imgui.ImGui;

import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class MenuBar {
    private final EditorComponent editor;


    public MenuBar(EditorComponent editor) {
        this.editor = editor;
    }

    /**
    * Creates the menu file that is used to save and play the game. This menu is created when the user presses the left button
    */
    private void createMenuFile() {
        // Handles the key pressing of the editor.
        if(Input.keyPressed(GLFW_KEY_LEFT_CONTROL)) {
            // Handles the key pressing the editor.
            if(Input.keyBeginPress(GLFW_KEY_S)) {
                editor.handleSave();
            }
            // Handles the keypress events.
            else if(Input.keyBeginPress(GLFW_KEY_O)) {
                editor.handleOpen();
            }
            // The Play button is pressed.
            else if(Input.keyBeginPress(GLFW_KEY_P)) {
                editor.handlePlay();
            }
        }

        // This method is called when the user clicks on the file menu
        if (ImGui.beginMenu("File")) {
            // Handles the new menu item.
            if (ImGui.menuItem("New")) {
                editor.handleNew();
            }
            // Open the editor if the menu item is open
            if (ImGui.menuItem("Open", "Ctrl+O")) {
                editor.handleOpen();
            }
            // Save the current state of the editor.
            if (ImGui.menuItem("Save", "Ctrl+S")) {
                editor.handleSave();
            }
            // Save As menu item.
            if (ImGui.menuItem("Save As")) {
                editor.handleSaveAs();
            }
            ImGui.endMenu();
        }
    }

    /**
    * Creates the Edit menu and puts the player into the editor if they press Ctrl + P. This is used to play the
    */
    private void createMenuEdit() {
        // This method is called when the user clicks on the Play button.
        if (ImGui.beginMenu("Edit")) {
            // Play the current play button.
            if(ImGui.menuItem("Play", "Ctrl+P")) {
                editor.handlePlay();
            }
            ImGui.endMenu();
        }
    }

    /**
    * Creates and adds a menu to the game object. This is called when the user clicks on the menu
    */
    private void createMenuGameObject() {
        List<LevelObject> levelEntities = editor.getLevel().getRegisteredObjects(LevelObjectType.ENTITY);
        List<LevelObject> levelItems = editor.getLevel().getRegisteredObjects(LevelObjectType.ITEM);

        // This method is called by the main method to add the menu items and other menu items.
        if (ImGui.beginMenu("GameObject")) {
            // This method is called when the user clicks on the menu item Platform
            if(ImGui.beginMenu("Area")) {
                // Add the platform to the editor
                if (ImGui.menuItem("Platform")) {
                    editor.addPlatform();
                }
                ImGui.endMenu();
            }
            // This method is called when the user clicks on the light menu
            if(ImGui.beginMenu("Light")) {
                // Add Spot Light to the editor
                if (ImGui.menuItem("Spot Light")) {
                    editor.addSpotLight();
                }
                ImGui.endMenu();
            }
            // Add Door or Fixed menu item
            if(ImGui.beginMenu("Fixed")) {
                // Add Door to the editor if the Door is on the menu item
                if (ImGui.menuItem("Door")) {
                    editor.addDoor();
                }

                ImGui.endMenu();
            }
            // This method is called when the user clicks on the menu.
            if(ImGui.beginMenu("Entities")) {
                for(LevelObject entity : levelEntities) {
                    // Add the entity to the editor.
                    if (ImGui.menuItem(entity.getName())) {
                        editor.addEntity(entity.getIdentifier());
                    }
                }
                ImGui.endMenu();
            }

            // This method is called when the menu is empty.
            if(ImGui.beginMenu("Items")) {
                for(LevelObject item : levelItems) {
                    // Add the item to the editor.
                    if (ImGui.menuItem(item.getName())) {
                        editor.addEntity(item.getIdentifier());
                    }
                }
                ImGui.endMenu();
            }
            ImGui.endMenu();
        }
    }

    /**
    * Creates the menu window and sets the open / closed state of the menu items depending on the state of the
    */
    private void createMenuWindow() {
        // This method is called when the window is opened.
        if (ImGui.beginMenu("Window")) {
            // This method is called when the hierarchy window is opened.
            if (ImGui.menuItem("Hierarchy", null, editor.isHierarchyWindowOpened())) {
                editor.setHierarchyWindowOpened(!editor.isHierarchyWindowOpened());
            }
            // This method is called when the Level window is opened.
            if (ImGui.menuItem("Level", null, editor.isLevelWindowOpened())) {
                editor.setLevelWindowOpened(!editor.isLevelWindowOpened());
            }
            // This method is called when the Inspector window is opened.
            if (ImGui.menuItem("Inspector", null, editor.isInspectorWindowOpened())) {
                editor.setInspectorWindowOpened(!editor.isInspectorWindowOpened());
            }
            ImGui.endMenu();
        }
    }

    /**
    * Method to create ImGui menu bar and game object. This method is called by JNI when user presses enter
    */
    public void imgui() {
        ImGui.beginMenuBar();
        createMenuFile();
        createMenuEdit();
        createMenuGameObject();
        createMenuWindow();
        ImGui.endMenuBar();
    }
}

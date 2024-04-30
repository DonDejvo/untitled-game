package com.webler.untitledgame.editor;

import com.webler.untitledgame.components.Level;
import com.webler.untitledgame.scenes.TestParams;
import imgui.ImGui;

public class MenuBar {
    private EditorComponent editor;


    public MenuBar(EditorComponent editor) {
        this.editor = editor;
    }

    private void createMenuFile() {
        Level level = editor.getComponent(Level.class, "Level");

        if (ImGui.beginMenu("File")) {
            if (ImGui.menuItem("New")) {
                editor.handleNew();
            }
            if (ImGui.menuItem("Open", "Ctrl+O")) {
                editor.handleOpen();
            }
            if (ImGui.menuItem("Save", "Ctrl+S")) {
                editor.handleSave();
            }
            if (ImGui.menuItem("Save As")) {
                editor.handleSaveAs();
            }
            ImGui.endMenu();
        }
    }

    private void createMenuEdit() {
        if (ImGui.beginMenu("Edit")) {
            if(ImGui.menuItem("Play", "Ctrl+P")) {
                editor.handlePlay();
            }
            ImGui.endMenu();
        }
    }

    private void createMenuGameObject() {
        if (ImGui.beginMenu("GameObject")) {
            if(ImGui.beginMenu("Area")) {
                if (ImGui.menuItem("Platform")) {
                    editor.addPlatform();
                }
                ImGui.endMenu();
            }
            if(ImGui.beginMenu("Light")) {
                if (ImGui.menuItem("Spot Light")) {
                    editor.addSpotLight();
                }
                ImGui.endMenu();
            }
            if(ImGui.beginMenu("Entity")) {
                if (ImGui.menuItem("Player")) {
                    editor.addEntity("player");
                }
                if (ImGui.menuItem("Cat Girl")) {

                }
                if (ImGui.menuItem("Slime")) {

                }
                ImGui.endMenu();
            }
            if(ImGui.beginMenu("Item")) {
                if (ImGui.menuItem("Key")) {

                }
                if (ImGui.menuItem("Diamond")) {

                }
                if (ImGui.menuItem("Espresso")) {

                }
                ImGui.endMenu();
            }
            if(ImGui.beginMenu("Fixed")) {
                if (ImGui.menuItem("Door")) {
                    editor.addDoor();
                }
                if (ImGui.menuItem("Wending Machine")) {

                }
                ImGui.endMenu();
            }
            ImGui.endMenu();
        }
    }

    private void createMenuWindow() {
        if (ImGui.beginMenu("Window")) {
            if (ImGui.menuItem("Hierarchy", null, editor.isHierarchyWindowOpened())) {
                editor.setHierarchyWindowOpened(!editor.isHierarchyWindowOpened());
            }
            if (ImGui.menuItem("Level", null, editor.isLevelWindowOpened())) {
                editor.setLevelWindowOpened(!editor.isLevelWindowOpened());
            }
            if (ImGui.menuItem("Inspector", null, editor.isInspectorWindowOpened())) {
                editor.setInspectorWindowOpened(!editor.isInspectorWindowOpened());
            }
            ImGui.endMenu();
        }
    }

    private void createMenuHelp() {
        if (ImGui.beginMenu("Help")) {
            if(ImGui.menuItem("About")) {

            }
            ImGui.endMenu();
        }
    }

    public void imgui() {
        ImGui.beginMenuBar();
        createMenuFile();
        createMenuEdit();
        createMenuGameObject();
        createMenuWindow();
        createMenuHelp();
        ImGui.endMenuBar();
    }
}

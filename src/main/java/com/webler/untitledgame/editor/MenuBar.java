package com.webler.untitledgame.editor;

import com.webler.untitledgame.components.LevelObject;
import com.webler.untitledgame.components.LevelObjectType;
import imgui.ImGui;

import java.util.List;
import java.util.stream.Collectors;

public class MenuBar {
    private EditorComponent editor;


    public MenuBar(EditorComponent editor) {
        this.editor = editor;
    }

    private void createMenuFile() {

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
        List<LevelObject> levelEntities = editor.getLevel().getRegisteredObjects(LevelObjectType.ENTITY);
        List<LevelObject> levelItems = editor.getLevel().getRegisteredObjects(LevelObjectType.ITEM);

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
            if(ImGui.beginMenu("Fixed")) {
                if (ImGui.menuItem("Door")) {
                    editor.addDoor();
                }

                ImGui.endMenu();
            }
            if(ImGui.beginMenu("Entities")) {
                for(LevelObject entity : levelEntities) {
                    if (ImGui.menuItem(entity.getName())) {
                        editor.addEntity(entity.getIdentifier());
                    }
                }
                ImGui.endMenu();
            }

            if(ImGui.beginMenu("Items")) {
                for(LevelObject item : levelItems) {
                    if (ImGui.menuItem(item.getName())) {
                        editor.addEntity(item.getIdentifier());
                    }
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

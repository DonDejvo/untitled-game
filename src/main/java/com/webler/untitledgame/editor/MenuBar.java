package com.webler.untitledgame.editor;

import imgui.ImGui;

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
                if (ImGui.menuItem("Vending Machine")) {
                    editor.addEntity("vending_machine");
                }
                if(ImGui.beginMenu("NPC")) {
                    if (ImGui.menuItem("Cat Girl 1")) {
                        editor.addEntity("cat_girl_1");
                    }
                    if (ImGui.menuItem("Cat Girl 2")) {
                        editor.addEntity("cat_girl_2");
                    }
                    if (ImGui.menuItem("Cat Girl 3")) {
                        editor.addEntity("cat_girl_3");
                    }
                    ImGui.endMenu();
                }
                ImGui.endMenu();
            }
            if(ImGui.beginMenu("Item")) {
                if (ImGui.menuItem("Key")) {
                    editor.addEntity("key");
                }
                if (ImGui.menuItem("Caffe Latte")) {
                    editor.addEntity("caffe_latte");
                }
                if (ImGui.menuItem("Espresso")) {
                    editor.addEntity("espresso");
                }
                if (ImGui.menuItem("Americano")) {
                    editor.addEntity("americano");
                }
                ImGui.endMenu();
            }
            if(ImGui.beginMenu("Fixed")) {
                if (ImGui.menuItem("Door")) {
                    editor.addDoor();
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

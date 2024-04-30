package com.webler.goliath.graphics.widgets;

import imgui.ImGui;
import imgui.flag.ImGuiColorEditFlags;
import imgui.type.ImInt;
import imgui.type.ImString;

public class Controls {
    private static final int MIN_VALUE = -100000;
    private static final int MAX_VALUE = 100000;
    private static final int COL_WIDTH = 200;

    public static void intControl(String label, int[] value, float step) {
        intControl(label, value, step, MIN_VALUE, MAX_VALUE);
    }

    public static void intControl(String label, int[] value, float step, int min, int max) {
        ImGui.columns(2);
        ImGui.setColumnWidth(0, COL_WIDTH);
        ImGui.text(label);
        ImGui.nextColumn();
        ImGui.pushID(label);
        ImGui.dragInt("##" + label, value, step, min, max);
        ImGui.popID();
        ImGui.columns(1);
    }

    public static void floatControl(String label, float[] value, float step) {
        floatControl(label, value, step, MIN_VALUE, MAX_VALUE);
    }

    public static void floatControl(String label, float[] value, float step, float min, float max) {
        ImGui.columns(2);
        ImGui.setColumnWidth(0, COL_WIDTH);
        ImGui.text(label);
        ImGui.nextColumn();
        ImGui.pushID(label);
        ImGui.dragFloat("##" + label, value, step, min, max);
        ImGui.popID();
        ImGui.columns(1);
    }

    public static void colorPicker(String label, float[] value) {
        ImGui.columns(2);
        ImGui.setColumnWidth(0, COL_WIDTH);
        ImGui.text(label);
        ImGui.nextColumn();
        ImGui.pushID(label);
        ImGui.colorEdit4("##" + label, value);
        ImGui.popID();
        ImGui.columns(1);
    }

    public static void textInput(String label, ImString value) {
        ImGui.columns(2);
        ImGui.setColumnWidth(0, COL_WIDTH);
        ImGui.text(label);
        ImGui.nextColumn();
        ImGui.pushID(label);
        ImGui.inputText("##" + label, value);
        ImGui.popID();
        ImGui.columns(1);
    }

    public static void comboBox(String label, ImInt value, String[] items) {
        ImGui.columns(2);
        ImGui.setColumnWidth(0, COL_WIDTH);
        ImGui.text(label);
        ImGui.nextColumn();
        ImGui.pushID(label);
        ImGui.combo("##" + label, value, items);
        ImGui.popID();
        ImGui.columns(1);
    }
}

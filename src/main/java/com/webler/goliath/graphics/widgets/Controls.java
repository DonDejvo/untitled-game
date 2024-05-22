package com.webler.goliath.graphics.widgets;

import imgui.ImGui;
import imgui.type.ImInt;
import imgui.type.ImString;

public class Controls {
    private static final int MIN_VALUE = -100000;
    private static final int MAX_VALUE = 100000;
    private static final int COL_WIDTH = 280;

    /**
    * Controls a value from an int array. The value is clamped to the range [ MIN_VALUE MAX_VALUE ]
    * 
    * @param label - The label to display on the button
    * @param value - The value to be controlled. If this value is null the value will be set to 0
    * @param step - The amount to step between
    */
    public static void intControl(String label, int[] value, float step) {
        intControl(label, value, step, MIN_VALUE, MAX_VALUE);
    }

    /**
    * Draws a control to select an int. It is possible to set min and max to 0. 0
    * 
    * @param label - the label for the control
    * @param value - the value to be dragged into the control
    * @param step - the step between the dragged value and the original value
    * @param min - the minimum value for the int ( inclusive )
    * @param max - the maximum value for the int ( exclusive )
    */
    public static void intControl(String label, int[] value, float step, float min, float max) {
        ImGui.columns(2);
        ImGui.setColumnWidth(0, COL_WIDTH);
        ImGui.text(label);
        ImGui.nextColumn();
        ImGui.pushID(label);
        ImGui.dragInt("##" + label, value, step, min, max);
        ImGui.popID();
        ImGui.columns(1);
    }

    /**
    * Draws a control with a floating point value. This is a convenience method that calls #floatControl ( String float float float int ) with reasonable defaults.
    * 
    * @param label - The label to display for the control. This can be used to access the control's result via Component#getText ().
    * @param value - The value to be used as the control's target.
    * @param step - The step between values in the range [ 0 1 ]
    */
    public static void floatControl(String label, float[] value, float step) {
        floatControl(label, value, step, MIN_VALUE, MAX_VALUE);
    }

    /**
    * Draws a floating point control. It is possible to specify min and max values in the range 0. 0 - 1. 0
    * 
    * @param label - the label for the control
    * @param value - the value to be dragged into the control
    * @param step - the step size for the dragged value ( must be greater than 0 )
    * @param min - the minimum value that can be dragged into the control
    * @param max - the maximum value that can be dragged into the
    */
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

    /**
    * Creates a color picker. It is possible to change the color of a cell by clicking on the color picker button
    * 
    * @param label - Label for the color picker
    * @param value - Value to be set for the color picker ( must be float
    */
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

    /**
    * Creates a text input field. This is a wrapper for ImGui. inputText ( label value )
    * 
    * @param label - Label for the input field
    * @param value - Value for the input field in the form of a
    */
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

    /**
    * Creates a combo box with label and value. The items are separated by commas. This is useful for adding items to a combobox
    * 
    * @param label - label for the box ( can be null )
    * @param value - value for the box ( can be null )
    * @param items - array of items to be added to the box
    */
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

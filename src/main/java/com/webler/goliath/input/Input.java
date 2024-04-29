package com.webler.goliath.input;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.*;

public class Input {
    private static final int MOUSE_BUTTON_COUNT = 9;
    private static Input instance = null;
    private double mouseX, mouseY;
    private double mouseDeltaX, mouseDeltaY;
    private double scrollX, scrollY;
    private final boolean[] mouseButtonPressed;
    private final boolean[] mouseButtonBeginPress;
    private int mouseButtonDown;
    private final boolean[] keyPressed;
    private final boolean[] keyBeginPress;
    private boolean captured;

    private Input() {
        mouseButtonDown = 0;
        mouseButtonPressed = new boolean[MOUSE_BUTTON_COUNT];
        mouseButtonBeginPress = new boolean[MOUSE_BUTTON_COUNT];
        keyPressed = new boolean[GLFW_KEY_LAST + 1];
        keyBeginPress = new boolean[GLFW_KEY_LAST + 1];
        captured = false;
    }

    public static Input getInstance() {
        if (instance == null) {
            instance = new Input();
        }
        return instance;
    }

    public static void start() {
        Input instance = getInstance();
        clear();
        instance.captured = false;
    }

    public static void endFrame() {
        Input instance = getInstance();
        instance.scrollX = 0;
        instance.scrollY = 0;
        instance.mouseDeltaX = 0;
        instance.mouseDeltaY = 0;
        Arrays.fill(instance.mouseButtonBeginPress, false);
        Arrays.fill(instance.keyBeginPress, false);
    }

    public static void clear() {
        Input instance = getInstance();
        instance.scrollX = 0;
        instance.scrollY = 0;
        instance.mouseDeltaX = 0;
        instance.mouseDeltaY = 0;
        instance.mouseButtonDown = 0;
        Arrays.fill(instance.mouseButtonPressed, false);
        Arrays.fill(instance.mouseButtonBeginPress, false);
        Arrays.fill(instance.keyBeginPress, false);
        Arrays.fill(instance.keyPressed, false);
    }

    public static void keyCallback(long window, int key, int scancode, int action, int mods) {
        Input instance = getInstance();
        if(key >= 0 && key <= GLFW_KEY_LAST) {
            if(action == GLFW_PRESS) {
                instance.keyPressed[key] = true;
                instance.keyBeginPress[key] = true;
            } else if(action == GLFW_RELEASE) {
                instance.keyPressed[key] = false;
                instance.keyBeginPress[key] = false;
            }
        }
    }

    public static void mousePosCallback(long window, double xpos, double ypos) {
        Input instance = getInstance();

        if(instance.captured) {
            clear();
            return;
        }

        instance.mouseDeltaX = xpos - instance.mouseX;
        instance.mouseDeltaY = ypos - instance.mouseY;

        instance.mouseX = xpos;
        instance.mouseY = ypos;
    }

    public static void mouseButtonCallback(long window, int button, int action, int mods) {
        if(instance.captured) return;

        Input instance = getInstance();
        if(action == GLFW_PRESS) {
            ++instance.mouseButtonDown;
            if(button < MOUSE_BUTTON_COUNT) {
                instance.mouseButtonPressed[button] = true;
                instance.mouseButtonBeginPress[button] = true;
            }
        } else if(action == GLFW_RELEASE) {
            --instance.mouseButtonDown;
            if(button < MOUSE_BUTTON_COUNT) {
                instance.mouseButtonPressed[button] = false;
            }
        }
    }

    public static void mouseScrollCallback(long window, double xOffset, double yOffset) {
        if(instance.captured) return;

        Input instance = getInstance();
        instance.scrollX = xOffset;
        instance.scrollY = yOffset;
    }

    public static double mouseX() {
        return getInstance().mouseX;
    }

    public static double mouseY() {
        return getInstance().mouseY;
    }

    public static double mouseDeltaX() {
        return getInstance().mouseDeltaX;
    }

    public static double mouseDeltaY() {
        return getInstance().mouseDeltaY;
    }

    public static boolean mouseButtonPressed(int button) {
        return getInstance().mouseButtonPressed[button];
    }

    public static boolean mouseButtonBeginPress(int button) {
        return getInstance().mouseButtonBeginPress[button];
    }

    public static boolean keyPressed(int key) {
        return getInstance().keyPressed[key];
    }

    public static boolean keyBeginPress(int key) {
        return getInstance().keyBeginPress[key];
    }

    public static boolean mouseButtonPress() {
        return getInstance().mouseButtonDown != 0;
    }

    public static double scrollX() {
        return getInstance().scrollX;
    }
    public static double scrollY() {
        return getInstance().scrollY;
    }

    public static boolean isCaptured() {
        return getInstance().captured;
    }

    public static void setCaptured(boolean captured) {
        getInstance().captured = captured;
    }
}

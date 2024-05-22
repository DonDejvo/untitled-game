package com.webler.goliath.input;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.*;

public class Input {
    private static final int MOUSE_BUTTON_COUNT = 9;
    private static Input instance = null;
    private double prevMouseX, prevMouseY;
    private double mouseX, mouseY;
    private double mouseDeltaX, mouseDeltaY;
    private double scrollX, scrollY;
    private final boolean[] mouseButtonPressed;
    private final boolean[] mouseButtonBeginPress;
    private int mouseButtonDown;
    private final boolean[] keyPressed;
    private final boolean[] keyBeginPress;
    private boolean captured;
    private long window;
    private boolean locked;

    private Input() {
        mouseButtonDown = 0;
        mouseButtonPressed = new boolean[MOUSE_BUTTON_COUNT];
        mouseButtonBeginPress = new boolean[MOUSE_BUTTON_COUNT];
        keyPressed = new boolean[GLFW_KEY_LAST + 1];
        keyBeginPress = new boolean[GLFW_KEY_LAST + 1];
        captured = false;
        locked = false;
    }

    /**
    * Returns the singleton instance of Input. This is useful for unit testing. If you want to run tests on a single instance of Input you should use #getInstance () instead.
    * 
    * 
    * @return the singleton instance of Input or null if none exists in the current JVM ( which may or may not be the case for tests
    */
    public static Input getInstance() {
        // Create a new instance of Input
        if (instance == null) {
            instance = new Input();
        }
        return instance;
    }

    /**
    * Starts capturing input. This is equivalent to pressing Enter and releasing the cursor. If you are using this method as a stand alone application you should call Input#stop () to stop capturing and re - start capturing in the same application.
    * 
    * @param window - The window in which to capture the input. This must be greater than 0
    */
    public static void start(long window) {
        Input instance = getInstance();
        instance.window = window;
        clear();
        instance.captured = false;
        setCursorLocked(false);
    }

    /**
    * Called when the mouse is at the beginning of a frame. This is a no - op for mouse input
    */
    public static void beginFrame() {
        instance.mouseDeltaX = instance.mouseX - instance.prevMouseX;
        instance.mouseDeltaY = instance.mouseY - instance.prevMouseY;
        instance.prevMouseX = instance.mouseX;
        instance.prevMouseY = instance.mouseY;
    }

    /**
    * Ends the current frame. This is useful for debugging and to reset the state of the Input object to what it was before the startFrame ()
    */
    public static void endFrame() {
        Input instance = getInstance();
        instance.scrollX = 0;
        instance.scrollY = 0;
        instance.mouseDeltaX = 0;
        instance.mouseDeltaY = 0;
        Arrays.fill(instance.mouseButtonBeginPress, false);
        Arrays.fill(instance.keyBeginPress, false);
    }

    /**
    * Clears the state of the Input. This is useful for testing and to prevent accidental flickering
    */
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

    /**
    * Called by GLFW when a key is pressed. This is the callback for keyboard events such as Alt + C and Shift + D.
    * 
    * @param window - The window that the event occurred in. This is ignored in this callback.
    * @param key - The key that was pressed. One of #GLFW_KEY_PAUSE #GLFW_KEY_LAST #GLFW_
    * @param scancode
    * @param action
    * @param mods
    */
    public static void keyCallback(long window, int key, int scancode, int action, int mods) {
        Input instance = getInstance();
        // This method is called when the key is pressed.
        if(key >= 0 && key <= GLFW_KEY_LAST) {
            // This method is called when the key is pressed.
            if(action == GLFW_PRESS) {
                instance.keyPressed[key] = true;
                instance.keyBeginPress[key] = true;
            // Release the keypress and beginPress events.
            } else if(action == GLFW_RELEASE) {
                instance.keyPressed[key] = false;
                instance.keyBeginPress[key] = false;
            }
        }
    }

    /**
    * Called when the mouse position changes. This is a callback for Input#mouse ( long double double )
    * 
    * @param window - The window that is calling this method.
    * @param xpos - The x position of the mouse. If you don't want to change the mouse position use - 1.
    * @param ypos - The y position of the mouse. If you don't want to change the mouse position use - 1
    */
    public static void mousePosCallback(long window, double xpos, double ypos) {
        Input instance = getInstance();

        // Clear the internal buffer and clear the instance.
        if(instance.captured) {
            clear();
            return;
        }

        instance.mouseX = xpos;
        instance.mouseY = ypos;
    }

    /**
    * Called by GLFW when a mouse button is pressed. This is the callback for the Input class.
    * 
    * @param window - The window that generated the event. Not used.
    * @param button - The button that was pressed. One of #GLFW_PRESS #GLFW_RELEASE or #MOUSE_PRESS.
    * @param action - The action that was performed. One of #GLFW_PRESS #GLFW_RELEASE
    * @param mods - The modifiers that were pressed
    */
    public static void mouseButtonCallback(long window, int button, int action, int mods) {
        // If the instance has captured the current instance.
        if(instance.captured) return;

        Input instance = getInstance();
        // This method is called when the mouse button is pressed.
        if(action == GLFW_PRESS) {
            ++instance.mouseButtonDown;
            // Set the mouse button to true.
            if(button < MOUSE_BUTTON_COUNT) {
                instance.mouseButtonPressed[button] = true;
                instance.mouseButtonBeginPress[button] = true;
            }
        // This method is called when the mouse button is released.
        } else if(action == GLFW_RELEASE) {
            --instance.mouseButtonDown;
            // Called when the mouse button is pressed.
            if(button < MOUSE_BUTTON_COUNT) {
                instance.mouseButtonPressed[button] = false;
            }
        }
    }

    /**
    * Called when the mouse scroll event occurs. This is a callback from Java's java. awt. Event class
    * 
    * @param window - The window that was scrolled
    * @param xOffset - The amount of horizontal scroll
    * @param yOffset - The amount of vertical scroll ( - 1 if none
    */
    public static void mouseScrollCallback(long window, double xOffset, double yOffset) {
        // If the instance has captured the current instance.
        if(instance.captured) return;

        Input instance = getInstance();
        instance.scrollX = xOffset;
        instance.scrollY = yOffset;
    }

    /**
    * Returns the X coordinate of the mouse. Note that this value is relative to the upper left corner of the screen.
    * 
    * 
    * @return the X coordinate of the mouse or Double. NaN if the mouse is not visible on the screen or if the system property cannot be
    */
    public static double mouseX() {
        return getInstance().mouseX;
    }

    /**
    * Returns the Y coordinate of the mouse. Note that this value is relative to the upper left corner of the screen.
    * 
    * 
    * @return the Y coordinate of the mouse or Double. NaN if the mouse is not visible ( never happens )
    */
    public static double mouseY() {
        return getInstance().mouseY;
    }

    /**
    * Returns the amount of pixels that have been moved in the X direction. This is useful for measuring the speed of the mouse movement in order to get a smooth effect.
    * 
    * 
    * @return the amount of pixels that have been moved in the X direction or 0 if none have been moved in the
    */
    public static double mouseDeltaX() {
        return getInstance().mouseDeltaX;
    }

    /**
    * Returns the amount of pixels that have been dragged since the last call to mouseDeltaY ().
    * 
    * 
    * @return the amount of pixels that have been dragged since the last call to mouseDeltaY (). A value of 0. 0 means no change
    */
    public static double mouseDeltaY() {
        return getInstance().mouseDeltaY;
    }

    /**
    * Returns whether or not the mouse button has been pressed. This is a convenience method for checking if the mouse button has been pressed since the last call to this method.
    * 
    * @param button - the mouse button to check. See java. awt. Button for valid values.
    * 
    * @return true if the mouse button has been pressed false otherwise. Note that a return of false does not imply that the mouse button has been pressed
    */
    public static boolean mouseButtonPressed(int button) {
        return getInstance().mouseButtonPressed[button];
    }

    /**
    * Returns true if the mouse button has begun being pressed. This is a convenience method that can be used in combination with #mouseButtonDown ( int ) and #mouseButtonUp ( int ) to determine if you want to press the button or not.
    * 
    * @param button - the mouse button to check for ( 0 - 3 )
    * 
    * @return whether the mouse button has begun being pressed or not ( false if not pressed or no button
    */
    public static boolean mouseButtonBeginPress(int button) {
        return getInstance().mouseButtonBeginPress[button];
    }

    /**
    * Returns whether or not the key is pressed. This is useful for checking if a key has been pressed and should be ignored if you don't want to know if the key is pressed
    * 
    * @param key - the key to check for
    * 
    * @return whether or not the key is pressed or not ( true = pressed false = not pressed etc.
    */
    public static boolean keyPressed(int key) {
        return getInstance().keyPressed[key];
    }

    /**
    * Returns whether or not the key was pressed at the beginning of a key sequence. This is useful for pressing keys that are used to determine when the user is in the middle of a key sequence such as Alt or Shift - JIS.
    * 
    * @param key - the key to check for. See KEY_ * constants.
    * 
    * @return whether or not the key was pressed at the beginning of a key sequence or not ( in which case false is returned
    */
    public static boolean keyBeginPress(int key) {
        return getInstance().keyBeginPress[key];
    }

    /**
    * Returns whether or not the mouse button has been pressed. This is useful for detecting when you want to press the mouse button in a JTabbedPane or the user has tapped the mouse while clicking on it.
    * 
    * 
    * @return whether or not the mouse button has been pressed or not ( false if not pressed at all in which case the method returns true
    */
    public static boolean mouseButtonPress() {
        return getInstance().mouseButtonDown != 0;
    }

    /**
    * Returns the horizontal scroll amount. This can be used to scroll in a non - standard way without affecting the user's experience.
    * 
    * 
    * @return the horizontal scroll amount in Java2D units 0. 0 to 1. 0 or Double. POSITIVE_INFINITY if the user doesn't have permission to
    */
    public static double scrollX() {
        return getInstance().scrollX;
    }
    /**
    * Returns the vertical scroll amount. This can be used to scroll in a non - standard way without affecting the user's experience.
    * 
    * 
    * @return the vertical scroll amount in Java2D units 0. 0 if none has been set or Double. POSITIVE_INFINITY if
    */
    public static double scrollY() {
        return getInstance().scrollY;
    }

    /**
    * Returns whether or not the capture is enabled. This is useful for debugging and to prevent accidental changes in the application that are in an unrecoverable state such as a file being moved to a temporary directory.
    * 
    * 
    * @return whether or not the capture is enabled ( true ) or disabled ( false ) by the test framework
    */
    public static boolean isCaptured() {
        return getInstance().captured;
    }

    /**
    * Sets whether or not the user is captured. This is useful for tests that need to know if the user has captured or not.
    * 
    * @param captured - true if the user is captured false
    */
    public static void setCaptured(boolean captured) {
        getInstance().captured = captured;
    }

    /**
    * Sets the cursor to locked or unlocked. This is useful for toggling the cursor on and off in a game or to prevent accidental flickering when the cursor is in the middle of the game
    * 
    * @param locked - true to set the cursor
    */
    public static void setCursorLocked(boolean locked) {
        Input instance = getInstance();
        // Sets the locked state of the instance.
        if(instance.locked != locked) {
            instance.locked = locked;
            glfwSetInputMode(instance.window, GLFW_CURSOR, locked ? GLFW_CURSOR_DISABLED : GLFW_CURSOR_NORMAL);
        }
//        if(locked) {
//            int[] width = new int[1];
//            int[] height = new int[1];
//            glfwGetWindowSize(instance.window, width, height);
//            glfwSetCursorPos(instance.window, (double) width[0] /2, (double) height[0] /2);
//        }
    }
}

package com.webler.goliath;

import com.webler.goliath.audio.AudioManager;
import com.webler.goliath.core.Scene;
import com.webler.goliath.core.SceneParams;
import com.webler.goliath.core.exceptions.SceneNotRegisteredException;
import com.webler.goliath.graphics.*;
import com.webler.goliath.graphics.canvas.Canvas;
import com.webler.goliath.graphics.components.Camera;
import com.webler.goliath.graphics.font.BitmapFont;
import com.webler.goliath.graphics.ui.UIElements;
import com.webler.goliath.input.Input;
import com.webler.goliath.utils.AssetPool;
import lombok.Getter;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

public class Game {
    protected static Logger logger = Logger.getLogger(Game.class.getName());
    private static final String charset = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
    private final Config config;
    private long window;
    @Getter
    private int width, height;
    private final String title;
    private final Map<String, Class<? extends Scene>> registeredScenes;
    private Scene currentScene;
    @Getter
    private String currentSceneName;
    @Getter
    private Renderer renderer;
    private ImGuiLayer imGuiLayer;
    @Getter
    private Framebuffer framebuffer;
    private final Stack<SceneChangeItem> sceneChangeStack;
    @Getter
    private Canvas canvas;
    @Getter
    private UIElements uiElements;

    public Game(Config config) {
        currentScene = null;
        this.config = config;
        width = config.getWindowWidth();
        height = config.getWindowHeight();
        title = config.getTitle();
        registeredScenes = new HashMap<>();
        sceneChangeStack = new Stack<>();
    }

    /**
    * Runs the event loop. This is the method that should be called by the EventLoop class to start the event
    */
    public void run() {

        init();
        loop();
        destroy();
    }

    /**
    * Initializes GLFW and creates the window. This should be called before any calls to #getGLUT
    */
    private void init() {
        // Resets the logger to the default logger if not configured.
        if (!config.isLoggerEnabled()) {
            LogManager.getLogManager().reset();
        }

        GLFWErrorCallback.createPrint(System.err).set();

        // Returns true if the GLFW is not initialized.
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW.");
        }

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_TRUE);

        window = GLFW.glfwCreateWindow(this.width, this.height, this.title, 0L, 0L);
        // Creates a GLFW window.
        if (window == 0L) {
            throw new IllegalStateException("Failed to create the GLFW window.");
        }

        try(
                GLFWCursorPosCallback ignored = GLFW.glfwSetCursorPosCallback(
                        window, Input::mousePosCallback);
                GLFWMouseButtonCallback ignored1 = GLFW.glfwSetMouseButtonCallback(
                        window, Input::mouseButtonCallback);
                GLFWScrollCallback ignored2 = GLFW.glfwSetScrollCallback(
                        window, Input::mouseScrollCallback);
                GLFWKeyCallback ignored3 = GLFW.glfwSetKeyCallback(
                        window, Input::keyCallback)
        ) {
            logger.info("Input callbacks initialized.");
        }

        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwSwapInterval(1);
        GLFW.glfwShowWindow(window);

        logger.info("Application started.");
        logger.info("GLFW Version: " + GLFW.glfwGetVersionString());

        GL.createCapabilities();

        AudioManager.init();

        AssetPool.addBitmapFont("default", new BitmapFont(
                new Spritesheet(
                        AssetPool.getTexture("goliath/font/pixfont-bold.png"),
                12, 16, 95, 16
            ),charset.toCharArray())
        );

        config.preload();

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        renderer = new Renderer();

        imGuiLayer = new ImGuiLayer(window);
        framebuffer = new Framebuffer(width, height);
        canvas = new Canvas(this);
        uiElements = new UIElements(canvas);

        imGuiLayer.init();
        DebugDraw.get().start();

        playScene(config.getStartScene(), config.getStartSceneParams());
    }

    /**
    * The main GLFW loop. Reads input from the input thread and passes it to the FrameBuffer
    */
    private void loop() {
        double beginTime = GLFW.glfwGetTime();
        double endTime;
        double dt = -1f;

        // This method is called by the main loop to close the specified window.
        while ( !GLFW.glfwWindowShouldClose(window) ) {

//            if(Input.keyBeginPress(GLFW.GLFW_KEY_F2)) {
//                if(cursorLocked) {
//                    GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
//                } else {
//                    GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
//                }
//                cursorLocked = !cursorLocked;
//            }

            int[] widthPointer = new int[1];
            int[] heightPointer = new int[1];

            GLFW.glfwGetWindowSize(window, widthPointer, heightPointer);

            width = widthPointer[0];
            height = heightPointer[0];

            // Set the width and height of the framebuffer.
            if(width != framebuffer.getWidth() || height != framebuffer.getHeight()) {
                framebuffer.setSize(width, height);
            }

            // Draws the current scene.
            if(currentScene != null) {
                Camera camera = currentScene.getCamera();

                DebugDraw.get().beginFrame();
                canvas.beginFrame();
                Input.beginFrame();

                currentScene.update(dt);

                glBindFramebuffer(GL_FRAMEBUFFER, framebuffer.getFbo());

                glViewport(0, 0, width, height);

                Color bg = camera.getBackgroundColor();
                glClearColor((float)bg.r, (float)bg.g, (float)bg.b, (float)bg.a);
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

                renderer.render(camera.getPVMatrix(), camera.getViewMatrix());

                DebugDraw.get().draw(currentScene.getCamera().getPVMatrix());

                canvas.endFrame();

                glBindFramebuffer(GL_FRAMEBUFFER, 0);

                currentScene.draw();

                Input.setCaptured(false);
                imGuiLayer.beginFrame();
                currentScene.imgui();
                imGuiLayer.endFrame();

                Input.endFrame();
                AudioManager.endFrame();
            }

            // This method is called by sceneChangeStack to change the scene.
            while(!sceneChangeStack.isEmpty()) {
                changeScene();
            }

            GLFW.glfwSwapBuffers(window);
            GLFW.glfwPollEvents();

            endTime = GLFW.glfwGetTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }
    }

    /**
    * Destroys GLFW and frees resources. Called when the application is terminated or when an error occurs
    */
    private void destroy() {
        imGuiLayer.destroy();
        renderer.clear();
        DebugDraw.get().destroy();
        AssetPool.destroy();
        framebuffer.destroy();
        AudioManager.destroy();

        Callbacks.glfwFreeCallbacks(window);
        GLFW.glfwDestroyWindow(window);

        GLFW.glfwTerminate();
        try(GLFWErrorCallback errorCallback = GLFW.glfwSetErrorCallback(null)) {
            // Free the error callback.
            if(errorCallback != null) {
                errorCallback.free();
            }
        }

        logger.info("Application ended.");
    }

    /**
    * Registers a scene to be used by Scene#getScene (). This is useful for subclasses of Scenes that need to be re - registered in order to avoid re - instantiating the same Scene object each time it is called.
    * 
    * @param cls - Class of the scene to be registered. Must have a public no - arg constructor
    */
    public void registerScene(Class<? extends Scene> cls) {
        registeredScenes.put(cls.getSimpleName(), cls);
    }

    /**
    * Plays a scene. This is a no - op if the scene is already playing. It does not wait for the scene to finish playing.
    * 
    * @param name - The name of the scene to play. This must be unique within the scene and not conflict with other scenes.
    * @param params - The parameters used to play the scene. This is required
    */
    public void playScene(String name, SceneParams params) {
        sceneChangeStack.add(new SceneChangeItem(name, params));
    }

    /**
    * Changes the scene and reinitializes the scene. This is called by Scene#start () and Scene
    */
    private void changeScene() {
        SceneChangeItem item = sceneChangeStack.pop();

        Class<? extends Scene> SceneClass = registeredScenes.get(item.name);
        // throw a SceneNotRegisteredException if the SceneClass is not registered.
        if(SceneClass == null) {
            throw new SceneNotRegisteredException(item.name);
        }
        try {
            Scene newScene = SceneClass.getConstructor(Game.class).newInstance(this);

            AudioManager.clear();
            renderer.clear();
            canvas.clear();

            // Destroy the current scene.
            if(currentScene != null) {
                currentScene.destroy();
            }

            Input.start(window);
            canvas.start();

            newScene.init(item.params);

            currentScene = newScene;
            currentSceneName = item.name;

            newScene.start();

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    private record SceneChangeItem(String name, SceneParams params) {
    }
}

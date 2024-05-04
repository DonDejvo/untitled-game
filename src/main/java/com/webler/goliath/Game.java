package com.webler.goliath;

import com.webler.goliath.core.Scene;
import com.webler.goliath.core.SceneParams;
import com.webler.goliath.graphics.*;
import com.webler.goliath.graphics.canvas.Canvas;
import com.webler.goliath.graphics.components.Camera;
import com.webler.goliath.graphics.font.BitmapFont;
import com.webler.goliath.graphics.ui.UIElements;
import com.webler.goliath.input.Input;
import com.webler.goliath.utils.AssetPool;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

public class Game {
    private static final String charset = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
    private final Config config;
    private long window;
    private int width, height;
    private final String title;
    private final Map<String, Class<? extends Scene>> registeredScenes;
    private Scene currentScene;
    private String currentSceneName;
    private Renderer renderer;
    private boolean cursorLocked;
    private ImGuiLayer imGuiLayer;
    private Framebuffer framebuffer;
    private final Stack<SceneChangeItem> sceneChangeStack;
    private Canvas canvas;
    private UIElements uiElements;

    public Game(Config config) {
        currentScene = null;
        this.config = config;
        width = config.windowWidth();
        height = config.windowHeight();
        title = config.title();
        registeredScenes = new HashMap<>();
        cursorLocked = false;
        sceneChangeStack = new Stack<>();
    }

    public void run() {

        init();
        loop();
        destroy();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW.");
        }

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_TRUE);

        window = GLFW.glfwCreateWindow(this.width, this.height, this.title, 0L, 0L);
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

        }

        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwSwapInterval(1);
        GLFW.glfwShowWindow(window);

        GL.createCapabilities();

        AssetPool.addBitmapFont("default", new BitmapFont(
                new Spritesheet(
                        AssetPool.getTexture("assets/font/pixfont-bold.png"),
                12, 16, 95, 16
            ),charset.toCharArray())
        );

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        renderer = new Renderer();

        imGuiLayer = new ImGuiLayer(window);
        framebuffer = new Framebuffer(width, height);
        canvas = new Canvas(this);
        uiElements = new UIElements(canvas);

        imGuiLayer.init();
        DebugDraw.get().start();

        playScene(config.startScene(), config.startSceneParams());
    }

    private void loop() {
        double beginTime = GLFW.glfwGetTime();
        double endTime;
        double dt = -1f;

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

            if(width != framebuffer.getWidth() || height != framebuffer.getHeight()) {
                framebuffer.setSize(width, height);
            }

            if(currentScene != null) {
                Camera camera = currentScene.getCamera();

                DebugDraw.get().beginFrame();
                canvas.beginFrame();

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
            }

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

    private void destroy() {
        imGuiLayer.destroy();
        renderer.destroy();
        DebugDraw.get().destroy();
        AssetPool.destroy();
        framebuffer.destroy();

        Callbacks.glfwFreeCallbacks(window);
        GLFW.glfwDestroyWindow(window);

        GLFW.glfwTerminate();
        try(GLFWErrorCallback errorCallback = GLFW.glfwSetErrorCallback(null)) {
            if(errorCallback != null) {
                errorCallback.free();
            }
        }
    }

    public void registerScene(Class<? extends Scene> cls) {
        registeredScenes.put(cls.getSimpleName(), cls);
    }

    public void playScene(String name, SceneParams params) {
        sceneChangeStack.add(new SceneChangeItem(name, params));
    }

    private void changeScene() {
        SceneChangeItem item = sceneChangeStack.pop();

        Class<? extends Scene> SceneClass = registeredScenes.get(item.name);
        if(SceneClass == null) {
            throw new RuntimeException("Scene " + item.name + " is not registered.");
        }
        try {
            Scene newScene = SceneClass.getConstructor(Game.class).newInstance(this);

            if(currentScene != null) {
                currentScene.destroy();
                renderer.destroy();
                canvas.destroy();
            }

            newScene.init(item.params);

            currentScene = newScene;
            currentSceneName = item.name;

            Input.start();
            canvas.start();
            newScene.start();

        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getCurrentSceneName() {
        return currentSceneName;
    }

    public Framebuffer getFramebuffer() {
        return framebuffer;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public UIElements getUiElements() {
        return uiElements;
    }

    private static class SceneChangeItem {
        private String name;
        private SceneParams params;

        private SceneChangeItem(String name, SceneParams params) {
            this.name = name;
            this.params = params;
        }
    }
}

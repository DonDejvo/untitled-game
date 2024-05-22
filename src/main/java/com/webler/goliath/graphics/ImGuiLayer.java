package com.webler.goliath.graphics;

import com.webler.goliath.exceptions.ResourceFormatException;
import com.webler.goliath.exceptions.ResourceNotFoundException;
import imgui.ImFontAtlas;
import imgui.ImFontConfig;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.io.InputStream;

public class ImGuiLayer {
    private final ImGuiImplGl3 imGuiImplGl3;
    private final ImGuiImplGlfw imGuiImplGlfw;
    private final long window;
    private final String defaultFontPath;

    public ImGuiLayer(long window) {
        this.window = window;
        imGuiImplGl3 = new ImGuiImplGl3();
        imGuiImplGlfw = new ImGuiImplGlfw();
        defaultFontPath = "goliath/font/segoeui.ttf";
    }

    /**
    * Initialize ImGui. This is called from GL context initialization and can be used to set up the OpenGL state
    */
    public void init() {
        ImGui.createContext();

        ImGuiIO imGuiIO = ImGui.getIO();

        ImFontAtlas fontAtlas = imGuiIO.getFonts();
        ImFontConfig fontConfig = new ImFontConfig();

        fontConfig.setGlyphRanges(fontAtlas.getGlyphRangesDefault());
        fontConfig.setPixelSnapH(true);

        InputStream is = ClassLoader.getSystemResourceAsStream(defaultFontPath);
        // Returns the font path.
        if (is == null) {
            throw new ResourceNotFoundException(defaultFontPath);
        }
        try {
            byte[] bytes = is.readAllBytes();
            fontAtlas.addFontFromMemoryTTF(bytes, 32, fontConfig);
        } catch (IOException e) {
            e.printStackTrace(System.err);
            throw new ResourceFormatException(defaultFontPath, "Could not load font at " + defaultFontPath);
        }

        fontAtlas.build();

        fontConfig.destroy();

        imGuiIO.setConfigFlags(ImGuiConfigFlags.DockingEnable);

        imGuiImplGlfw.init(window, true);
        imGuiImplGl3.init("#version 330 core");
    }

    /**
    * Begin a new frame. This is called by GLFW when it has finished drawing the frame ( s )
    */
    public void beginFrame() {
        imGuiImplGlfw.newFrame();
        ImGui.newFrame();
    }

    /**
    * Called by ImGui to end the frame. This is the same as render () but does not call glBegin
    */
    public void endFrame() {
        ImGui.render();
        imGuiImplGl3.renderDrawData(ImGui.getDrawData());

        // This method will update the current context and render the current window.
        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long backupWindowPtr = GLFW.glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            GLFW.glfwMakeContextCurrent(backupWindowPtr);
        }
    }

    /**
    * Destroy the OpenGL context. This is called when the window is no longer needed and should not be called
    */
    public void destroy() {
        imGuiImplGl3.dispose();
        imGuiImplGlfw.dispose();
        ImGui.destroyContext();
    }
}

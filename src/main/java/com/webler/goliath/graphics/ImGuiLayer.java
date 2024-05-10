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
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class ImGuiLayer {
    private final ImGuiImplGl3 imGuiImplGl3;
    private final ImGuiImplGlfw imGuiImplGlfw;
    private final long window;

    public ImGuiLayer(long window) {
        this.window = window;
        imGuiImplGl3 = new ImGuiImplGl3();
        imGuiImplGlfw = new ImGuiImplGlfw();
    }

    public void init() {
        ImGui.createContext();

        ImGuiIO imGuiIO = ImGui.getIO();

        ImFontAtlas fontAtlas = imGuiIO.getFonts();
        ImFontConfig fontConfig = new ImFontConfig();

        fontConfig.setGlyphRanges(fontAtlas.getGlyphRangesDefault());
        fontConfig.setPixelSnapH(true);

        InputStream is = ClassLoader.getSystemResourceAsStream("goliath/font/segoeui.ttf");
        if (is == null) {
            throw new ResourceNotFoundException("goliath/font/segoeui.ttf");
        }
        try {
            byte[] bytes = is.readAllBytes();
            fontAtlas.addFontFromMemoryTTF(bytes, 32, fontConfig);
        } catch (IOException e) {
            throw new ResourceFormatException(e.getMessage());
        }

        fontAtlas.build();

        fontConfig.destroy();

        imGuiIO.setConfigFlags(ImGuiConfigFlags.DockingEnable);

        imGuiImplGlfw.init(window, true);
        imGuiImplGl3.init("#version 330 core");
    }

    public void beginFrame() {
        imGuiImplGlfw.newFrame();
        ImGui.newFrame();
    }

    public void endFrame() {
        ImGui.render();
        imGuiImplGl3.renderDrawData(ImGui.getDrawData());

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long backupWindowPtr = GLFW.glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            GLFW.glfwMakeContextCurrent(backupWindowPtr);
        }
    }

    public void destroy() {
        imGuiImplGl3.dispose();
        imGuiImplGlfw.dispose();
        ImGui.destroyContext();
    }
}

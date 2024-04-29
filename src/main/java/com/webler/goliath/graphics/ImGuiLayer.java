package com.webler.goliath.graphics;

import imgui.ImFontAtlas;
import imgui.ImFontConfig;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.lwjgl.glfw.GLFW;

import java.net.URISyntaxException;
import java.net.URL;
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

//        ImFontAtlas fontAtlas = imGuiIO.getFonts();
//        ImFontConfig fontConfig = new ImFontConfig();
//
//        fontConfig.setGlyphRanges(fontAtlas.getGlyphRangesDefault());
//        fontConfig.setPixelSnapH(true);
//
//        URL url = getClass().getClassLoader().getResource("assets/font/segoeui.ttf");
//        if (url == null) {
//            throw new IllegalStateException("Unable to find font atlas");
//        }
//        String fontPath = url.getPath().replaceFirst("/", "");
//        fontAtlas.addFontFromFileTTF(fontPath, 32, fontConfig);
//
//        fontAtlas.build();
//
//        fontConfig.destroy();

        imGuiIO.setFontGlobalScale(2);

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

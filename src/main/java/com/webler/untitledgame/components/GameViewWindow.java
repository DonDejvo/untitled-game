package com.webler.untitledgame.components;

import com.webler.goliath.core.Component;
import com.webler.goliath.input.Input;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Vector2d;

// TODO: This class is only for learning purpose and won't be part of the final product. Don't forget to remove!

public class GameViewWindow extends Component {
    private final ImVec2 windowPos;
    private final ImVec2 windowSize;
    private boolean hovered;

    public GameViewWindow() {
        windowPos = new ImVec2();
        windowSize = new ImVec2();
        hovered = false;
    }

    @Override
    public void start() {

    }

    @Override
    public void update(double dt) {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void imgui() {
        ImGui.begin("Game Viewport", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);

        ImGui.getWindowPos(windowPos);
        ImGui.getWindowSize(windowSize);

        ImGui.image(gameObject.getGame().getFramebuffer().getTexId(),
                gameObject.getGame().getFramebuffer().getWidth(),
                gameObject.getGame().getFramebuffer().getHeight(),
                0, 1, 1, 0);
        hovered = ImGui.isItemHovered();

        Input.setCaptured(!hovered);

        ImGui.end();
    }

    public ImVec2 getWindowPos() {
        return windowPos;
    }

    public ImVec2 getWindowSize() {
        return windowSize;
    }

    public boolean isHovered() {
        return hovered;
    }

    public Vector2d getMousePos() {
        double offsetX = windowPos.x;
        double offsetY = windowPos.y + 40;
        double screenX = Input.mouseX();
        double screenY = Input.mouseY();
        return gameObject.getScene().getCamera().getWorldPosFromScreenCoords(screenX, screenY, offsetX, offsetY);
    }
}

package com.webler.untitledgame.components;

import com.webler.goliath.core.Component;
import com.webler.goliath.graphics.Color;
import com.webler.goliath.graphics.DebugDraw;
import com.webler.goliath.graphics.components.Camera;
import com.webler.goliath.math.Rect;
import com.webler.untitledgame.editor.EditorConfig;
import org.joml.Vector3d;

public class GridLines extends Component {
    private EditorConfig editorConfig;

    public GridLines(EditorConfig editorConfig) {
        this.editorConfig = editorConfig;
    }

    @Override
    public void start() {

    }

    @Override
    public void update(double dt) {
        Camera camera = gameObject.getScene().getCamera();

        Rect viewport = camera.getViewport();
        Vector3d cameraPos = camera.getEntity().transform.position;

        double halfVW = viewport.width * 0.5;
        double halfVH = viewport.height * 0.5;

        double firstX = Math.floor((cameraPos.x - halfVW) / editorConfig.gridWidth()) * editorConfig.gridWidth();
        double firstY = Math.floor((cameraPos.y - halfVH) / editorConfig.gridHeight()) * editorConfig.gridHeight();

        int numCols = (int) (viewport.width / editorConfig.gridWidth());
        int numRows = (int) (viewport.height / editorConfig.gridHeight());
        Color color = new Color(1, 1, 1, 0.5);

        for (int i = 0; i <= numCols; ++i) {
            DebugDraw.get().addLine(
                    new Vector3d(firstX + editorConfig.gridWidth() * i, -halfVH + cameraPos.y, 0),
                    new Vector3d(firstX + editorConfig.gridWidth() * i, halfVH + cameraPos.y, 0),
                    color);
        }

        for (int i = 0; i <= numRows; ++i) {
            DebugDraw.get().addLine(
                    new Vector3d(-halfVW + cameraPos.x, firstY + i * editorConfig.gridHeight(), 0),
                    new Vector3d(halfVW + cameraPos.x, firstY + i * editorConfig.gridHeight(), 0),
                    color);
        }
    }

    @Override
    public void destroy() {

    }
}

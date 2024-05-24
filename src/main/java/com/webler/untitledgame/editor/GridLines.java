package com.webler.untitledgame.editor;

import com.webler.goliath.core.Component;
import com.webler.goliath.graphics.Color;
import com.webler.goliath.graphics.DebugDraw;
import com.webler.goliath.graphics.components.Camera;
import com.webler.goliath.math.Rect;
import org.joml.Vector3d;

public class GridLines extends Component {
    private final EditorConfig editorConfig;

    public GridLines(EditorConfig editorConfig) {
        this.editorConfig = editorConfig;
    }

    /**
    * Called when the server is started. This is where we start the web server and the server's state is maintained
    */
    @Override
    public void start() {

    }

    /**
    * Updates the DebugDraw to reflect the current position. This is called every frame by the game engine.
    * 
    * @param dt - time since the last update in seconds ( ignored
    */
    @Override
    public void update(double dt) {
        Camera camera = gameObject.getScene().getCamera();

        Rect viewport = camera.getViewport();
        Vector3d cameraPos = camera.getGameObject().transform.position;

        double halfVW = viewport.width * 0.5;
        double halfVH = viewport.height * 0.5;

        double firstX = Math.floor((cameraPos.x - halfVW) / editorConfig.gridWidth()) * editorConfig.gridWidth();
        double firstY = Math.floor((cameraPos.y - halfVH) / editorConfig.gridHeight()) * editorConfig.gridHeight();

        int numCols = (int) (viewport.width / editorConfig.gridWidth());
        int numRows = (int) (viewport.height / editorConfig.gridHeight());
        Color color = new Color(1, 1, 1, 0.5);

        // Draw the grid of the editor.
        for (int i = 0; i <= numCols; ++i) {
            DebugDraw.get().addLine(
                    new Vector3d(firstX + editorConfig.gridWidth() * i, -halfVH + cameraPos.y, 0),
                    new Vector3d(firstX + editorConfig.gridWidth() * i, halfVH + cameraPos.y, 0),
                    color);
        }

        // Add a line to the debug draw.
        for (int i = 0; i <= numRows; ++i) {
            DebugDraw.get().addLine(
                    new Vector3d(-halfVW + cameraPos.x, firstY + i * editorConfig.gridHeight(), 0),
                    new Vector3d(halfVW + cameraPos.x, firstY + i * editorConfig.gridHeight(), 0),
                    color);
        }
    }

    /**
    * Called when the component is no longer needed. This is the place to do any cleanup that needs to be done
    */
    @Override
    public void destroy() {

    }
}

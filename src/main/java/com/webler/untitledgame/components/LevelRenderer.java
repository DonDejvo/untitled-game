package com.webler.untitledgame.components;

import com.webler.goliath.core.Component;
import com.webler.goliath.graphics.Mesh;
import com.webler.goliath.input.Input;
import com.webler.untitledgame.level.geometry.LevelGeometry;
import com.webler.untitledgame.scenes.LevelParams;
import org.joml.Vector4d;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_P;

public class LevelRenderer extends Component {
    private final Level level;
    private Mesh mesh;

    public LevelRenderer(Level level) {
        this.level = level;
    }

    /**
    * Called when the player starts. Initializes the level and adds it to the game object's renderer
    */
    @Override
    public void start() {
        LevelGeometry geometry = new LevelGeometry(level);

        mesh = new Mesh(geometry);
        mesh.getColor().mul(new Vector4d(0.75, 0.75, 0.75, 1.0));
        gameObject.getGame().getRenderer().add(mesh);

        level.buildLevel();
    }

    /**
    * Updates the mesh and plays the LevelEditorScene if the user presses the GLFW_KEY_P
    * 
    * @param dt - time since last update
    */
    @Override
    public void update(double dt) {
        mesh.getModelMatrix().set(gameObject.transform.getMatrix());
        // This method is called when the user presses the key presses the LevelEditorScene.
        if(Input.keyPressed(GLFW_KEY_P)) {
            getGameObject().getGame().playScene("LevelEditorScene", new LevelParams(level.getPath()));
        }
    }

    /**
    * Removes the mesh from the game. This is called when the object is no longer needed to render the
    */
    @Override
    public void destroy() {
        gameObject.getGame().getRenderer().remove(mesh);
    }

}

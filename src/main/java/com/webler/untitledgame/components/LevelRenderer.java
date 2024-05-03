package com.webler.untitledgame.components;

import com.webler.goliath.core.Component;
import com.webler.goliath.core.GameObject;
import com.webler.goliath.graphics.Mesh;
import com.webler.goliath.input.Input;
import com.webler.goliath.utils.AssetPool;
import com.webler.untitledgame.level.geometry.LevelGeometry;
import com.webler.untitledgame.scenes.LevelParams;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_P;

public class LevelRenderer extends Component {
    private final Level level;
    private Mesh mesh;

    public LevelRenderer(Level level) {
        this.level = level;
    }

    @Override
    public void start() {
        LevelGeometry geometry = new LevelGeometry(level,
                AssetPool.getTexture("assets/tiles/ceiling.png").getTexId(),
                AssetPool.getTexture("assets/tiles/wall.png").getTexId(),
                AssetPool.getTexture("assets/tiles/wall.png").getTexId());

        mesh = new Mesh(geometry);
        gameObject.getGame().getRenderer().add(mesh);

        level.buildLevel();
    }

    @Override
    public void update(double dt) {
        mesh.getModelMatrix().set(gameObject.transform.getMatrix());
        if(Input.keyPressed(GLFW_KEY_P)) {
            GameObject levelObject = getEntity().getScene().getEntityByName("Level");
            getEntity().getGame().playScene("LevelEditorScene", new LevelParams(levelObject.getComponent(Level.class, "Level").getPath()));
        }
    }

    @Override
    public void destroy() {
        gameObject.getGame().getRenderer().remove(mesh);
    }

}

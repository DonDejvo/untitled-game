package com.webler.goliath.graphics.components;

import com.webler.goliath.graphics.Color;
import com.webler.goliath.graphics.Geometry;
import com.webler.goliath.core.Component;
import com.webler.goliath.graphics.Mesh;
import lombok.Getter;
import lombok.Setter;

public class MeshRenderer extends Component {
    private Mesh mesh;
    @Setter
    @Getter
    private Color color;
    @Getter
    private final Geometry geometry;

    public MeshRenderer(Geometry geometry) {
        this.geometry = geometry;
        this.color = Color.WHITE;
    }

    /**
    * Creates and adds the mesh to the game's renderer. Called when the object is started and is ready to be played
    */
    @Override
    public void start() {
        mesh = new Mesh(geometry);
        gameObject.getGame().getRenderer().add(mesh);
    }

    /**
    * Updates the model. This is called every frame to update the model. You can override this in your own implementation if you want to do something other than update the model and / or offset the model by a certain amount.
    * 
    * @param dt - Time since the last update in seconds ( ignored
    */
    @Override
    public void update(double dt) {
        mesh.getModelMatrix().set(gameObject.transform.getMatrix());
        mesh.getModelMatrix().translate(offset);
        mesh.getColor().set(color.r, color.g, color.b, color.a);
    }

    /**
    * Removes the mesh from the game. This is called when the object is no longer needed to render the
    */
    @Override
    public void destroy() {
        gameObject.getGame().getRenderer().remove(mesh);
    }

}

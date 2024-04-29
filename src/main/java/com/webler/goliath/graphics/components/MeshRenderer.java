package com.webler.goliath.graphics.components;

import com.webler.goliath.graphics.Color;
import com.webler.goliath.graphics.Geometry;
import com.webler.goliath.utils.AssetPool;
import com.webler.goliath.core.Component;
import com.webler.goliath.graphics.Mesh;

public class MeshRenderer extends Component {
    private Mesh mesh;
    private Color color;
    private final Geometry geometry;

    public MeshRenderer(Geometry geometry) {
        this.geometry = geometry;
        this.color = Color.WHITE;
    }

    @Override
    public void start() {
        mesh = new Mesh(geometry);
        gameObject.getGame().getRenderer().add(mesh);
    }

    @Override
    public void update(double dt) {
        mesh.getModelMatrix().set(gameObject.transform.getMatrix());
        mesh.getColor().set(color.r, color.g, color.b, color.a);
    }

    @Override
    public void destroy() {
        gameObject.getGame().getRenderer().remove(mesh);
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}

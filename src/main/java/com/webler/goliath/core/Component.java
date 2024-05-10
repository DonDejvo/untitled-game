package com.webler.goliath.core;

import org.joml.Vector3d;

public abstract class Component {
    protected GameObject gameObject;
    public final Vector3d offset;

    public Component() {
        offset = new Vector3d();
    }

    public abstract void start();

    public abstract void update(double dt);

    public abstract void destroy();

    public void imgui() {}

    public GameObject getGameObject() {
        return gameObject;
    }

    public void setEntity(GameObject gameObject) {
        this.gameObject = gameObject;
    }

    public <T extends Component> T getComponent(Class<T> cls, String name) {
        return gameObject.getComponent(cls, name);
    }

    public Vector3d getOffsetPosition() {
        return new Vector3d(offset)
                .mul(gameObject.transform.scale)
                .rotate(gameObject.transform.rotation)
                .add(gameObject.transform.position);
    }
}

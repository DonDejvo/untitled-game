package com.webler.goliath.core;

// TODO: Add comments
public abstract class Component {
    protected GameObject gameObject;

    public abstract void start();

    public abstract void update(double dt);

    public abstract void destroy();

    public void imgui() {}

    public GameObject getEntity() {
        return gameObject;
    }

    public void setEntity(GameObject gameObject) {
        this.gameObject = gameObject;
    }

    public <T extends Component> T getComponent(Class<T> cls, String name) {
        return gameObject.getComponent(cls, name);
    }
}

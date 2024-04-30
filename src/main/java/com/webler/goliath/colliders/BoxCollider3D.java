package com.webler.goliath.colliders;

import com.webler.goliath.core.Component;
import org.joml.Vector3d;

public class BoxCollider3D extends Component {
    private Vector3d size;

    public BoxCollider3D(Vector3d size) {
        this.size = size;
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

    public Vector3d getSize() {
        return size;
    }

    public void setSize(Vector3d size) {
        this.size = size;
    }

    public Vector3d getCenter() {
        return getOffsetPosition();
    }

    public Vector3d getMin() {
        Vector3d halfSize = new Vector3d(size.x / 2, size.y / 2, size.z / 2);
        return getOffsetPosition().sub(halfSize);
    }

    public Vector3d getMax() {
        Vector3d halfSize = new Vector3d(size.x / 2, size.y / 2, size.z / 2);
        return getOffsetPosition().add(halfSize);
    }

    public boolean collidesWith(BoxCollider3D other) {
        Vector3d min = getMin();
        Vector3d max = getMax();
        Vector3d otherMin = other.getMin();
        Vector3d otherMax = other.getMax();
        return (max.x - otherMin.x) * (min.x - otherMax.x) < 0 &&
                (max.y - otherMin.y) * (min.y - otherMax.y) < 0 &&
                (max.z - otherMin.z) * (min.z - otherMax.z) < 0;
    }
}

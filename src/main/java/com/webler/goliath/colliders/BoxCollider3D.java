package com.webler.goliath.colliders;

import com.webler.goliath.core.Component;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3d;

@Setter
@Getter
public class BoxCollider3D extends Component {
    private Vector3d size;

    public BoxCollider3D(Vector3d size) {
        this.size = size;
    }

    @Override
    public void start() {

    }

    /**
    * Updates the progress bar. This is called every frame to indicate the progress of the animation. The time in seconds since the last call to update () is given by dt
    * 
    * @param dt - the time since the last
    */
    @Override
    public void update(double dt) {

    }

    @Override
    public void destroy() {

    }

    /**
    * Returns the center of the ellipse. Note that this is equivalent to getOffsetPosition (). The center will be the same as this ellipse's offset position.
    * 
    * 
    * @return the center of the ellipse as a Vector3d object with x y width height and z properties set
    */
    public Vector3d getCenter() {
        return getOffsetPosition();
    }

    /**
    * Returns the position of the minimum point of the bounding box. Note that this is a copy of the offset position so you can modify it in place without affecting the size of the bounding box.
    * 
    * 
    * @return the position of the minimum point of the bounding box in world coordinates ( center of the box is 0
    */
    public Vector3d getMin() {
        Vector3d halfSize = new Vector3d(size.x / 2, size.y / 2, size.z / 2);
        return getOffsetPosition().sub(halfSize);
    }

    /**
    * Returns the maximum position of the camera. Note that this is a bounding box not a bounding box.
    * 
    * 
    * @return the maximum position of the camera in world coordinates ( not normalized ). If the camera is empty it returns Vector3d.
    */
    public Vector3d getMax() {
        Vector3d halfSize = new Vector3d(size.x / 2, size.y / 2, size.z / 2);
        return getOffsetPosition().add(halfSize);
    }

    /**
    * Checks if this box collides with the other box. This is used to determine if a collision is occurring between two boxes
    * 
    * @param other - the box to check against
    * 
    * @return true if there is a collision false if there is no collision or the box is collided with
    */
    public boolean collidesWith(BoxCollider3D other) {
        Vector3d min = getMin();
        Vector3d max = getMax();
        Vector3d otherMin = other.getMin();
        Vector3d otherMax = other.getMax();
        return (max.x - otherMin.x) * (min.x - otherMax.x) < 0 &&
                (max.y - otherMin.y) * (min.y - otherMax.y) < 0 &&
                (max.z - otherMin.z) * (min.z - otherMax.z) < 0;
    }

    /**
    * Returns true if the box contains the point. This is useful for determining if a point lies inside the box
    * 
    * @param point - the point to check for containment in the box. It must be normalized to the range [ 0 1 ] before being checked.
    * 
    * @return whether or not the box contains the point or not. False is returned if the point is outside
    */
    public boolean contains(Vector3d point) {
        Vector3d min = getMin();
        Vector3d max = getMax();
        return (max.x - point.x) * (min.x - point.x) < 0 &&
                (max.y - point.y) * (min.y - point.y) < 0 &&
                (max.z - point.z) * (min.z - point.z) < 0;
    }
}

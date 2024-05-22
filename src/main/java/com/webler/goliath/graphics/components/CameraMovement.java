package com.webler.goliath.graphics.components;

import com.webler.goliath.core.Component;
import com.webler.goliath.input.Input;
import com.webler.goliath.math.MathUtils;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3d;

import static org.lwjgl.glfw.GLFW.*;

@Setter
@Getter
public class CameraMovement extends Component {
    private  Camera camera;
    private boolean enabled;
    private double speed;
    private double yaw, pitch;

    public CameraMovement(Camera camera, double speed) {
        this.camera = camera;
        this.speed = speed;
        this.enabled = true;
    }

    /**
    * Called when the server is started. This is where we start the web server and the server's state is maintained
    */
    @Override
    public void start() {

    }

    /**
    * Updates the camera. This is called every frame to perform a frame update. The method can be overridden in subclasses to provide custom update behavior
    * 
    * @param dt - time since the last
    */
    @Override
    public void update(double dt) {
        // Returns true if the plugin is enabled.
        if(!enabled) return;

        // This method is called when the mouse button is pressed.
        if(Input.mouseButtonPress()) {
            yaw += Input.mouseDeltaX() * -0.005;
            pitch = MathUtils.clamp(pitch - Input.mouseDeltaY() * -0.005, -1.5, 1.5);
        }

        camera.direction.set(0, 0, 1);
        camera.direction.rotateX(pitch);
        camera.direction.rotateY(yaw + Math.PI / 2);

        Vector3d moveVec = new Vector3d();

        // Move camera direction to camera. direction
        if(Input.keyPressed(GLFW_KEY_W)) {
            moveVec.add(camera.direction);
        }
        // Move vector to camera direction
        if(Input.keyPressed(GLFW_KEY_S)) {
            moveVec.sub(camera.direction);
        }
        // Move the camera to the right of the camera direction
        if(Input.keyPressed(GLFW_KEY_A)) {
            moveVec.add(new Vector3d(camera.direction.x, 0, camera.direction.z).rotateY(Math.PI / 2));
        }
        // Move the camera to the right of the camera direction.
        if(Input.keyPressed(GLFW_KEY_D)) {
            moveVec.sub(new Vector3d(camera.direction.x, 0, camera.direction.z).rotateY(Math.PI / 2));
        }
        // Normalize the moveVec to the nearest square of the moveVec.
        if(moveVec.lengthSquared() > 0) {
            moveVec.normalize();
        }
        moveVec.mul(speed);

        camera.getGameObject().transform.position.add(new Vector3d(moveVec).mul(dt));
    }

    /**
    * Called when the component is no longer needed. This is the place to do any cleanup that needs to be done
    */
    @Override
    public void destroy() {

    }

}

package com.webler.untitledgame.components;

import com.webler.goliath.core.Component;
import com.webler.goliath.core.GameObject;
import com.webler.goliath.core.SceneParams;
import com.webler.goliath.graphics.ui.UIElements;
import com.webler.goliath.input.Input;
import com.webler.untitledgame.scenes.TestParams;
import org.joml.Math;
import org.joml.Vector3d;

import static org.lwjgl.glfw.GLFW.*;

// TODO: This class is only for learning purpose and won't be part of the final product. Don't forget to remove!

public class HeroController extends Component {
    public final Vector3d direction;
    private final Vector3d velocity;
    private final Vector3d acceleration;
    private double pitch;
    private double yaw;
    public double speed;
    public double friction;
    public double maxPitch;
    private int turning = 0;

    public HeroController(float speed, float friction) {
        direction = new Vector3d(0, 0, 1);
        velocity = new Vector3d();
        acceleration = new Vector3d();
        this.speed = speed;
        this.friction = friction;
        pitch = 0;
        yaw = 0;
        maxPitch = (float)Math.PI * 0.5f;
    }

    @Override
    public void start() {

    }

    @Override
    public void update(double dt) {
        handleInput();
        if(turning == -1) {
            yaw += dt * 2;
        } else if(turning == 1) {
            yaw -= dt * 2;
        }
        direction.set(Math.sin(yaw), Math.sin(pitch), Math.cos(yaw));
        velocity.add(new Vector3d(acceleration).mul(dt));
        velocity.y -= dt * 10;
        Vector3d frameDecceleration = new Vector3d(
                velocity.x * friction,
                0,
                velocity.z * friction)
                .mul(dt);
        velocity.sub(frameDecceleration);
        Vector3d frameVelocity = new Vector3d(velocity).mul(dt);
        gameObject.transform.position.add(frameVelocity);
        if(gameObject.transform.position.y < 2) {
            gameObject.transform.position.y = 2;
            velocity.y = 0;
        }

        UIElements ui = gameObject.getGame().getUiElements();

        ui.begin(0, 0, 720, 240);
        ui.label("Welcome eternal wanderer", 0, 0);
        ui.label(String.format("velocity: %.2f, %.2f", velocity.x, velocity.z), 0, 50);
        ui.label("Press P to switch scenes", 0, 100);
        ui.end();

        ui.begin(200, 800, 1200, 320);
        ui.label("This is a label.", 0, 0);
        if(ui.button("Click Me!", 400, 0)) {
            System.out.println("You clicked me!");
            velocity.y = 10;
        }
        ui.textBlock("Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Donec iaculis gravida nulla. Mauris dictum facilisis augue.", 0, 50, 1200);
        ui.end();
    }

    @Override
    public void destroy() {

    }

    private void handleInput() {
        //yaw -= Input.mouseDeltaX() * 0.005;
        //pitch -= Input.mouseDeltaY() * 0.005;
        if(Input.keyPressed(GLFW_KEY_A)) {
            turning = -1;
        } else if(Input.keyPressed(GLFW_KEY_D)) {
            turning = 1;
        } else {
            turning = 0;
        }
        //pitch = Math.signum(pitch) * Math.min(Math.abs(pitch), maxPitch);
        Vector3d force = new Vector3d();
        if(Input.keyPressed(GLFW_KEY_W)) {
            force.z = 1;
        } else if(Input.keyPressed(GLFW_KEY_S)) {
            force.z = -1;
        }
        if(Input.keyPressed(GLFW_KEY_Q)) {
            force.x = 1;
        } else if(Input.keyPressed(GLFW_KEY_E)) {
            force.x = -1;
        }
        if(force.lengthSquared() > 0) {
            force.rotateY(yaw).normalize().mul(speed);
        }
        acceleration.set(force);
    }
}

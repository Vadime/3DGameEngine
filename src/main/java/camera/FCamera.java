package camera;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import window.Window;
import engine.MouseInput;
import util.Transformation;

import static org.lwjgl.glfw.GLFW.*;
/**
 * This is the First Person Controller
 */
public class FCamera extends ICamera {

    public FCamera(Vector3f position, Vector3f rotation, float cam_speed, float mouse_sensitivity) {
        super(position, rotation, cam_speed, mouse_sensitivity);
    }

    public void input(Window window) {
        velocity.set(0, 0, 0);
        if (window.isKeyPressed(GLFW_KEY_W)) {
            velocity.z = -cam_speed;
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            velocity.z = cam_speed;
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            velocity.x = -cam_speed;
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            velocity.x = cam_speed;
        }
        if (window.isKeyPressed(GLFW_KEY_Z)) {
            velocity.y = -cam_speed;
        } else if (window.isKeyPressed(GLFW_KEY_X)) {
            velocity.y = cam_speed;
        }
    }

    public void update(MouseInput mouseInput) {
        Vector2f rotVec = mouseInput.getDisplVec();
        if (mouseInput.isLeftButtonPressed()) {
            rotation.x += rotVec.x * mouse_sensitivity;
            rotation.y += rotVec.y * mouse_sensitivity;
        }
        if (velocity.z != 0 | velocity.x != 0 | velocity.y != 0) {
            position.x -= velocity.z * Math.sin(Math.toRadians(rotation.y)) + velocity.x * Math.sin(Math.toRadians(rotation.y - 90));
            position.z += velocity.z * Math.cos(Math.toRadians(rotation.y)) + velocity.x * Math.cos(Math.toRadians(rotation.y - 90));
            position.y += velocity.y;
        }
        Transformation.updateGenericViewMatrix(position, rotation, viewMatrix);
    }

    public Vector3f getPosition() {
        return position;
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    public Vector3f getRotation() {
        return rotation;
    }

}
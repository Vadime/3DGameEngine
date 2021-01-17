package camera;

import engine.MouseInput;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import window.Window;

/**
 * Camera
 */
public abstract class ICamera {

    public final Vector3f position, rotation;
    public final Matrix4f viewMatrix;
    public final Vector3f velocity;
    public final float cam_speed, mouse_sensitivity;

    public ICamera(Vector3f position, Vector3f rotation, float cam_speed, float mouse_sensitivity) {
        this.position = position;
        this.rotation = rotation;
        viewMatrix = new Matrix4f();
        velocity = new Vector3f(0, 0, 0);
        this.cam_speed = cam_speed;
        this.mouse_sensitivity = mouse_sensitivity;
    }

    public abstract void input(Window window);

    public abstract void update(MouseInput mouseInput);

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

}

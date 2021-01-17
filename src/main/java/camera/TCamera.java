package camera;

import engine.MouseInput;
import org.joml.Vector3f;
import window.Window;

/**
 * This is the Third Person Controller
 */

public class TCamera extends ICamera {

    public TCamera(Vector3f position, Vector3f rotation, float cam_speed, float mouse_sensitivity) {
        super(position, rotation, cam_speed, mouse_sensitivity);
    }

    @Override
    public void input(Window window) {

    }

    @Override
    public void update(MouseInput mouseInput) {

    }
}

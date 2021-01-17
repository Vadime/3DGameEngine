package engine;

import window.*;

public abstract class GameEngine implements Runnable {

    public final Window window;
    public final MouseInput mouseInput;

    public GameEngine(String windowTitle, int width, int height) {
        window = new Window(windowTitle, width, height);
        mouseInput = new MouseInput();
    }

    @Override
    public void run() {
        try {
            window.init();
            mouseInput.init(window, false);
            init();
            while (!window.windowShouldClose()) {
                mouseInput.input(window);
                input();
                window.addTime();
                while (window.isReadyForUpdate()) {
                    update();
                    window.clearUpdateTime();
                }
                render();
                window.update();
            }
        } catch (Exception excp) {
            excp.printStackTrace();
        } finally {
            cleanup();
        }
    }

    public abstract void init();

    public abstract void input();

    public abstract void update();

    public abstract void render();

    public abstract void cleanup();


}

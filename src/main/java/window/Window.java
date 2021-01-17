package window;

import org.joml.Matrix4f;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    public boolean cullFace, showTriangles, showFps, compatibleProfile, antialiasing, frustumCulling, vSync;

    private final String title;

    private int width, height;

    private long windowHandle;

    private boolean resized;

    public static final float FOV = (float) Math.toRadians(60.0f), Z_NEAR = 0.01f, Z_FAR = 1000.f;
    private final Matrix4f projectionMatrix;

    public double lastLoopTime;
    private double lastFps;
    private int fps;
    float accumulator = 0f;
    public float interval = 1f / 30;

    public Window(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.resized = false;
        projectionMatrix = new Matrix4f();
    }

    public void init() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        if (compatibleProfile)
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_COMPAT_PROFILE);
        else {
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
            glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        }
        boolean maximized = false;
        if (width == 0 || height == 0) {
            width = 270;
            height = 480;
            glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
            maximized = true;
        }
        windowHandle = glfwCreateWindow(width, height, title, NULL, NULL);
        if (windowHandle == NULL)
            throw new RuntimeException("Failed to create the GLFW window");
        glfwSetFramebufferSizeCallback(windowHandle, (window, width, height) -> {
            this.width = width;
            this.height = height;
            this.setResized(true);
        });
        glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true);
        });

        if (maximized)
            glfwMaximizeWindow(windowHandle);
        else {
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            glfwSetWindowPos(windowHandle, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);
        }
        glfwMakeContextCurrent(windowHandle);
        if (vSync)
            glfwSwapInterval(1);
        glfwShowWindow(windowHandle);
        GL.createCapabilities();
        glClearColor(0, 0, 0, 0);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_STENCIL_TEST);
        if (showTriangles)
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        if (cullFace) {
            glEnable(GL_CULL_FACE);
            glCullFace(GL_BACK);
        }
        if (antialiasing)
            glfwWindowHint(GLFW_SAMPLES, 4);
        lastLoopTime = getTime();
        lastFps = getTime();
        fps = 0;
    }

    private double getTime() {
        return System.nanoTime() / 1_000_000_000.0;
    }

    float getElapsedTime() {
        double time = getTime();
        float elapsedTime = (float) (time - lastLoopTime);
        lastLoopTime = time;
        return elapsedTime;
    }

    double getLastLoopTime() {
        return lastLoopTime;
    }

    void sync() {
        try {
            while (getTime() < getLastLoopTime() + 1f / 75)
                Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void updateFPS(Window window) {
        if (window.showFps && getLastLoopTime() - lastFps > 1) {
            lastFps = getLastLoopTime();
            window.setWindowTitle(window.getTitle() + " - " + fps + " FPS");
            fps = 0;
        }
        fps++;
    }

    public void addTime() {
        accumulator += getElapsedTime();
    }

    public boolean isReadyForUpdate() {
        return accumulator >= interval;
    }

    public void clearUpdateTime() {
        accumulator -= interval;
    }

    public void restoreState() {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_STENCIL_TEST);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        if (cullFace) {
            glEnable(GL_CULL_FACE);
            glCullFace(GL_BACK);
        }
    }

    public long getWindowHandle() {
        return windowHandle;
    }

    public String getWindowTitle() {
        return title;
    }

    public void setWindowTitle(String title) {
        glfwSetWindowTitle(windowHandle, title);
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public void updateProjectionMatrix() {
        glViewport(0, 0, width, height);
        projectionMatrix.setPerspective(FOV, (float) width / (float) height, Z_NEAR, Z_FAR);
    }

    public static Matrix4f updateProjectionMatrix(Matrix4f matrix, int width, int height) {
        float aspectRatio = (float) width / (float) height;
        return matrix.setPerspective(FOV, aspectRatio, Z_NEAR, Z_FAR);
    }

    public void setClearColor(float r, float g, float b, float alpha) {
        glClearColor(r, g, b, alpha);
    }

    public boolean isKeyPressed(int keyCode) {
        return glfwGetKey(windowHandle, keyCode) == GLFW_PRESS;
    }

    public boolean windowShouldClose() {
        return glfwWindowShouldClose(windowHandle);
    }

    public String getTitle() {
        return title;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isResized() {
        return resized;
    }

    public void setResized(boolean resized) {
        this.resized = resized;
    }

    public void update() {
        updateFPS(this);
        glfwSwapBuffers(windowHandle);
        glfwPollEvents();
        if (!vSync)
            sync();
    }

}

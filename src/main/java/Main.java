import anim.AnimGameItem;
import anim.Animation;
import assimp.AnimMeshesLoader;
import assimp.StaticMeshesLoader;
import camera.FCamera;
import engine.*;
import lights.DirectionalLight;
import model.GameItem;
import org.joml.Vector3f;
import particles.ParticleShader;
import shadow.ShadowRenderer;
import sky.SkyBox;
import sky.SkyBoxShader;
import util.FrustumCullingFilter;
import weather.Fog;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;

public class Main extends GameEngine {

    public static void main(String... args) {
        new Main().run();
    }

    private final FCamera FCamera;
    private Scene scene;
    private Animation animation;

    private ShadowRenderer shadowRenderer;
    private SceneShader sceneShader;
    private SkyBoxShader skyBoxShader;
    private ParticleShader particleShader;
    private FrustumCullingFilter frustumFilter;

    public Main() {
        super("3D Game Engine", 0, 0);
        window.cullFace = true;
        window.showFps = true;
        window.compatibleProfile = true;
        window.antialiasing = true;
        window.frustumCulling = true;
        window.vSync = true;
        FCamera = new FCamera(new Vector3f(-1, 3, 4), new Vector3f(15, 390, 0), 0.4f, 0.2f);
    }

    public void init() {
        skyBoxShader = new SkyBoxShader();
        sceneShader = new SceneShader();
        particleShader = new ParticleShader();
        shadowRenderer = new ShadowRenderer();
        frustumFilter = new FrustumCullingFilter();
        scene = new Scene();
        GameItem terrain = new GameItem(StaticMeshesLoader.load("models/plane/plane.obj", "models/plane"));
        terrain.setScale(10.0f);
        AnimGameItem animItem = AnimMeshesLoader.loadAnimGameItem("models/player/player.fbx", "models/player");
        animItem.setScale(0.05f);
        animation = animItem.getCurrentAnimation();
        scene.setGameItems(new GameItem[]{animItem, terrain});
        scene.setRenderShadows(true);
        scene.setFog(new Fog(true, new Vector3f(0.5f, 0.5f, 0.5f), 0.02f));
        SkyBox skyBox = new SkyBox("models/skybox/skybox.obj", "models/skybox/skybox.png");
        skyBox.setScale(100f);
        scene.setSkyBox(skyBox);
        scene.setAmbientLight(new Vector3f(0.3f, 0.3f, 0.3f));
        scene.setSkyBoxLight(new Vector3f(1.0f, 1.0f, 1.0f));
        scene.setDirectionalLight(new DirectionalLight(new Vector3f(1, 1, 1), new Vector3f(0, 0.5f, 0.5f), 1));
    }

    public void input() {
        FCamera.input(window);
    }

    public void update() {
        if (animation != null) animation.nextFrame(window.lastLoopTime);
        FCamera.update(mouseInput);
        if (window.frustumCulling) {
            frustumFilter.updateFrustum(window.getProjectionMatrix(), FCamera.getViewMatrix());
            frustumFilter.filter(scene.getGameMeshes());
            frustumFilter.filter(scene.getGameInstancedMeshes());
        }

        if (scene.isRenderShadows()) shadowRenderer.render(window, scene, FCamera);
    }

    public void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

        window.updateProjectionMatrix();
        sceneShader.renderScene(window, shadowRenderer, FCamera, scene);
        skyBoxShader.renderSkyBox(window, FCamera, scene);
        particleShader.renderParticles(window, FCamera, scene);
    }

    public void cleanup() {
        shadowRenderer.cleanup();
        skyBoxShader.cleanup();
        sceneShader.cleanup();
        particleShader.cleanup();
        scene.cleanup();
    }

}

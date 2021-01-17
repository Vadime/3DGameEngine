package engine;

import anim.AnimGameItem;
import anim.AnimatedFrame;
import camera.FCamera;
import lights.DirectionalLight;
import lights.PointLight;
import lights.SpotLight;
import model.GameItem;
import model.InstancedMesh;
import model.Mesh;
import model.Texture;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import shader.ShaderProgram;
import shadow.ShadowCascade;
import shadow.ShadowRenderer;
import util.Transformation;
import util.Utils;
import window.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL13.GL_TEXTURE2;

public class SceneShader extends ShaderProgram {

    private static final int MAX_POINT_LIGHTS = 5;
    private static final int MAX_SPOT_LIGHTS = 5;
    private final List<GameItem> filteredItems;

    public SceneShader() {
        super();
        createVertexShader(Utils.loadResource("/shaders/scene_vertex.vs"));
        createFragmentShader(Utils.loadResource("/shaders/scene_fragment.fs"));
        link();
        createUniform("viewMatrix");
        createUniform("projectionMatrix");
        createUniform("texture_sampler");
        createUniform("normalMap");
        createMaterialUniform("material");
        createUniform("specularPower");
        createUniform("ambientLight");
        createPointLightListUniform("pointLights", MAX_POINT_LIGHTS);
        createSpotLightListUniform("spotLights", MAX_SPOT_LIGHTS);
        createDirectionalLightUniform("directionalLight");
        createFogUniform("fog");
        for (int i = 0; i < ShadowRenderer.NUM_CASCADES; i++) {
            createUniform("shadowMap_" + i);
        }
        createUniform("orthoProjectionMatrix", ShadowRenderer.NUM_CASCADES);
        createUniform("modelNonInstancedMatrix");
        createUniform("lightViewMatrix", ShadowRenderer.NUM_CASCADES);
        createUniform("cascadeFarPlanes", ShadowRenderer.NUM_CASCADES);
        createUniform("renderShadow");
        createUniform("jointsMatrix");
        createUniform("isInstanced");
        createUniform("numCols");
        createUniform("numRows");
        createUniform("selectedNonInstanced");
        filteredItems = new ArrayList<>();
    }

    public void renderScene(Window window, ShadowRenderer shadowRenderer, FCamera FCamera, Scene scene) {
        bind();
        Matrix4f viewMatrix = FCamera.getViewMatrix();
        Matrix4f projectionMatrix = window.getProjectionMatrix();
        setUniform("viewMatrix", viewMatrix);
        setUniform("projectionMatrix", projectionMatrix);
        List<ShadowCascade> shadowCascades = shadowRenderer.getShadowCascades();
        for (int i = 0; i < ShadowRenderer.NUM_CASCADES; i++) {
            ShadowCascade shadowCascade = shadowCascades.get(i);
            setUniform("orthoProjectionMatrix", shadowCascade.getOrthoProjMatrix(), i);
            setUniform("cascadeFarPlanes", ShadowRenderer.CASCADE_SPLITS[i], i);
            setUniform("lightViewMatrix", shadowCascade.getLightViewMatrix(), i);
        }
        renderLights(viewMatrix, scene);
        setUniform("fog", scene.getFog());
        setUniform("texture_sampler", 0);
        setUniform("normalMap", 1);
        int start = 2;
        for (int i = 0; i < ShadowRenderer.NUM_CASCADES; i++) {
            setUniform("shadowMap_" + i, start + i);
        }
        setUniform("renderShadow", scene.isRenderShadows() ? 1 : 0);
        renderNonInstancedMeshes(scene, shadowRenderer);
        renderInstancedMeshes(scene, shadowRenderer, viewMatrix);
        unbind();
    }

    private void renderNonInstancedMeshes(Scene scene, ShadowRenderer shadowRenderer) {
        setUniform("isInstanced", 0);
        Map<Mesh, List<GameItem>> mapMeshes = scene.getGameMeshes();
        for (Mesh mesh : mapMeshes.keySet()) {
            setUniform("material", mesh.getMaterial());
            Texture text = mesh.getMaterial().getTexture();
            if (text != null) {
                setUniform("numCols", text.getNumCols());
                setUniform("numRows", text.getNumRows());
            }
            shadowRenderer.bindTextures(GL_TEXTURE2);
            mesh.renderList(mapMeshes.get(mesh), (GameItem gameItem) -> {
                setUniform("selectedNonInstanced", gameItem.isSelected() ? 1.0f : 0.0f);
                Matrix4f modelMatrix = Transformation.buildModelMatrix(gameItem);
                setUniform("modelNonInstancedMatrix", modelMatrix);
                if (gameItem instanceof AnimGameItem) {
                    AnimGameItem animGameItem = (AnimGameItem) gameItem;
                    AnimatedFrame frame = animGameItem.getCurrentAnimation().getCurrentFrame();
                    setUniform("jointsMatrix", frame.getJointMatrices());
                }
            });
        }
    }

    private void renderInstancedMeshes(Scene scene, ShadowRenderer shadowRenderer, Matrix4f viewMatrix) {
        setUniform("isInstanced", 1);
        Map<InstancedMesh, List<GameItem>> mapMeshes = scene.getGameInstancedMeshes();
        for (InstancedMesh mesh : mapMeshes.keySet()) {
            Texture text = mesh.getMaterial().getTexture();
            if (text != null) {
                setUniform("numCols", text.getNumCols());
                setUniform("numRows", text.getNumRows());
            }
            setUniform("material", mesh.getMaterial());
            filteredItems.clear();
            for (GameItem gameItem : mapMeshes.get(mesh)) {
                if (gameItem.isInsideFrustum()) {
                    filteredItems.add(gameItem);
                }
            }
            shadowRenderer.bindTextures(GL_TEXTURE2);
            mesh.renderListInstanced(filteredItems, viewMatrix);
        }
    }

    private void renderLights(Matrix4f viewMatrix, Scene scene) {
        setUniform("ambientLight", scene.getAmbientLight());
        setUniform("specularPower", 10f);
        PointLight[] pointLightList = scene.getPointLightList();
        int numLights = pointLightList != null ? pointLightList.length : 0;
        for (int i = 0; i < numLights; i++) {
            PointLight currPointLight = new PointLight(pointLightList[i]);
            Vector3f lightPos = currPointLight.getPosition();
            Vector4f aux = new Vector4f(lightPos, 1);
            aux.mul(viewMatrix);
            lightPos.x = aux.x;
            lightPos.y = aux.y;
            lightPos.z = aux.z;
            setUniform("pointLights", currPointLight, i);
        }
        SpotLight[] spotLightList = scene.getSpotLightList();
        numLights = spotLightList != null ? spotLightList.length : 0;
        for (int i = 0; i < numLights; i++) {
            SpotLight currSpotLight = new SpotLight(spotLightList[i]);
            Vector4f dir = new Vector4f(currSpotLight.getConeDirection(), 0);
            dir.mul(viewMatrix);
            currSpotLight.setConeDirection(new Vector3f(dir.x, dir.y, dir.z));
            Vector3f lightPos = currSpotLight.getPointLight().getPosition();
            Vector4f aux = new Vector4f(lightPos, 1);
            aux.mul(viewMatrix);
            lightPos.x = aux.x;
            lightPos.y = aux.y;
            lightPos.z = aux.z;
            setUniform("spotLights", currSpotLight, i);
        }
        DirectionalLight currDirLight = new DirectionalLight(scene.getDirectionalLight());
        Vector4f dir = new Vector4f(currDirLight.getDirection(), 0);
        dir.mul(viewMatrix);
        currDirLight.setDirection(new Vector3f(dir.x, dir.y, dir.z));
        setUniform("directionalLight", currDirLight);
    }

}

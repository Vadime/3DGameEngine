package sky;

import camera.FCamera;
import engine.Scene;
import model.Mesh;
import org.joml.Matrix4f;
import shader.ShaderProgram;
import util.Transformation;
import util.Utils;
import window.Window;

public class SkyBoxShader extends ShaderProgram {

    public SkyBoxShader() {
        super();
        createVertexShader(Utils.loadResource("/shaders/sb_vertex.vs"));
        createFragmentShader(Utils.loadResource("/shaders/sb_fragment.fs"));
        link();
        createUniform("projectionMatrix");
        createUniform("modelViewMatrix");
        createUniform("texture_sampler");
        createUniform("ambientLight");
        createUniform("colour");
        createUniform("hasTexture");
    }

    public void renderSkyBox(Window window, FCamera FCamera, Scene scene) {
        SkyBox skyBox = scene.getSkyBox();
        if (skyBox != null) {
            bind();
            setUniform("texture_sampler", 0);
            Matrix4f projectionMatrix = window.getProjectionMatrix();
            setUniform("projectionMatrix", projectionMatrix);
            Matrix4f viewMatrix = FCamera.getViewMatrix();
            float m30 = viewMatrix.m30();
            viewMatrix.m30(0);
            float m31 = viewMatrix.m31();
            viewMatrix.m31(0);
            float m32 = viewMatrix.m32();
            viewMatrix.m32(0);
            Mesh mesh = skyBox.getMesh();
            Matrix4f modelViewMatrix = Transformation.buildModelViewMatrix(skyBox, viewMatrix);
            setUniform("modelViewMatrix", modelViewMatrix);
            setUniform("ambientLight", scene.getSkyBoxLight());
            setUniform("colour", mesh.getMaterial().getAmbientColour());
            setUniform("hasTexture", mesh.getMaterial().isTextured() ? 1 : 0);
            mesh.render();
            viewMatrix.m30(m30);
            viewMatrix.m31(m31);
            viewMatrix.m32(m32);
            unbind();
        }
    }

}

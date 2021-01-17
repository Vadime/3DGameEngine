package shadow;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import util.Utils;
import window.Window;
import model.*;
import shader.ShaderProgram;

public class ShadowTestRenderer {

    private ShaderProgram testShaderProgram;

    private Mesh quadMesh;

    public void init(Window window) {
        setupTestShader();
    }

    private void setupTestShader() {
        testShaderProgram = new ShaderProgram();
        testShaderProgram.createVertexShader(Utils.loadResource("/shaders/test_vertex.vs"));
        testShaderProgram.createFragmentShader(Utils.loadResource("/shaders/test_fragment.fs"));
        testShaderProgram.link();

        for (int i = 0; i < ShadowRenderer.NUM_CASCADES; i++) {
            testShaderProgram.createUniform("texture_sampler[" + i + "]");
        }

        //quadMesh = StaticMeshesLoader.load("models/quad.obj", "textures/grassblock.png")[0];
    }

    public void renderTest(ShadowBuffer shadowMap) {
        testShaderProgram.bind();

        testShaderProgram.setUniform("texture_sampler[0]", 0);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, shadowMap.getDepthMapTexture().getIds()[0]);

        quadMesh.render();

        testShaderProgram.unbind();
    }

    public void cleanup() {
        if (testShaderProgram != null) {
            testShaderProgram.cleanup();
        }
    }
}

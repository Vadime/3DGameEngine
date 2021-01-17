package particles;

import camera.FCamera;
import engine.Scene;
import model.InstancedMesh;
import model.Texture;
import org.joml.Matrix4f;
import shader.ShaderProgram;
import util.Utils;
import window.Window;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glDepthMask;

public class ParticleShader extends ShaderProgram {


    public ParticleShader() {
        super();
        createVertexShader(Utils.loadResource("/shaders/particles_vertex.vs"));
        createFragmentShader(Utils.loadResource("/shaders/particles_fragment.fs"));
        link();

        createUniform("viewMatrix");
        createUniform("projectionMatrix");
        createUniform("texture_sampler");
        createUniform("numCols");
        createUniform("numRows");
    }

    public void renderParticles(Window window, FCamera FCamera, Scene scene) {
        bind();

        Matrix4f viewMatrix = FCamera.getViewMatrix();
        setUniform("viewMatrix", viewMatrix);
        setUniform("texture_sampler", 0);
        Matrix4f projectionMatrix = window.getProjectionMatrix();
        setUniform("projectionMatrix", projectionMatrix);

        IParticleEmitter[] emitters = scene.getParticleEmitters();
        int numEmitters = emitters != null ? emitters.length : 0;

        glDepthMask(false);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE);

        for (int i = 0; i < numEmitters; i++) {
            IParticleEmitter emitter = emitters[i];
            InstancedMesh mesh = (InstancedMesh) emitter.getBaseParticle().getMesh();

            Texture text = mesh.getMaterial().getTexture();
            setUniform("numCols", text.getNumCols());
            setUniform("numRows", text.getNumRows());

            mesh.renderListInstanced(emitter.getParticles(), true, viewMatrix);
        }

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDepthMask(true);

        unbind();
    }

}

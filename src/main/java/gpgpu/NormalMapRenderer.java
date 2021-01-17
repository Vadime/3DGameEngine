package gpgpu;


import model.Texture;
import shader.ShaderProgram;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL42.glTexStorage2D;
import static org.lwjgl.opengl.GL43.glDispatchCompute;


public class NormalMapRenderer extends ShaderProgram {

    private float strength;
    private Texture normalmap;
    private final int N;

    public NormalMapRenderer(int N) {
        super();
        createComputeShader("gpgpu/NormalMap");
        link();
        createUniform("displacementmap");
        createUniform("N");
        createUniform("normalStrength");
        this.N = N;
        normalmap = new Texture();
        normalmap.generate();
        normalmap.bind();
        normalmap.bilinearFilter();
        glTexStorage2D(GL_TEXTURE_2D, (int) (Math.log(N) / Math.log(2)), GL_RGBA32F, N, N);
    }

    public void render(Texture heightmap) {
        bind();
        glActiveTexture(GL_TEXTURE0);
        heightmap.bind();
        setUniform("displacementmap", 0);
        setUniform("N", N);
        setUniform("normalStrength", strength);
        glBindImageTexture(0, normalmap.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA32F);
        glDispatchCompute(N / 16, N / 16, 1);
        glFinish();
        normalmap.bind();
        normalmap.bilinearFilter();
        unbind();
    }

    public float getStrength() {
        return strength;
    }

    public void setStrength(float strength) {
        this.strength = strength;
    }

    public Texture getNormalmap() {
        return normalmap;
    }

    public void setNormalmap(Texture normalmap) {
        this.normalmap = normalmap;
    }
}

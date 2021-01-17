package gpgpu;

import model.Texture;
import shader.ShaderProgram;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL42.glTexStorage2D;
import static org.lwjgl.opengl.GL43.glDispatchCompute;


public class SplatMapRenderer extends ShaderProgram {

    private Texture splatmap;
    private final int N;

    public SplatMapRenderer(int N) {
        super();
        createComputeShader("gpgpu/SplatMap");
        link();
        createUniform("normalmap");
        createUniform("N");
        this.N = N;
        splatmap = new Texture();
        splatmap.generate();
        splatmap.bind();
        splatmap.bilinearFilter();
        glTexStorage2D(GL_TEXTURE_2D, (int) (Math.log(N) / Math.log(2)), GL_RGBA16F, N, N);
    }

    public void render(Texture normalmap) {
        bind();
        glActiveTexture(GL_TEXTURE0);
        normalmap.bind();
        setUniform("normalmap", 0);
        setUniform("N", N);
        glBindImageTexture(0, splatmap.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
        glDispatchCompute(N / 16, N / 16, 1);
        glFinish();
        splatmap.bind();
        splatmap.bilinearFilter();
        unbind();
    }

    public Texture getSplatmap() {
        return splatmap;
    }

    public void setSplatmap(Texture splatmap) {
        this.splatmap = splatmap;
    }

}

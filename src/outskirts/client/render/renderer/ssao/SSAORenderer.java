package outskirts.client.render.renderer.ssao;

import outskirts.client.Loader;
import outskirts.client.Outskirts;
import outskirts.client.render.Texture;
import outskirts.client.render.renderer.Renderer;
import outskirts.client.render.renderer.post.PostRenderer;
import outskirts.client.render.shader.ShaderProgram;
import outskirts.util.BitmapImage;
import outskirts.util.Colors;
import outskirts.util.Identifier;
import outskirts.util.Maths;
import outskirts.util.vector.Vector3f;
import outskirts.util.vector.Vector4f;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.*;

public class SSAORenderer extends Renderer {



    private Vector3f[] KERNEL_SAMPLES = new Vector3f[64];

    private final int TEX_RAND_TANOP_SZ = 4;
    private Texture TEX_RAND_TANOP;

    private ShaderProgram shaderSSAO = new ShaderProgram(
            new Identifier("shaders/post/post.vsh").getInputStream(),
            new Identifier("shaders/ssao/ssao.fsh").getInputStream()
    );

    private ShaderProgram shaderSSAOBlur = new ShaderProgram(
            new Identifier("shaders/post/post.vsh").getInputStream(),
            new Identifier("shaders/ssao/blur.fsh").getInputStream()
    );

    public SSAORenderer() {

        for (int i = 0;i < KERNEL_SAMPLES.length;i++) {
            Vector3f v = new Vector3f((float)Math.random() * 2f - 1f, (float)Math.random() * 2f - 1f, (float)Math.random());
            v.normalize();
            v.scale((float)Math.random());

            float scale = i / (float)KERNEL_SAMPLES.length;  // make the samples tends near to the center.
            scale = Maths.lerp(scale*scale, 0.1f, 1.0f);
            v.scale(scale);

            KERNEL_SAMPLES[i] = v;
        }

        BitmapImage texRandTS = new BitmapImage(TEX_RAND_TANOP_SZ, TEX_RAND_TANOP_SZ);
        for (int i = 0;i < TEX_RAND_TANOP_SZ;i++) {
            for (int j = 0; j < TEX_RAND_TANOP_SZ; j++) {
                // actually [-1, 1] of xy in shader/applicate. just int RGB cant store negatives.
                texRandTS.setPixel(i, j, Colors.toRGBA(new Vector4f((float)Math.random(), (float)Math.random(), 0, 1)));
//                texRandTS.setRGB(i, j, Colors.toARGB(new Vector4f(1,1,1,1)));
            }
        }
        TEX_RAND_TANOP = Loader.loadTexture(texRandTS);

        shaderSSAO.useProgram();
        shaderSSAO.setInt("gPositionDepth", 0);
        shaderSSAO.setInt("gNormal", 1);
        shaderSSAO.setInt("texRandTan", 2);

        shaderSSAOBlur.useProgram();
        shaderSSAOBlur.setInt("ssaoMap", 0);
    }

    public void renderSSAO(Texture gPositionDepth, Texture gNormal) {

        shaderSSAO.useProgram();

        float fbWidth = Outskirts.toMainFramebufferCoords(Outskirts.getWidth()), fbHeight = Outskirts.toMainFramebufferCoords(Outskirts.getHeight());
        shaderSSAO.setVector2f("texRandTanScale", fbWidth/TEX_RAND_TANOP_SZ, fbHeight/TEX_RAND_TANOP_SZ);

        for (int i = 0;i < KERNEL_SAMPLES.length;i++) {
            shaderSSAO.setVector3f("KERNEL_SAMPLES["+i+"]", KERNEL_SAMPLES[i]);
        }

        shaderSSAO.setMatrix4f("viewMatrix", Outskirts.renderEngine.getViewMatrix());
        shaderSSAO.setMatrix4f("projectionMatrix", Outskirts.renderEngine.getProjectionMatrix());

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, gPositionDepth.textureID());
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, gNormal.textureID());
        glActiveTexture(GL_TEXTURE2);
        glBindTexture(GL_TEXTURE_2D, TEX_RAND_TANOP.textureID());

        PostRenderer.doDrawQuad();
    }

    public void renderSSAOBlur(Texture ssaoMap) {

        shaderSSAOBlur.useProgram();

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, ssaoMap.textureID());

        PostRenderer.doDrawQuad();

    }

    @Override
    public ShaderProgram getShader() {
        return null;
    }
}

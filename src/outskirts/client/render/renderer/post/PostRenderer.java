package outskirts.client.render.renderer.post;

import outskirts.client.Loader;
import outskirts.client.material.Model;
import outskirts.client.material.Texture;
import outskirts.client.render.renderer.Renderer;
import outskirts.client.render.shader.ShaderProgram;
import outskirts.util.Identifier;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class PostRenderer extends Renderer {

    private ShaderProgram shader = new ShaderProgram(
            new Identifier("shaders/post/post.vsh").getInputStream(),
            new Identifier("shaders/post/post.fsh").getInputStream()
    );

    public static float exposure = 1;

    public void render(Texture postcfb) {

        shader.useProgram();

        shader.setFloat("exposure", exposure);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, postcfb.textureID());

        doDrawQuad();
    }

    public static void doDrawQuad() {

        glBindVertexArray(QUAD_FULLNDC.vaoID());

        glDrawElements(GL_TRIANGLES, QUAD_FULLNDC.vertexCount(), GL_UNSIGNED_INT, 0);

        glBindVertexArray(0);
    }


    @Override
    public ShaderProgram getShader() {
        return shader;
    }


    public static final Model QUAD_FULLNDC = Loader.loadModel(2,new float[] {
            -1, 1,
            -1, -1,
            1, -1,
            1, -1,
            1, 1,
            -1, 1
    }, 2,new float[] {
            0, 1,
            0, 0,
            1, 0,
            1, 0,
            1, 1,
            0, 1
    });
}

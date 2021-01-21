package outskirts.client.render.renderer.test;

import outskirts.client.Loader;
import outskirts.client.render.Model;
import outskirts.client.render.renderer.Renderer;
import outskirts.client.render.shader.ShaderProgram;
import outskirts.util.Identifier;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class TestRenderer extends Renderer {

    private ShaderProgram shader = new ShaderProgram(
            new Identifier("shaders/test/test.vsh").getInputStream(),
            new Identifier("shaders/test/test.fsh").getInputStream()
    );

    private Model model = Loader.loadModel(3, new float[]{
            0, 0, 0,
            1, 0, 0,
            .5f, 1, 0
    });

    public void doRender() {

        shader.useProgram();

        glBindVertexArray(model.vaoID());

        glDrawElements(GL_TRIANGLES, model.vertexCount(), GL_UNSIGNED_INT, 0);

    }

    @Override
    public ShaderProgram getShader() {
        return shader;
    }
}

package outskirts.client.render.renderer.debug.test;

import outskirts.client.Loader;
import outskirts.client.render.Model;
import outskirts.client.render.renderer.Renderer;
import outskirts.client.render.shader.ShaderProgram;
import outskirts.util.Identifier;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class DebugTestRenderer extends Renderer {

    private ShaderProgram shader = new ShaderProgram(
            new Identifier("shaders/debug/test/test.vsh").getInputStream(),
            new Identifier("shaders/debug/test/test.fsh").getInputStream(),
            new Identifier("shaders/debug/test/test.gsh").getInputStream()
    );

    private Model model = Loader.loadModel(3, new float[]{
            .5f, .5f, 0,
            -.5f, .5f, 0,
            -.5f, -.5f, 0,
            .5f, -.5f, 0
    });

    public void doRender() {

        shader.useProgram();

        glBindVertexArray(model.vaoID());

        glDrawElements(GL_POINTS, model.vertexCount(), GL_UNSIGNED_INT, 0);

    }

}

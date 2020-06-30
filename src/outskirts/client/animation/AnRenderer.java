package outskirts.client.animation;

import outskirts.client.Outskirts;
import outskirts.client.animation.animated.AnimatedModel;
import outskirts.client.render.renderer.Renderer;
import outskirts.client.render.shader.ShaderProgram;
import outskirts.util.Identifier;
import outskirts.util.vector.Matrix4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class AnRenderer extends Renderer {

    private ShaderProgram shader = new ShaderProgram(
            new Identifier("shaders/animation.vsh").getInputStream(),
            new Identifier("shaders/animation.fsh").getInputStream()
    );

    private static String[] uniform_jointTransform = createUniformNameArray("jointTransforms[%s]", 50);

    public void render(AnimatedModel model) {

        shader.useProgram();
        Matrix4f[] jointTrans = model.getJointTransforms();
        for (int i = 0; i < jointTrans.length;i++) {
            shader.setMatrix4f(uniform_jointTransform[i], jointTrans[i]);
        }
        shader.setMatrix4f("projectionMatrix", Outskirts.renderEngine.getProjectionMatrix());
        shader.setMatrix4f("viewMatrix", Outskirts.renderEngine.getViewMatrix());

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, model.texture.textureID());

        glBindVertexArray(model.model.vaoID());

        glDrawElements(GL_TRIANGLES, model.model.vertexCount(), GL_UNSIGNED_INT, 0);

    }

    @Override
    public ShaderProgram getShader() {
        return shader;
    }
}

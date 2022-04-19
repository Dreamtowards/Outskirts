package outskirts.client.render.renderer.sky;

import outskirts.client.Outskirts;
import outskirts.client.render.renderer.EntityRenderer;
import outskirts.client.render.renderer.ModelRenderer;
import outskirts.client.render.renderer.Renderer;
import outskirts.client.render.shader.ShaderProgram;
import outskirts.util.Identifier;
import outskirts.util.Maths;
import outskirts.util.vector.Matrix3f;
import outskirts.util.vector.Matrix4f;
import outskirts.util.vector.Vector3f;
import outskirts.util.vector.Vector4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static outskirts.client.render.isoalgorithm.sdf.Vectors.vec3;
import static outskirts.client.render.renderer.ModelRenderer.*;

public class SkyRenderer extends Renderer {

    private ShaderProgram shader = new ShaderProgram(
            new Identifier("shaders/sky/sky.vsh").getInputStream(),
            new Identifier("shaders/sky/sky.fsh").getInputStream());

    public void render(float height, float rotX, Vector3f col) {

        shader.useProgram();

        shader.setMatrix4f("projectionMatrix", Outskirts.renderEngine.getProjectionMatrix());
        shader.setMatrix4f("viewMatrix", Outskirts.renderEngine.getViewMatrix());

        shader.setMatrix4f("modelMatrix", Maths.createModelMatrix(
                vec3(Outskirts.getCamera().getPosition()).add(0, height, 0),
                vec3(10000, 1, 10000),
                Matrix3f.rotate(rotX, Vector3f.UNIT_Z, null), null));

        shader.setVector3f("CamPos", Outskirts.getCamera().getPosition());
        shader.setVector3f("bgColor", vec3(EntityRenderer.bgColor));
        shader.setVector3f("fillColor", col);

        glBindVertexArray(M_HORIZ_PLANE.vaoID());


        glDrawElements(GL_TRIANGLES, M_HORIZ_PLANE.vertexCount(), GL_UNSIGNED_INT, 0);

        glBindVertexArray(0);
    }
}

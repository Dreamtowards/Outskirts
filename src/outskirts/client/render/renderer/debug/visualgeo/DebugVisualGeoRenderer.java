package outskirts.client.render.renderer.debug.visualgeo;

import outskirts.client.Outskirts;
import outskirts.client.render.Model;
import outskirts.client.render.renderer.Renderer;
import outskirts.client.render.shader.ShaderProgram;
import outskirts.entity.Entity;
import outskirts.util.Identifier;
import outskirts.util.Maths;
import outskirts.util.vector.Matrix3f;
import outskirts.util.vector.Vector3f;
import outskirts.util.vector.Vector4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class DebugVisualGeoRenderer extends Renderer {

    private ShaderProgram shader = new ShaderProgram(
            new Identifier("shaders/debug/visualgeo/visualgeo.vsh").getInputStream(),
            new Identifier("shaders/debug/visualgeo/visualgeo.fsh").getInputStream(),
            new Identifier("shaders/debug/visualgeo/visualgeo.gsh").getInputStream());

    public final Vector4f normColor = new Vector4f();
    public final Vector4f borderColor = new Vector4f();

    public void render(Model model, Vector3f pos, Matrix3f rot) {

        shader.useProgram();

        shader.setVector4f("normColor", normColor);
        shader.setVector4f("borderColor", borderColor);

        shader.setMatrix4f("projectionMatrix", Outskirts.renderEngine.getProjectionMatrix());
        shader.setMatrix4f("viewMatrix", Outskirts.renderEngine.getViewMatrix());

        shader.setMatrix4f("modelMatrix", Maths.createModelMatrix(pos, Vector3f.ONE, rot, null));

        glBindVertexArray(model.vaoID());

        glDrawElements(GL_TRIANGLES, model.vertexCount(), GL_UNSIGNED_INT, 0);

        glBindVertexArray(0);
    }

    public void render(Entity entity) {
        render(entity.getModel(), entity.position(), entity.rotation());
    }

}

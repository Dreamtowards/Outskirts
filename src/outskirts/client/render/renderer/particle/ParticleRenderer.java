package outskirts.client.render.renderer.particle;

import outskirts.client.Loader;
import outskirts.client.Outskirts;
import outskirts.client.render.Model;
import outskirts.client.particle.Particle;
import outskirts.client.render.renderer.Renderer;
import outskirts.client.render.shader.ShaderProgram;
import outskirts.util.Identifier;
import outskirts.util.Maths;
import outskirts.util.vector.Matrix3f;
import outskirts.util.vector.Matrix4f;
import outskirts.util.vector.Vector3f;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class ParticleRenderer extends Renderer {

    private ShaderProgram shader = new ShaderProgram(
            new Identifier("shaders/particle/particle.vsh").getInputStream(),
            new Identifier("shaders/particle/particle.fsh").getInputStream()
    );

    public void render(List<Particle> particles) {

        shader.useProgram();

        shader.setMatrix4f("viewMatrix", Outskirts.renderEngine.getViewMatrix());
        shader.setMatrix4f("projectionMatrix", Outskirts.renderEngine.getProjectionMatrix());

        Matrix4f tmpModelMatrix = new Matrix4f();
        Matrix3f tmpLookAtMatrix = new Matrix3f();

        for (Particle particle : particles) {
            Vector3f camToParticle = Vector3f.sub(particle.getPosition(), Outskirts.getCamera().getPosition(), null);
            if (camToParticle.lengthSquared() == 0)
                camToParticle.set(Vector3f.UNIT_X);
            camToParticle.normalize();

            Maths.lookAt(camToParticle, Vector3f.UNIT_Y, tmpLookAtMatrix);
            shader.setMatrix4f("modelMatrix", Maths.createModelMatrix(particle.getPosition(), new Vector3f(10,1,1), tmpLookAtMatrix, tmpModelMatrix));

            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, particle.getTexture().textureID());

            glBindVertexArray(MODEL_RECT.vaoID());

            glDrawElements(GL_TRIANGLES, MODEL_RECT.vertexCount(), GL_UNSIGNED_INT, 0);
        }
    }

    @Override
    public ShaderProgram getShader() {
        return shader;
    }



    /**
     * in 3D coords, align center.
     *
     * 1     0,5
     * +-----+
     * |  /  |
     * +-----+
     * 2,3   4
     * todo: needs normals.?
     */
    private static Model MODEL_RECT = Loader.loadModel(3,new float[] {
             0.5f, 0.5f, 0f,
            -0.5f, 0.5f, 0f,
            -0.5f,-0.5f, 0f,
            -0.5f,-0.5f, 0f,
             0.5f,-0.5f, 0f,
             0.5f, 0.5f, 0f
    }, 2,new float[] {
            1, 1,
            0, 1,
            0, 0,
            0, 0,
            1, 0,
            1, 1
    });
}

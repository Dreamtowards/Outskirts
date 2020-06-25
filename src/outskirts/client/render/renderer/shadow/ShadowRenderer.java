package outskirts.client.render.renderer.shadow;

import outskirts.client.Outskirts;
import outskirts.client.material.Texture;
import outskirts.client.render.glw.Framebuffer;
import outskirts.client.render.renderer.Renderer;
import outskirts.client.render.shader.ShaderProgram;
import outskirts.entity.Entity;
import outskirts.util.Identifier;
import outskirts.util.Maths;
import outskirts.util.vector.Matrix4f;
import outskirts.util.vector.Vector3f;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class ShadowRenderer extends Renderer {

    private static final int SHADOW_RESOLUTION = 1024*2;
    private static final int SHADOW_SIZE = 100;

    private Framebuffer depthMapFBO;
    {
        depthMapFBO = Framebuffer.glfGenFramebuffer()
                .bindPushFramebuffer()
                .resize(SHADOW_RESOLUTION, SHADOW_RESOLUTION)
                .attachTextureColor(0)
                .attachTextureDepth()
                .checkFramebufferStatus();

        glDrawBuffer(GL_NONE);  // not render any Color Data. just depthMap.
        glReadBuffer(GL_NONE);

        depthMapFBO.popFramebuffer();
    }

    private Matrix4f shadowspaceMatrix = new Matrix4f();  // a.k.a LightSpace. LightProjectionMatrix * LightViewMatrix. Worldpoint->ProjectionPoint.

    private ShaderProgram shader = new ShaderProgram(
            new Identifier("shaders/shadow_depthmap.vsh").getInputStream(),
            new Identifier("shaders/shadow_depthmap.fsh").getInputStream()
    );

    public void renderDepthMap(List<Entity> entities) {
//        if (depthMapFBO == null)
//            init();

        depthMapFBO.bindPushFramebuffer();

        glClear(GL_DEPTH_BUFFER_BIT);

        Matrix4f orthoproj = Maths.createOrthographicProjectionMatrix(SHADOW_SIZE, SHADOW_SIZE, 1000f, null);
        Matrix4f viewmat = Maths.createViewMatrix(Outskirts.getCamera().getPosition(), Maths.lookAt(Outskirts.getWorld().lights.get(0).getDirection(), Vector3f.UNIT_Y, null), null);
        Matrix4f.mul(orthoproj, viewmat, shadowspaceMatrix);

        shader.useProgram();
        shader.setMatrix4f("lightspaceMatrix", getShadowspaceMatrix());

//        glCullFace(GL_FRONT);

        for (Entity entity : entities) {

            shader.setMatrix4f("modelMatrix", Maths.createModelMatrix(entity.getPosition(), entity.tmp_boxSphere_scale, entity.getRotation(), null));

            glBindVertexArray(entity.getMaterial().getModel().vaoID());
            glDrawElements(GL_TRIANGLES, entity.getMaterial().getModel().vertexCount(), GL_UNSIGNED_INT, 0);
        }
//        glCullFace(GL_BACK);

        depthMapFBO.popFramebuffer();

    }

    public final Texture getDepthMapTexture() {
        return depthMapFBO.depthTexture();
    }

    public Matrix4f getShadowspaceMatrix() {
        return shadowspaceMatrix;
    }

    @Override
    public ShaderProgram getShader() {
        return shader;
    }
}
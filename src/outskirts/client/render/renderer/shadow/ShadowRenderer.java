package outskirts.client.render.renderer.shadow;

import outskirts.client.ClientSettings;
import outskirts.client.Loader;
import outskirts.client.Outskirts;
import outskirts.client.render.Texture;
import outskirts.client.render.Framebuffer;
import outskirts.client.render.renderer.Renderer;
import outskirts.client.render.shader.ShaderProgram;
import outskirts.entity.Entity;
import outskirts.util.Identifier;
import outskirts.util.Maths;
import outskirts.util.vector.Matrix4f;
import outskirts.util.vector.Vector3f;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.*;

//todo: PFC and Border-Trans
public class ShadowRenderer extends Renderer {

    private static final int SHADOW_RESOLUTION = 1024*2;
    private static final int SHADOW_SIZE = 100;

    private Framebuffer depthMapFBO = Framebuffer.glfGenFramebuffer()
                .bindPushFramebuffer()
                .resize(SHADOW_RESOLUTION, SHADOW_RESOLUTION)
                .attachTextureColor(0)
                .disableColorBuffer()  // not render any Color Data. just depthMap.
                .uExec(() -> {
                    Loader.OP_TEX_MM_filter = GL_LINEAR;  // for depthmap
                })
                .attachTextureDepth()
                .checkFramebufferStatus()
                .popFramebuffer();

    private Matrix4f shadowspaceMatrix = new Matrix4f();  // a.k.a LightSpace. LightProjectionMatrix * LightViewMatrix. Worldpoint->ProjectionPoint.

    private ShaderProgram shader = new ShaderProgram(
            new Identifier("shaders/shadow/shadow.vsh").getInputStream(),
            new Identifier("shaders/shadow/shadow.fsh").getInputStream()
    );

    private Vector3f shadowDirection = new Vector3f(-1, -1, -1).normalize();

    public void renderDepthMap(List<Entity> entities) {
//        if (depthMapFBO == null)
//            init();

//        shadowDirection.set(Outskirts.getCamera().getCameraUpdater().getDirection());

        depthMapFBO.bindPushFramebuffer();

        glClear(GL_DEPTH_BUFFER_BIT);

        Matrix4f orthoproj = Maths.createOrthographicProjectionMatrix(SHADOW_SIZE, SHADOW_SIZE, ClientSettings.FAR_PLANE, null);
        Matrix4f viewmat = Maths.createViewMatrix(Outskirts.getCamera().getPosition(), Maths.lookAt(shadowDirection, Vector3f.UNIT_Y, null), null);
        Matrix4f.mul(orthoproj, viewmat, shadowspaceMatrix);

        shader.useProgram();
        shader.setMatrix4f("lightspaceMatrix", getShadowspaceMatrix());

//        glCullFace(GL_FRONT);

        for (Entity entity : entities) {

            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, entity.getRenderPerferences().getDiffuseMap().textureID());

            shader.setMatrix4f("modelMatrix", Maths.createModelMatrix(entity.position(), entity.tmp_boxSphere_scale, entity.rotation(), null));

            glBindVertexArray(entity.getModel().vaoID());
            glDrawElements(GL_TRIANGLES, entity.getModel().vertexCount(), GL_UNSIGNED_INT, 0);
        }
//        glCullFace(GL_BACK);

        depthMapFBO.popFramebuffer();

    }

    public Vector3f getShadowDirection() {
        return shadowDirection;
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

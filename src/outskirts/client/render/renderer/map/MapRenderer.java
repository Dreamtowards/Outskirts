package outskirts.client.render.renderer.map;

import outskirts.block.Block;
import outskirts.client.ClientSettings;
import outskirts.client.Outskirts;
import outskirts.client.material.Model;
import outskirts.client.material.Texture;
import outskirts.client.render.Framebuffer;
import outskirts.client.render.renderer.Renderer;
import outskirts.client.render.shader.ShaderProgram;
import outskirts.util.Identifier;
import outskirts.util.Maths;
import outskirts.util.vector.Matrix3f;
import outskirts.util.vector.Matrix4f;
import outskirts.util.vector.Vector3f;
import outskirts.world.chunk.Chunk;
import outskirts.world.chunk.ChunkPos;

import java.awt.image.BufferedImage;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class MapRenderer extends Renderer {

    private ShaderProgram shader = new ShaderProgram(
            new Identifier("shaders/map/map.vsh").getInputStream(),
            new Identifier("shaders/map/map.fsh").getInputStream()
    );

    private Framebuffer fbChunk = Framebuffer.glfGenFramebuffer()
            .bindPushFramebuffer()
            .resize(128, 128)
            .attachTextureColor(0)
            .attachTextureDepth()
            .checkFramebufferStatus()
            .popFramebuffer();

    private Matrix3f MAT_LOOKDOWN = Maths.lookAt(new Vector3f(0, -1, -0.001f).normalize(), Vector3f.UNIT_Y, new Matrix3f());


    public BufferedImage render(Chunk chunk) {

        fbChunk.bindPushFramebuffer();
        Outskirts.renderEngine.prepare();

        shader.useProgram();

        shader.setMatrix4f("viewMatrix", Maths.createViewMatrix(new Vector3f(8, 0, 8), MAT_LOOKDOWN, null));
        shader.setMatrix4f("projectionMatrix", Maths.createOrthographicProjectionMatrix(16, 16, ClientSettings.FAR_PLANE, null));

        Model model = chunk.proxyEntity.getModel();
        glBindVertexArray(model.vaoID());

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, Block.TEXTURE_ATLAS.getAtlasTexture().textureID());

        glDrawElements(GL_TRIANGLES, model.vertexCount(), GL_UNSIGNED_INT, 0);

        glBindVertexArray(0);

        fbChunk.popFramebuffer();

        return Texture.glfGetTexImage(fbChunk.colorTextures(0));
    }

    @Override
    public ShaderProgram getShader() {
        return shader;
    }
}

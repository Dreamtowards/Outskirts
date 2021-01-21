package outskirts.client.render.renderer.skybox;

import outskirts.client.Loader;
import outskirts.client.Outskirts;
import outskirts.client.render.Texture;
import outskirts.client.render.renderer.Renderer;
import outskirts.client.render.shader.ShaderProgram;
import outskirts.init.ex.Models;
import outskirts.util.Identifier;

import java.awt.image.BufferedImage;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class SkyboxRenderer extends Renderer {

    private ShaderProgram shader = new ShaderProgram(
            new Identifier("shaders/skybox/skybox.vsh").getInputStream(),
            new Identifier("shaders/skybox/skybox.fsh").getInputStream()
    );
    public Texture textureCubemap = Loader.loadTextureCubeMap(null, new BufferedImage[]{
            Loader.loadPNG(new Identifier("textures/skybox/wide/right.jpg").getInputStream()),
            Loader.loadPNG(new Identifier("textures/skybox/wide/left.jpg").getInputStream()),
            Loader.loadPNG(new Identifier("textures/skybox/wide/top.jpg").getInputStream()),
            Loader.loadPNG(new Identifier("textures/skybox/wide/bottom.jpg").getInputStream()),
            Loader.loadPNG(new Identifier("textures/skybox/wide/front.jpg").getInputStream()),
            Loader.loadPNG(new Identifier("textures/skybox/wide/back.jpg").getInputStream())
    });

    public void render() {

        glDisable(GL_CULL_FACE);  // the box is been seen from Inner.
        glDepthFunc(GL_LEQUAL);   // the box's projection-z /NDC.z is ==1.0.(max far.) set from shader. so needs EQUAL-pass.

        shader.useProgram();
        glBindVertexArray(Models.GEO_CUBE.vaoID());

        shader.setMatrix4f("proj", Outskirts.renderEngine.getProjectionMatrix());
        shader.setMatrix4f("view", Outskirts.renderEngine.getViewMatrix());

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_CUBE_MAP, textureCubemap.textureID());

        glDrawElements(GL_TRIANGLES, Models.GEO_CUBE.vertexCount(), GL_UNSIGNED_INT, 0);

        glDepthFunc(GL_LESS);   // set back
        glEnable(GL_CULL_FACE); // set back.
    }

    @Override
    public ShaderProgram getShader() {
        return shader;
    }
}

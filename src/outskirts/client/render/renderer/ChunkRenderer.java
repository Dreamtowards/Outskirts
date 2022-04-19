package outskirts.client.render.renderer;

import outskirts.client.render.chunk.RenderChunk;
import outskirts.client.render.shader.ShaderProgram;
import outskirts.util.ResourceLocation;

public class ChunkRenderer extends Renderer {

    private ShaderProgram shader = new ShaderProgram(
            new ResourceLocation("shaders/chunk/chunk.vsh").getInputStream(),
            new ResourceLocation("shaders/chunk/chunk.fsh").getInputStream());


    public void render(RenderChunk rs) {



    }

}

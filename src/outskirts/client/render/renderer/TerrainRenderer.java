package outskirts.client.render.renderer;

import outskirts.client.Outskirts;
import outskirts.client.material.Material;
import outskirts.client.render.shader.ShaderProgram;
import outskirts.event.Event;
import outskirts.event.EventHandler;
import outskirts.event.Events;
import outskirts.event.world.terrain.TerrainLoadedEvent;
import outskirts.event.world.terrain.TerrainUnloadedEvent;
import outskirts.util.vector.Matrix4f;
import outskirts.world.terrain.Terrain;

import java.util.List;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class TerrainRenderer extends Renderer {

    public TerrainRenderer() {

        Events.EVENT_BUS.register(this);
    }

    @EventHandler
    private void onTerrainLoaded(TerrainLoadedEvent event) {
//        event.getTerrain()._update_model();
//        event.getTerrain()._update_texture();
    }

    @EventHandler
    private void onTerrainUnloaded(TerrainUnloadedEvent event) {

    }

    public void render(Iterable<Terrain> terrains) {

        getShader().useProgram();

        getShader().setMatrix4f("modelMatrix", Matrix4f.IDENTITY);

        for (Terrain terrain : terrains) {
            Material material = terrain.getMaterial();

            glBindVertexArray(material.getModel().vaoID());

            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, material.getDiffuseMap().textureID());

            drawElementsOrArrays(material.getModel());
        }

        glBindVertexArray(0);
    }

    @Override
    public ShaderProgram getShader() {
        return Outskirts.renderEngine.getEntityRenderer().getShader();
    }
}

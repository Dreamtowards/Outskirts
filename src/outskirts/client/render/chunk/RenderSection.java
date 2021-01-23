package outskirts.client.render.chunk;

import outskirts.block.Block;
import outskirts.client.render.Model;
import outskirts.entity.Entity;
import outskirts.entity.EntityStaticMesh;
import outskirts.util.vector.Vector3f;

public final class RenderSection {

    private boolean dirty;
    private EntityStaticMesh proxyentity = new EntityStaticMesh();

    public RenderSection() {
        proxyentity().getRenderPerferences().setDiffuseMap(Block.TEXTURE_ATLAS.getAtlasTexture());
    }

    public boolean isDirty() {
        return dirty;
    }
    public void markDirty() {
        dirty = true;
    }
    public void clearDirty() {
        dirty = false;
    }

    public Vector3f position() {
        return proxyentity.position();
    }

    public void setModel(Model model) {
        proxyentity.setModel(model);
    }

    public Entity proxyentity() {
        return proxyentity;
    }
}

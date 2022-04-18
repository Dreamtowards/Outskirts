package outskirts.client.render.chunk;

import outskirts.client.Outskirts;
import outskirts.client.render.Model;
import outskirts.client.render.isoalgorithm.dc.Octree;
import outskirts.entity.item.EntityStaticMesh;
import outskirts.init.MaterialTextures;
import outskirts.util.vector.Vector3f;
import outskirts.world.Chunk;

import static outskirts.client.render.isoalgorithm.sdf.Vectors.vec3;
import static outskirts.util.logging.Log.LOGGER;


public final class RenderSection {

    public boolean rebuildRequested;
    public EntityStaticMesh proxyentity = new EntityStaticMesh();

//    public Octree cachedLod;
//    public float cachedLodSize;
//    public boolean lodDirty;

//    public void computeLOD() {
//        cachedLodSize = calcShouldLodSize();
//        Octree n = Outskirts.getWorld().getOctree(position());
//        if (n==null) return;
//        cachedLod = Octree.doLOD((Octree.Internal)Octree.copy(n), cachedLodSize, vec3(0), 16f);
//        LOGGER.info("Build LOD");
//    }
//    public float calcShouldLodSize() {
//        return ChunkRenderDispatcher.calculateLodSize(position(), Outskirts.getPlayer().position());
//    }

    public void markRebuild() {
        rebuildRequested = true;
    }

    public RenderSection(Vector3f p) {
        Chunk.validateChunkPos(p);
        proxyentity.getRenderPerferences().setDiffuseMap(MaterialTextures.DIFFUSE_ATLAS.getAtlasTexture())
                                          .setNormalMap(MaterialTextures.NORMAL_ATLAS.getAtlasTexture())
                                          .setDisplacementMap(MaterialTextures.DISPLACEMENT_ATLAS.getAtlasTexture());
        proxyentity.position().set(p);
    }

    void doLoadUp() {
        Outskirts.getScheduler().addScheduledTask(() -> Outskirts.getWorld().addEntity(proxyentity));
    }

    void doUnloadDown() {
        if (Outskirts.getWorld() == null) return;
        Outskirts.getScheduler().addScheduledTask(() -> Outskirts.getWorld().removeEntity(proxyentity));
    }

    public Vector3f position() {
        return proxyentity.position();
    }

    public void setModel(Model model) {
        proxyentity.setModel(model);
    }

}

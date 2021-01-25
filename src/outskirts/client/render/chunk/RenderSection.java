package outskirts.client.render.chunk;

import outskirts.client.Outskirts;
import outskirts.client.render.Model;
import outskirts.entity.Entity;
import outskirts.entity.EntityStaticMesh;
import outskirts.init.MaterialTextures;
import outskirts.util.CollectionUtils;
import outskirts.util.vector.Vector3f;

import java.util.Arrays;

public final class RenderSection {

    public boolean dirty;
    private EntityStaticMesh proxyentity = new EntityStaticMesh();

//    private Stitcher stitcher = new Stitcher();

    public RenderSection(Vector3f p) {
        assert p.x%16==0 && p.y%16==0 && p.z%16==0;
        proxyentity.getRenderPerferences().setDiffuseMap(MaterialTextures.TEXTURE_ATLAS.getAtlasTexture());
        proxyentity.position().set(p);
    }

    void doLoadUp() {
        Outskirts.getWorld().addEntity(proxyentity);
    }

    void doUnloadDown() {
        if (Outskirts.getWorld() == null) return;
        Outskirts.getWorld().removeEntity(proxyentity);
    }

    public Vector3f position() {
        return proxyentity.position();
    }

    public void setModel(Model model) {
        proxyentity.setModel(model);
    }

//    public Stitcher stitcher() {
//        return stitcher;
//    }
//
//    public static final class Stitcher {
//
//        private EntityStaticMesh[] edgeproxy = CollectionUtils.fill(new EntityStaticMesh[3], EntityStaticMesh::new);
//        private EntityStaticMesh[] faceproxy = CollectionUtils.fill(new EntityStaticMesh[3], EntityStaticMesh::new);
//
//        public final boolean[] edgedirty = new boolean[3];
//        public final boolean[] facedirty = new boolean[3];
//
//        public void setEdgeProxyModel(int i, Model model) {
//            edgeproxy[i].setModel(model);
//        }
//
//        public void setFaceProxyModel(int i, Model model) {
//            faceproxy[i].setModel(model);
//        }
//
//    }
}

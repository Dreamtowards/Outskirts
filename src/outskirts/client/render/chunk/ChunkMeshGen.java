package outskirts.client.render.chunk;

import outskirts.client.Outskirts;
import outskirts.client.gui.debug.GuiVert3D;
import outskirts.client.render.VertexBuffer;
import outskirts.client.render.isoalgorithm.dc.DualContouring;
import outskirts.client.render.isoalgorithm.dc.Octree;
import outskirts.util.CollectionUtils;
import outskirts.util.Ref;
import outskirts.util.mx.VertexUtil;
import outskirts.util.vector.Vector3f;
import outskirts.world.World;

import java.util.List;
import java.util.function.Function;

import static outskirts.client.render.isoalgorithm.sdf.Vectors.vec3;

public class ChunkMeshGen {

    public static VertexBuffer buildModel(RenderSection rs, Ref<float[]> vertm, Function<Vector3f, Octree> octreeGet) {

        Octree node = octreeGet.apply(rs.position());
        if (node==null) return null;

        VertexBuffer vbuf = DualContouring.contouring(node);


        // Stitching. FACEs.
        Octree[] fp = new Octree[2];
        for (int i = 0;i < 3;i++) {
            // we choose stitching towards min-er. cuz miner are always exists. chunk-load is from min to max in each axis.
            Octree neib = octreeGet.apply(vec3(rs.position()).addv(i, 16));
            if (neib != null) {
                int beginfi = vbuf.positions.size();
                fp[0] = node;
                fp[1] = neib;
                DualContouring.doFaceContour(fp, i, vbuf);

                // vertex-axis-pos correct offset. because Octrees are all rel-pos.
                for (int fi = beginfi;fi < vbuf.positions.size();fi+=3) {
                    float f = vbuf.positions.get(fi+i);
                    if (f < 8) {
                        vbuf.positions.set(fi+i, 16+f);
                    }
                }
            }
        }

        // Stitching ADJACENTs.
        Octree[] ea = new Octree[4];
        for (int i = 0;i < 3;i++) {
            boolean comple = true;
            int[] adja = DualContouring.EDGE_ADJACENT[i*2];
            for (int j = 0;j < 4;j++) {
                Octree neib = octreeGet.apply(vec3(rs.position()).addScaled(16, Octree.VERT[adja[j]]));
                if (neib == null) {
                    comple = false;
                    break;
                }
                ea[j] = neib;
            }
            if (comple) {
                int beginfi = vbuf.positions.size();
                DualContouring.doEdgeContour(ea, i, vbuf);

                for (int fi = beginfi;fi < vbuf.positions.size();fi+=3) {
                    for (int j = 0;j < 3;j++) {
                        if (j == i) continue;
                        float f = vbuf.positions.get(fi+j);
                        if (f < 8) {
                            vbuf.positions.set(fi+j, 16+f);
                        }
                    }
                }
            }
        }


        vbuf.inituvnorm(false);

        VertexUtil.smoothnorm(vbuf);

        vertm.value = CollectionUtils.toArrayf((List)vbuf.verttags);
        assert vertm.value.length == vbuf.positions.size()/3;

        return vbuf;
    }

}
